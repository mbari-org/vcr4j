/*
 * @(#)VCR.java   2011.09.15 at 04:20:40 PDT
 *
 * Copyright 2011 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.mbari.vcr4j.purejavacomm;

import org.mbari.comm.BadPortException;
import org.mbari.util.NumberUtilities;
import org.mbari.vcr4j.IVCRReply;
import org.mbari.vcr4j.VCRAdapter;
import org.mbari.vcr4j.VCRException;
import org.mbari.vcr4j.VCRUtil;
import org.mbari.vcr4j.rs422.Command;
import org.mbari.vcr4j.rs422.VCRReply;
import org.mbari.vcr4j.rs422.VCRState;
import org.mbari.vcr4j.time.FrameRates;
import org.mbari.vcr4j.time.HMSF;
import org.mbari.vcr4j.time.Timecode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;


/**
 * A VCR that uses the purejavacomm/jna library for Native serial port access
 * @author Brian Schlining
 * @since 2011-09-15
 */
public class VCR extends VCRAdapter {

    /**
     * For RXTX we have to put the thread to sleep VERY briefly
     * in order for the serial io to work
     */
    private static final long IO_DELAY = 100;

    /**
     * Maximum receive timeout in millisecs
     */
    private final static long RECEIVE_TIMEOUT = 400;

    /** Record command */

    private static final Logger log = LoggerFactory.getLogger(VCR.class);

    private InputStream inputStream;    // Reads responses from VCR

    /**
     *     For storage of the mostRecently submitted command to the VCR. When a reply is returned by the vcr this command is updated in the vcrReplyBuffer
     */
    private byte[] mostRecentCommand;

    /**
     *     Need to hang on to the check sum. the VCRReply object needs this.
     */

    //private byte[] mostRecentCommandChecksum;
    private OutputStream outputStream;    // Sends commands to VCR
    private SerialPort serialPort;        // Serial port connected to VCR

    private final byte[] EMPTY_USERBITS = new byte[] {0, 0, 0, 0};

    /**
     * @param portName
     *
     * @throws BadPortException
     * @throws IOException
     */
    public VCR(String portName) throws BadPortException, IOException {

        // Initialize RS422 specific support classes
        vcrReply = new VCRReply();

        // Open I/O streams to the serial port
        try {
            serialPort = openSerialPort(portName);
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
        }
        catch (Exception ex) {
            throw new BadPortException("Failed to open " + portName, ex);
        }

        // Request status and time code right away
        requestTimeCode();
        requestStatus();
    }

    /** Disconnect serial port communications */
    @Override
    public void disconnect() {
        try {
            log.info("Closing serial port:" + serialPort.getName());
            outputStream.close();
            inputStream.close();
            serialPort.close();
            getVcrTimecode().timecodeProperty().set(Timecode.zero());
            ((VCRState) getVcrReply().getVcrState()).setStatus(0);
        }
        catch (Exception e) {
            if (log.isErrorEnabled() && (serialPort != null)) {
                log.error("Problem occured when closing serial port communications on " + serialPort.getName());
            }
        }

        super.disconnect();
    }

    /** Eject a tape, updates status. */
    @Override
    public void eject() {
        Command command = Command.EJECT_TAPE;
        byte[] bytes = command.getBytes();

        log(command, bytes);
        sendCommand(bytes);
        requestStatus();
    }

    /** Fast forward the tape, updates status. */
    @Override
    public void fastForward() {
        Command command = Command.FAST_FWD;
        byte[] bytes = command.getBytes();

        log(command, bytes);
        sendCommand(bytes);
        requestStatus();
    }

    /**
     * Returns a name for this connection
     *
     * @return
     */
    @Override
    public String getConnectionName() {
        return (serialPort == null) ? "Not Connected" : serialPort.getName();
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @Override
    public IVCRReply getVcrReply() {
        return vcrReply;
    }

    /**
     */
    @Override
    public void kill() {
        try {
            outputStream.close();
            inputStream.close();
            serialPort.close();
        }
        catch (Exception e) {
            // Do nothing
        }
        super.kill();
    }

    private void log(Command command, byte[] data) {
        if (log.isDebugEnabled()) {
            log.debug("VCR << [" + NumberUtilities.toHexString(data) + "] " + command.getDescription());
        }
    }

    /**
     *
     * @param serialPortName
     * @return
     *
     * @throws NoSuchPortException
     * @throws PortInUseException
     * @throws UnsupportedCommOperationException
     */
    public static SerialPort openSerialPort(String serialPortName)
            throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException {
        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(serialPortName);
        SerialPort serialPort = (SerialPort) portId.open("VCR", 2000);

        serialPort.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_ODD);
        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
        //serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_IN);
        //serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_OUT);

        // If the below is enabled you will not get any output from the VCR.
//                serialPort.setFlowControlMode(
//                        SerialPort.FLOWCONTROL_RTSCTS_IN
//                        & SerialPort.FLOWCONTROL_RTSCTS_OUT);
        serialPort.enableReceiveTimeout((int) RECEIVE_TIMEOUT);    // Returns after waiting 190ms

        return serialPort;
    }

    /**
     * Pause the tape. This is not yet implemented
     *
     */
    @Override
    public void pause() {
        // TODO 20040203 brian: Insert pause command here
        requestStatus();
    }

    /** Play the tape, updates status. */
    @Override
    public void play() {
        Command command = Command.PLAY_FWD;
        byte[] bytes = command.getBytes();
        log(command, bytes);
        sendCommand(bytes);
        requestStatus();
    }

    /**
     * Set the timecode on the VCR.
     * @param timecode a byte array representing the time code value
     */
    @Override
    public void presetTimecode(byte[] timecode) {
        Command command = Command.PRESET_TIMECODE;
        byte[] bytes = command.getBytes();
        System.arraycopy(timecode, 0, bytes, 2, timecode.length);
        log(command, bytes);
        sendCommand(bytes);
        requestStatus();
    }

    /**
     * Set the Userbits on the VCR
     *
     * @param userbits
     */
    @Override
    public void presetUserbits(byte[] userbits) {
        Command command = Command.PRESET_USERBITS;
        byte[] bytes = command.getBytes();
        System.arraycopy(userbits, 0, bytes, 2, userbits.length);
        log(command, bytes);
        sendCommand(bytes);
        requestStatus();
    }

    /**
     * Reads the response to a command from the serial port connected to the VCR.
     * @throws IOException
     * @throws VCRException
     * @throws InterruptedException
     */
    private synchronized void readResponse() throws IOException, VCRException, InterruptedException {

        // Get the command returned by the VCR
        final byte[] cmd = new byte[2];

        if (inputStream.available() > 0) {
            inputStream.read(cmd);
        }

        Thread.sleep(IO_DELAY);    // RXTX does not block serial IO correctly.

        // Extract the number of data bytes in the command block. Then
        // read the data from the serial port
        final int numDataBytes =  cmd[0] & 0x0F;    // Get the number of data blocks
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

            log.debug("VCR >> [" + NumberUtilities.toHexString(c) + "]");
        }

        ((org.mbari.vcr4j.rs422.VCRReply) vcrReply).update(mostRecentCommand, cmd, data, checksum);


    }

    /** Start recording, updates status. */
    @Override
    public void record() {
        Command command = Command.RECORD;
        byte[] bytes = command.getBytes();
        log(command, bytes);
        sendCommand(bytes);
        requestStatus();
    }

    /** Release the tape, updates status. */
    @Override
    public void releaseTape() {
        Command command = Command.RELEASE_TAPE;
        byte[] bytes = command.getBytes();
        log(command, bytes);
        sendCommand(bytes);
        requestStatus();
    }

    /**
     * Query the VCR for its type. Return codes are MODEL        Data-1      Data-2<br> BVH-2000(00) 00  11<br>
     * BVH-2000(02) 00  10<br>
     */
    @Override
    public void requestDeviceType() {
        Command command = Command.DEVICE_TYPE_REQUEST;
        byte[] bytes = command.getBytes();
        log(command, bytes);
        sendCommand(bytes);
    }

    /**
     * Get longitudial timecode, which is stored on the audio track. Not as
     * accurate as getVTimeCode, but this method will get timecodes during fast-forwards, rewinds and shuttiling
     */
    @Override
    public void requestLTimeCode() {
        Command command = Command.GET_LTIMECODE;
        byte[] bytes = command.getBytes();
        log(command, bytes);
        sendCommand(bytes);
    }

    /**
     *
     */
    @Override
    public void requestLUserbits() {
        Command command = Command.GET_LUBTIMECODE;
        byte[] bytes = command.getBytes();
        log(command, bytes);
        //vcrReply.getVcrUserbits().setUserbits(EMPTY_USERBITS);
        sendCommand(bytes);
    }

    /**
     *
     */
    @Override
    public void requestLocalDisable() {
        Command command = Command.LOCAL_DISABLE;
        byte[] bytes = command.getBytes();

        log(command, bytes);
        sendCommand(bytes);
    }

    /**
     *
     */
    @Override
    public void requestLocalEnable() {
        Command command = Command.LOCAL_ENABLE;
        byte[] bytes = command.getBytes();

        log(command, bytes);
        sendCommand(bytes);
    }

    /** Send a "get status" command to the VCR */
    @Override
    public void requestStatus() {
        Command command = Command.GET_STATUS;
        byte[] bytes = command.getBytes();

        log(command, bytes);
        sendCommand(bytes);
    }

    /**
     * When requesting a timecode, the results depend on the tape speed because
     * at very low speeds (> 0.25 play speed) it may not be possible to recover
     * the timecode. However, if VITC is present it may be used instead. To
     * automate the decision process, Sony has provided a special command which
     * will return the best source of timecode. Note that when LTC and VITC are
     * both garbage you get back the 74.14 corrected LTC data. In this case, the
     * time is actually the last good LTC time corrected by the tape timer.
     *
     */
    @Override
    public void requestTimeCode() {
        Command command = Command.GET_TIMECODE;
        byte[] bytes = command.getBytes();

        log(command, bytes);
        sendCommand(bytes);
    }

    /**
     * Tries to request the best userbits. This method has not been tested.
     *
     */
    @Override
    public void requestUserbits() {
        requestLUserbits();
        requestLUserbits();
    }

    /**
     * Get vertical timecode, which is stored between video frames. These
     * timecodes can not be accessed in any mode accept play mode. So use
     * getLTimeCode when fast-forwarding, rewinding, or shuttling
     */
    @Override
    public void requestVTimeCode() {
        Command command = Command.GET_VTIMECODE;
        byte[] bytes = command.getBytes();

        log(command, bytes);
        sendCommand(bytes);
    }

    /**
     *
     */
    @Override
    public void requestVUserbits() {
        Command command = Command.GET_VUBTIMECODE;
        byte[] bytes = command.getBytes();
        //vcrReply.getVcrUserbits().setUserbits(EMPTY_USERBITS);
        log(command, bytes);
        sendCommand(bytes);
    }

    /** Rewind the tape, updates status. */
    @Override
    public void rewind() {
        Command command = Command.REWIND;
        byte[] bytes = command.getBytes();
        log(command, bytes);
        sendCommand(bytes);
        requestStatus();
    }

    /**
     * Fast forward to the specified timecode, updates status.
     * @param timecode a byte array representing the time code value
     * @see VCRUtil
     */
    @Override
    public void seekTimecode(byte[] timecode) {
        Command command = Command.SONY_SEEK_TIMECODE;
        byte[] bytes = command.getBytes();
        System.arraycopy(timecode, 0, bytes, 2, timecode.length);
        log(command, bytes);
        sendCommand(bytes);
        requestStatus();
    }

    /**
     * Fast forward to the specified timecode, updates status.
     * @param timecode
     */
    @Override
    public void seekTimecode(int timecode) {
        seekTimecode(NumberUtilities.toByteArray(timecode));
    }

    /**
     *
     * @param timecode
     */
    @Override
    public void seekTimecode(Timecode timecode) {
        Optional<HMSF> hmsfOpt = timecode.getHMSF();

        if (hmsfOpt.isPresent()) {
            HMSF hmsf = hmsfOpt.get();
            byte[] time = new byte[]{VCRUtil.intToTime(hmsf.getFrame()),
                    VCRUtil.intToTime(hmsf.getSecond()),
                    VCRUtil.intToTime(hmsf.getMinute()),
                    VCRUtil.intToTime(hmsf.getHour())};
            seekTimecode(time);
        }
    }

    /**
     * Sends a command, in the format of a byte[], to the VCR.
     * @param command The command to send to the VCR
     */
    protected synchronized void sendCommand(byte[] command) {

        // Store the last command
        this.mostRecentCommand = command;

        // Add the checksum
        byte checksum = VCRReply.calculateChecksum(command);

        command[command.length - 1] = checksum;

        try {
            outputStream.write(command);
            Thread.sleep(IO_DELAY);    // RXTX does not block serial IO correctly.
            readResponse();
        }
        catch (IOException e) {
            log.error("Failed to send a command to the VCR", e);
        }
        catch (VCRException e) {
            log.error("Failed to send a command to the VCR", e);
        }
        catch (InterruptedException e) {
            log.error("Thread " + Thread.currentThread().getName() + " was interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * shuttle forward using the giving speed, updates status.
     * @param speed value between 0 (slow) and 255 (fast)
     */
    @Override
    public void shuttleForward(int speed) {
        Command command = Command.SHUTTLE_FWD;
        byte[] bytes = command.getBytes();
        // put the correct speed in the byte array
        byte[] byteSpeed = NumberUtilities.toByteArray(speed);
        bytes[2] = byteSpeed[3];    // speed is between 0-255, ignore other bytes
        log(command, bytes);
        sendCommand(bytes);
        requestStatus();
    }

    /**
     * shuttle backwards using the giving speed, updates status.
     * @param speed value between 0 (slow) and 255 (fast)
     */
    @Override
    public void shuttleReverse(int speed) {
        Command command = Command.SHUTTLE_REV;
        byte[] bytes = command.getBytes();
        // put the correct speed in the byte array
        byte[] byteSpeed = NumberUtilities.toByteArray(speed);
        bytes[2] = byteSpeed[3];    // speed is between 0-255, ignore other bytes
        log(command, bytes);
        sendCommand(bytes);
        requestStatus();
    }

    /** Stop the tape, updates status. */
    @Override
    public void stop() {
        Command command = Command.STOP;
        byte[] bytes = command.getBytes();
        log(command, bytes);
        sendCommand(bytes);
        requestStatus();
    }
}
