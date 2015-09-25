/*
 * Copyright 2007 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.mbari.vcr4j.vcr;

//import javax.comm.NoSuchPortException;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.TooManyListenersException;
import junit.framework.Assert;
import org.junit.Test;
import org.mbari.vcr4j.rs422.CommUtil;
import org.mbari.util.NumberUtilities;
import org.mbari.vcr4j.rs422.Command;
import org.mbari.vcr4j.rs422.VCR;
//import javax.comm.PortInUseException;
import org.mbari.vcr4j.rs422.VCRReply;
//import javax.comm.SerialPortEvent;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
//import javax.comm.SerialPortEventListener;
//import javax.comm.UnsupportedCommOperationException;


/**
 * Class description
 *
 *
 * @version    $Id: $
 * @author     Brian Schlining
 */
public class VCRTestJavaComm2 {
    
    private static long RECEIVE_TIMEOUT = 40;
    
    private static final Logger log = LoggerFactory.getLogger(VCRTestJavaComm2.class);
    
    public VCRTestJavaComm2() {
    }
    
    @Test
    public void test01() {
        Set<CommPortIdentifier> ports = CommUtil.getAvailableSerialPorts();
        for (CommPortIdentifier port: ports) {
            try {
                log.debug("Testing port " + port.getName());
                run(port.getName());
            } catch (Exception ex) {
                log.error("An exception occured", ex);
                Assert.fail();
            } 
        }
    }
    
    public static void run(String serialPortName) throws NoSuchPortException, IOException, PortInUseException, UnsupportedCommOperationException, InterruptedException, TooManyListenersException {
        SerialPort serialPort = VCR.openSerialPort(serialPortName);
        serialPort.notifyOnDataAvailable(true);
        final InputStream in = serialPort.getInputStream();
        final OutputStream out = serialPort.getOutputStream();
        
        serialPort.addEventListener(new MyListener(in));
        
        byte[] response = null;
        send(out, Command.GET_STATUS);
        send(out, Command.GET_TIMECODE);
        send(out, Command.PLAY_FWD);
        send(out, Command.GET_STATUS);
        send(out, Command.GET_TIMECODE);
        Thread.sleep(2000);
        send(out, Command.STOP);
        send(out, Command.GET_TIMECODE);
        Thread.sleep(2000);
        send(out, Command.GET_TIMECODE1);
        Thread.sleep(2000);
        send(out, Command.GET_TIMECODE2);
        Thread.sleep(2000);
        send(out, Command.GET_STATUS);
        Thread.sleep(2000);
        in.close();
        out.close();
        serialPort.close();
    }
    
    public static void send(OutputStream outputStream, Command command) throws IOException {
        byte[] bytes = command.getBytes();
        bytes[bytes.length - 1] = VCRReply.calculateChecksum(bytes);
        log.debug("VCR << " + NumberUtilities.toHexString(bytes));
        outputStream.write(bytes);
    }
    
    static class MyListener implements  SerialPortEventListener {
        
        private final InputStream inputStream;
        public MyListener(InputStream inputStream) {
            this.inputStream = inputStream;
        }
        
        
        
        public void serialEvent(SerialPortEvent evt) {
            
            switch (evt.getEventType()) {
                
            case SerialPortEvent.BI:
                
            case SerialPortEvent.OE:
                
            case SerialPortEvent.FE:
                
            case SerialPortEvent.PE:
                
            case SerialPortEvent.CD:
                
            case SerialPortEvent.CTS:
                
            case SerialPortEvent.DSR:
                
            case SerialPortEvent.RI:
                
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
                
            case SerialPortEvent.DATA_AVAILABLE:
                byte[] readBuffer = new byte[16];
                
                try {
                    while (inputStream.available() > 0) {
                        int numBytes = inputStream.read(readBuffer);
                        log.debug("VCR >> " + NumberUtilities.toHexString(readBuffer));
                    }
                } 
                catch (IOException e) {
                    log.error("Trouble reading from serialport", e);
                }
                
                break;
            }
            
        }
        
    }
    
}
