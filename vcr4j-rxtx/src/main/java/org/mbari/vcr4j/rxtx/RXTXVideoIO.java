package org.mbari.vcr4j.rxtx;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import org.mbari.util.NumberUtilities;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.rs422.RS422Error;
import org.mbari.vcr4j.rs422.RS422ResponseParser;
import org.mbari.vcr4j.rs422.RS422State;
import org.mbari.vcr4j.rs422.RS422Timecode;
import org.mbari.vcr4j.rs422.RS422Userbits;
import org.mbari.vcr4j.rs422.RS422VideoIO;
import org.mbari.vcr4j.rs422.commands.CommandToBytes;
import org.mbari.vcr4j.rs422.commands.RS422ByteCommands;
import org.mbari.vcr4j.rs422.commands.RS422VideoCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2016-01-28T14:24:00
 */
public class RXTXVideoIO implements RS422VideoIO {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * For RXTX we have to put the thread to sleep VERY briefly
     * in order for the serial io to work
     */
    private static final long IO_DELAY = 10;

    /**
     * Maximum receive timeout in millisecs
     */
    private final static long RECEIVE_TIMEOUT = 40;

    private final RS422ResponseParser responseParser = new RS422ResponseParser();


    private final Subject<VideoCommand, VideoCommand> commandSubject = new SerializedSubject<>(PublishSubject.create());

    private OutputStream outputStream;    // Sends commands to VCR
    private InputStream inputStream;    // Reads responses from VCR
    private SerialPort serialPort;        // Serial port connected to VCR

    public RXTXVideoIO(String portName) {
        try {
            serialPort = openSerialPort(portName);
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to open " + portName, e);
        }

        commandSubject.subscribe(vc -> {
            if (vc.equals(RS422VideoCommands.REQUEST_USERBITS)) {
                // LUB and VUB access if very state dependant. We get around that by
                // requesting for both.
                send(RS422VideoCommands.REQUEST_LUSERBITS);
                send(RS422VideoCommands.REQUEST_VUSERBITS);
            }
            else if (vc.equals(VideoCommands.REQUEST_TIMESTAMP)
                    || vc.equals(VideoCommands.REQUEST_ELAPSED_TIME)) {
                // Do Nothing
            }
            else {
                byte[] cmd = CommandToBytes.apply(vc);
                if (!Arrays.equals(cmd, RS422ByteCommands.UNDEFINED.getBytes())) {
                    sendCommand(cmd, vc);
                }
            }

        });

    }

    public static SerialPort openSerialPort(String serialPortName)
            throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException {
        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(serialPortName);
        SerialPort serialPort = (SerialPort) portId.open("VCR", 2000);

        serialPort.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_ODD);
        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);

        // If the below is enabled you will not get any output from the VCR.
        //        serialPort.setFlowControlMode(
        //                SerialPort.FLOWCONTROL_RTSCTS_IN
        //                & SerialPort.FLOWCONTROL_RTSCTS_OUT);
        serialPort.enableReceiveTimeout((int) RECEIVE_TIMEOUT);    // Returns after waiting 190ms

        return serialPort;
    }

    /**
     * Sends a command, in the format of a byte[], to the VCR.
     * @param command The command to send to the VCR
     */
    protected synchronized void sendCommand(byte[] command, VideoCommand videoCommand) {

        // Add the checksum
        byte checksum = RS422ResponseParser.calculateChecksum(command);

        command[command.length - 1] = checksum;

        try {
            logCommand(command, videoCommand);
            outputStream.write(command);
            Thread.sleep(IO_DELAY);    // RXTX does not block serial IO correctly.
            readResponse(command, videoCommand);

        }
        catch (IOException | RXTXException e) {
            log.error("Failed to send a command to the VCR", e);
        }
        catch (InterruptedException e) {
            log.error("Thread " + Thread.currentThread().getName() + " was interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Reads the response to a command from the serial port connected to the VCR.
     * @throws IOException
     * @throws RXTXException
     * @throws InterruptedException
     */
    private synchronized void readResponse(byte[] mostRecentCommand, VideoCommand videoCommand) throws IOException, RXTXException, InterruptedException {

        // Get the command returned by the VCR
        final byte[] cmd = new byte[2];

        if (inputStream.available() > 0) {
            inputStream.read(cmd);
        }

        Thread.sleep(IO_DELAY);    // RXTX does not block serial IO correctly.

        // Extract the number of data bytes in the command block. Then
        // read the data from the serial port
        final int numDataBytes = (int) (cmd[0] & 0x0F);    // Get the number of data blocks
        byte[] data = null;

        if (numDataBytes > 0) {
            data = new byte[numDataBytes];

            if (inputStream.available() > 0) {
                inputStream.read(data);
            }
            else {
                throw new IOException("Incoming data is missing . byte[] = " + NumberUtilities.toHexString(cmd));
            }
        }

        Thread.sleep(IO_DELAY);    // RXTX does not block serial IO correctly.

        // Read the checksum that the VCR sends
        final byte[] checksum = new byte[1];

        if (inputStream.available() > 0) {
            inputStream.read(checksum);
        }
        else {
            throw new IOException("Incoming checksum is missing. cmd[] =  " + NumberUtilities.toHexString(cmd) +
                    " data[] = " + NumberUtilities.toHexString(data));
        }

        logResponse(cmd, data, checksum);

        responseParser.update(mostRecentCommand, cmd, data, checksum, Optional.of(videoCommand));


    }




    @Override
    public Subject<VideoCommand, VideoCommand> getCommandSubject() {
        return commandSubject;
    }

    @Override
    public void close() {
        log.info("Closing serial port:" + serialPort.getName());
        try {
            outputStream.close();
            inputStream.close();
            serialPort.close();
            responseParser.getStatusObservable()
                    .onNext(RS422State.STOPPED);
            responseParser.getTimecodeObservable()
                    .onNext(RS422Timecode.ZERO);
            responseParser.getStatusObservable().onCompleted();
            responseParser.getTimecodeObservable().onCompleted();
            responseParser.getErrorObservable().onCompleted();
            responseParser.getUserbitsObservable().onCompleted();
            commandSubject.onCompleted();
            serialPort = null;
        }
        catch (Exception e) {
            if (log.isErrorEnabled() && (serialPort != null)) {
                log.error("Problem occured when closing serial port communications on " + serialPort.getName());
            }
        }
    }

    @Override
    public <A extends VideoCommand> void send(A videoCommand) {
        commandSubject.onNext(videoCommand);
    }

    @Override
    public String getConnectionID() {
        return (serialPort == null) ? "Not Connected" : serialPort.getName();
    }

    @Override
    public Observable<RS422Error> getErrorObservable() {
        return responseParser.getErrorObservable();
    }

    @Override
    public Observable<RS422State> getStateObservable() {
        return responseParser.getStatusObservable();
    }

    @Override
    public Observable<RS422Timecode> getTimecodeObservable() {
        return responseParser.getTimecodeObservable();
    }

    @Override
    public Observable<RS422Userbits> getUserbitsObservable() {
        return responseParser.getUserbitsObservable();
    }

    /**
     *
     * @return An Observable for the VideoIndex. If the device is recording, the VideoIndex will
     *    also have a timestamp from the computers clock as well as a timecode. Otherwise,
     *    only a timecode will be included.
     */
    @Override
    public Observable<VideoIndex> getIndexObservable() {
        return Observable.combineLatest(responseParser.getTimecodeObservable(), responseParser.getStatusObservable(),
                (timecode, state) -> {
                    if (state.isRecording()) {
                        return new VideoIndex(Optional.of(Instant.now()), Optional.empty(), Optional.of(timecode.getTimecode()));
                    }
                    else {
                        return new VideoIndex(Optional.empty(), Optional.empty(), Optional.of(timecode.getTimecode()));
                    }
                }).distinctUntilChanged();
    }

    private void logCommand(byte[] bytes, VideoCommand videoCommand) {
        if (log.isDebugEnabled()) {
            log.debug("[0x" + NumberUtilities.toHexString(bytes) + "] >>> VCR (" + videoCommand.getName() + ")");
        }
    }

    private void logResponse(byte[] cmd, byte[] data, byte[] checksum) {
        if (log.isDebugEnabled()) {

            /*
             * Munge it all into a single byte array
             */
            int dataLength = (data == null) ? 0 : data.length;
            final byte[] c = new byte[cmd.length + dataLength + 1];

            System.arraycopy(cmd, 0, c, 0, cmd.length);

            if (data != null) {
                System.arraycopy(data, 0, c, cmd.length, data.length);
            }

            c[c.length - 1] = checksum[0];

            log.debug("[0x" + NumberUtilities.toHexString(c) + "] <<< VCR");
        }
    }


}
