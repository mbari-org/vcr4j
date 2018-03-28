package org.mbari.vcr4j.jserialcomm;

import com.fazecast.jSerialComm.SerialPort;
import org.mbari.vcr4j.rs422.RS422Exception;
import org.mbari.vcr4j.rs422.RS422ResponseParser;
import org.mbari.vcr4j.rs422.RS422State;
import org.mbari.vcr4j.rs422.RS422VideoIO;
import org.mbari.vcr4j.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by brian on 3/26/16.
 */
public class SerialCommVideoIO extends RS422VideoIO  {

    /**
     * For RXTX we have to put the thread to sleep VERY briefly
     * in order for the serial io to work. We'll keep using this with jSerialComm
     */
    public static final long IO_DELAY = 10;

    /**
     * Maximum receive timeout in millisecs
     */
    public final static long RECEIVE_TIMEOUT = 40;


    private final Logger log = LoggerFactory.getLogger(getClass());
    private SerialPort serialPort;    // Serial port connected to VCR

    public SerialCommVideoIO(SerialPort serialPort, InputStream inputStream, OutputStream outputStream) {
        super(inputStream, outputStream, IO_DELAY);
        Preconditions.checkArgument(serialPort != null, "SerialPort can not be null");
        this.serialPort = serialPort;
    }

    @Override
    public String getConnectionID() {
        return serialPort.getSystemPortName();
    }

    @Override
    public void close() {
        log.info("Closing serial port:" + serialPort.getSystemPortName());

        try {
            getCommandSubject().onComplete();
            getOutputStream().close();
            getInputStream().close();
            serialPort.closePort();
            RS422ResponseParser responseParser = getResponseParser();
            responseParser.getStatusObservable().onNext(RS422State.STOPPED);
            responseParser.getStatusObservable().onComplete();
            responseParser.getTimecodeObservable().onComplete();
            responseParser.getErrorObservable().onComplete();
            responseParser.getUserbitsObservable().onComplete();
            //serialPort = null;
        }
        catch (Exception e) {
            if (log.isErrorEnabled()
                    && (serialPort != null)) {
                log.error("Problem occured when closing serial port communications on " + serialPort.getSystemPortName());
            }
        }
    }

    public static SerialCommVideoIO open(String portName) {
        try {
            SerialPort serialPort = SerialPort.getCommPort(portName);
            serialPort.openPort();
            serialPort.setBaudRate(38400);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 100, 0);
            serialPort.setNumDataBits(8);
            serialPort.setParity(SerialPort.ODD_PARITY);
            serialPort.setNumStopBits(1);
            serialPort.setFlowControl(SerialPort.FLOW_CONTROL_RTS_ENABLED);

            InputStream inputStream = serialPort.getInputStream();
            OutputStream outputStream = serialPort.getOutputStream();
            return new SerialCommVideoIO(serialPort, inputStream, outputStream);
        }
        catch (Exception e) {
            throw new RS422Exception("Failed to open " + portName, e);
        }
    }

    public static Set<String> getAvailableSerialPorts() {
        SerialPort[] ports = SerialPort.getCommPorts();
        Set<String> portNames = new HashSet<>();
        for (SerialPort port : ports) {
            try {
                port.openPort();
                port.closePort();
                portNames.add(port.getSystemPortName());
            }
            catch (Exception e) {
                // Port is not available
            }
        }
        return portNames;
    }

    public static List<String> getSerialPorts() {
        return Arrays.stream(SerialPort.getCommPorts())
            .map(SerialPort::getSystemPortName)
            .sorted()
            .collect(Collectors.toList());
    }
}
