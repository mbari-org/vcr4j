package org.mbari.vcr4j.jssc;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.rs422.VCRVideoIO;
import org.mbari.vcr4j.rs422.LoggerHelper;
import org.mbari.vcr4j.rs422.RS422Error;
import org.mbari.vcr4j.rs422.RS422Exception;
import org.mbari.vcr4j.rs422.RS422ResponseParser;
import org.mbari.vcr4j.rs422.RS422State;
import org.mbari.vcr4j.rs422.RS422Timecode;
import org.mbari.vcr4j.rs422.RS422Userbits;
import org.mbari.vcr4j.rs422.commands.CommandToBytes;
import org.mbari.vcr4j.rs422.commands.RS422ByteCommands;
import org.mbari.vcr4j.rs422.commands.RS422VideoCommands;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2016-02-03T15:39:00
 */
public class JSSCVideoIO implements VCRVideoIO {

    /**
     * For RXTX we have to put the thread to sleep VERY briefly
     * in order for the serial io to work
     */
    public static final long IO_DELAY = 10;
    private final long ioDelay;

    private final RS422ResponseParser responseParser = new RS422ResponseParser();
    private final Subject<VideoCommand<?>> commandSubject;

    /**
     * Maximum receive timeout in millisecs
     */
    public final static int RECEIVE_TIMEOUT = 500;

//    private final Logger log = LoggerFactory.getLogger(getClass());
    private final System.Logger log = System.getLogger(getClass().getName());
    private final LoggerHelper loggerHelper = new LoggerHelper(log);
    private SerialPort serialPort;    // Serial port connected to VCR

    public JSSCVideoIO(SerialPort serialPort, long ioDelay) {
        this.serialPort = serialPort;
        this.ioDelay = ioDelay;

        PublishSubject<VideoCommand<?>> s1 = PublishSubject.create();
        commandSubject = s1.toSerialized();

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

    @Override
    public <A extends VideoCommand<?>> void send(A videoCommand) {
        commandSubject.onNext(videoCommand);
    }

    /**
     * Sends a command, in the format of a byte[], to the VCR.
     * @param command The command to send to the VCR
     * @param videoCommand The corresponding videoCommand sent to the vcr
     */
    protected synchronized void sendCommand(byte[] command, VideoCommand<?> videoCommand) {

        // Add the checksum
        byte checksum = RS422ResponseParser.calculateChecksum(command);

        command[command.length - 1] = checksum;

        try {
            loggerHelper.logCommand(command, videoCommand);
            serialPort.writeBytes(command);
            Thread.sleep(ioDelay);    // RXTX does not block serial IO correctly.
            readResponse(command, videoCommand);
        }
        catch (SerialPortException | RS422Exception e) {
            log.log(System.Logger.Level.ERROR, "Failed to send a command to the VCR", e);
        }
        catch (InterruptedException e) {
            log.log(System.Logger.Level.ERROR, "Thread " + Thread.currentThread().getName() + " was interrupted", e);
            Thread.currentThread().interrupt();
        }
        catch (SerialPortTimeoutException e) {
            log.log(System.Logger.Level.ERROR, "Serial port timed out", e);
        }

    }

    /**
     * Reads the response to a command from the serial port connected to the VCR.
     * @param mostRecentCommand The bytes representing the last command sent to the VCR
     * @param videoCommand The command last sent to the vcr
     * @throws SerialPortException bad exception
     * @throws RS422Exception ugly exception
     * @throws InterruptedException rude exception
     * @throws SerialPortTimeoutException bored exception
     */
    protected synchronized void readResponse(byte[] mostRecentCommand, VideoCommand videoCommand)
            throws SerialPortException, RS422Exception, InterruptedException, SerialPortTimeoutException {

        // Get the command returned by the VCR
        //final byte[] cmd = new byte[2];

        final byte[] cmd = serialPort.readBytes(2, RECEIVE_TIMEOUT);

        Thread.sleep(ioDelay);    // RXTX does not block serial IO correctly.

        // Extract the number of data bytes in the command block. Then
        // read the data from the serial port
        final int numDataBytes = (int) (cmd[0] & 0x0F);    // Get the number of data blocks
        byte[] data = (numDataBytes > 0) ? serialPort.readBytes(numDataBytes, RECEIVE_TIMEOUT) : null;

        Thread.sleep(ioDelay);    // RXTX does not block serial IO correctly.

        // Read the checksum that the VCR sends
        final byte[] checksum = serialPort.readBytes(1, RECEIVE_TIMEOUT);

        loggerHelper.logResponse(cmd, data, checksum);

        responseParser.update(mostRecentCommand, cmd, data, checksum, videoCommand);

    }



    /**
     * Factory method. Use this to open a connection
     * @param portName The port name (e.g. COM1 or /dev/tty6)
     * @return A VideoIO object
     */
    public static JSSCVideoIO open(String portName) {
        try {
            SerialPort serialPort = new SerialPort(portName);
            serialPort.openPort();
            return new JSSCVideoIO(serialPort, IO_DELAY);
        }
        catch (Exception e) {
            throw new RS422Exception("Failed to open " + portName, e);
        }
    }


    @Override
    public void close() {
        log.log(System.Logger.Level.INFO, "Closing serial port:" + serialPort.getPortName());

        try {
            getCommandSubject().onComplete();
            serialPort.closePort();
            responseParser.getStatusObservable().onNext(RS422State.STOPPED);
            responseParser.getStatusObservable().onComplete();
            responseParser.getTimecodeObservable().onComplete();
            responseParser.getErrorObservable().onComplete();
            responseParser.getUserbitsObservable().onComplete();
            serialPort = null;
        }
        catch (Exception e) {
            if (log.isLoggable(System.Logger.Level.ERROR)
                    && (serialPort != null)) {
                log.log(System.Logger.Level.ERROR, "Problem occured when closing serial port communications on " + serialPort.getPortName());
            }
        }
    }

    @Override
    public String getConnectionID() {
        return null;
    }

    @Override
    public Subject<VideoCommand<?>> getCommandSubject() {
        return commandSubject;
    }

    @Override
    public Observable<RS422Error> getErrorObservable() {
        return responseParser.getErrorObservable();
    }

    /**
     *
     * @return An Observable for the VideoIndex. If the device is recording, the VideoIndex will
     *    also have a timestamp from the computers clock as well as a timecode. Otherwise,
     *    only a timecode will be included.
     */
    @Override
    public Observable<VideoIndex> getIndexObservable() {
        return Observable
                .combineLatest(responseParser.getTimecodeObservable(),
                        responseParser.getStatusObservable(),
                        (timecode, state) -> {
                            if (state.isRecording()) {
                                return new VideoIndex(Optional.of(Instant.now()),
                                        Optional.empty(),
                                        Optional.of(timecode.getTimecode()));
                            }
                            else {
                                return new VideoIndex(Optional.empty(),
                                        Optional.empty(),
                                        Optional.of(timecode.getTimecode()));
                            }
                        })
                .distinctUntilChanged();
    }


    @Override
    public Observable<RS422State> getStateObservable() {
        return responseParser.getStatusObservable();
    }

    public Observable<RS422Timecode> getTimecodeObservable() {
        return responseParser.getTimecodeObservable();
    }

    public Observable<RS422Userbits> getUserbitsObservable() {
        return responseParser.getUserbitsObservable();
    }
}
