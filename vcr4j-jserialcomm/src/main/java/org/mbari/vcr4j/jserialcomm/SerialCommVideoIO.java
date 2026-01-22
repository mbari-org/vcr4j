package org.mbari.vcr4j.jserialcomm;

/*-
 * #%L
 * vcr4j-jserialcomm
 * %%
 * Copyright (C) 2008 - 2026 Monterey Bay Aquarium Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.fazecast.jSerialComm.SerialPort;
import org.mbari.vcr4j.rs422.RS422Exception;
import org.mbari.vcr4j.rs422.RS422ResponseParser;
import org.mbari.vcr4j.rs422.RS422State;
import org.mbari.vcr4j.rs422.RS422VideoIO;
import org.mbari.vcr4j.util.Preconditions;

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


//    private final Logger log = LoggerFactory.getLogger(getClass());
    private final System.Logger log = System.getLogger(getClass().getName());
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
        // serial port will be null if we've already close it
        if (serialPort != null) {
            log.log(System.Logger.Level.INFO, "Closing serial port:" + serialPort.getSystemPortName());

            try {
                getCommandSubject().onComplete();
                getOutputStream().close();
                getInputStream().close();
                if (serialPort.isOpen()) {
                    serialPort.closePort();
                }
                RS422ResponseParser responseParser = getResponseParser();
                responseParser.getStatusObservable().onNext(RS422State.STOPPED);
                responseParser.getStatusObservable().onComplete();
                responseParser.getTimecodeObservable().onComplete();
                responseParser.getErrorObservable().onComplete();
                responseParser.getUserbitsObservable().onComplete();
                serialPort = null;
            } catch (Exception e) {
                if (serialPort != null) {
                    log.log(System.Logger.Level.ERROR, () -> "Problem occured when closing serial port communications on " + serialPort.getSystemPortName());
                }
            }
        }
    }

    public static SerialCommVideoIO open(String portName) {
        System.Logger slog = System.getLogger(SerialCommVideoIO.class.getName());
        slog.log(System.Logger.Level.INFO, "Opening serial port: " + portName);
        SerialPort serialPort = null;
        try {
            serialPort = SerialPort.getCommPort(portName);
            if (!serialPort.isOpen()) {
                serialPort.openPort();
            }
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
            slog.log(System.Logger.Level.WARNING, "Failed to open " + portName, e);
            if (serialPort != null && serialPort.isOpen()) {
                serialPort.closePort();
            }
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
