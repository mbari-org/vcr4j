package org.mbari.vcr4j.purejavacomm;

import org.mbari.vcr4j.rs422.RS422Exception;
import org.mbari.vcr4j.rs422.RS422ResponseParser;
import org.mbari.vcr4j.rs422.RS422State;
import org.mbari.vcr4j.rs422.RS422VideoIO;
import org.mbari.vcr4j.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Brian Schlining
 * @since 2016-02-03T14:02:00
 */
public class PJCVideoIO extends RS422VideoIO {

    /**
     * Wwe have to put the thread to sleep VERY briefly
     * in order for the serial io to work
     */
    public static final long IO_DELAY = 100;

    /**
     * Maximum receive timeout in millisecs
     */
    public final static long RECEIVE_TIMEOUT = 400;

    private final Logger log = LoggerFactory.getLogger(getClass());
    private SerialPort serialPort;    // Serial port connected to VCR

    public PJCVideoIO(SerialPort serialPort, InputStream inputStream, OutputStream outputStream) {
        super(inputStream, outputStream, IO_DELAY);
        Preconditions.checkArgument(serialPort != null, "SerialPort can not be null");
        this.serialPort = serialPort;
    }

    @Override
    public void close() {
        log.info("Closing serial port:" + serialPort.getName());

        try {
            getCommandSubject().onComplete();
            getOutputStream().close();
            getInputStream().close();
            serialPort.close();
            RS422ResponseParser responseParser = getResponseParser();
            responseParser.getStatusObservable().onNext(RS422State.STOPPED);
            responseParser.getStatusObservable().onComplete();
            responseParser.getTimecodeObservable().onComplete();
            responseParser.getErrorObservable().onComplete();
            responseParser.getUserbitsObservable().onComplete();
            serialPort = null;
        }
        catch (Exception e) {
            if (log.isErrorEnabled()
                    && (serialPort != null)) {
                log.error("Problem occured when closing serial port communications on " + serialPort.getName());
            }
        }
    }

    /**
     * Factory method. Use this to open a connection
     * @param portName
     * @return
     */
    public static PJCVideoIO open(String portName) {
        try {
            SerialPort serialPort = openSerialPort(portName);
            InputStream inputStream = serialPort.getInputStream();
            OutputStream outputStream = serialPort.getOutputStream();

            return new PJCVideoIO(serialPort, inputStream, outputStream);
        }
        catch (Exception e) {
            throw new RS422Exception("Failed to open " + portName, e);
        }
    }

    public static SerialPort openSerialPort(String serialPortName)
            throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException {
        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(serialPortName);
        SerialPort serialPort = (SerialPort) portId.open("VCR", 2000);

        serialPort.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_ODD);
        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);

        // If the below is enabled you will not get any output from the VCR.
        // serialPort.setFlowControlMode(
        // SerialPort.FLOWCONTROL_RTSCTS_IN
        // & SerialPort.FLOWCONTROL_RTSCTS_OUT);
        serialPort.enableReceiveTimeout((int) RECEIVE_TIMEOUT);    // Returns after waiting 190ms

        return serialPort;
    }

    @Override
    public String getConnectionID() {
        return serialPort.getName();
    }
}
