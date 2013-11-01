/*
 * Copyright 2007 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.mbari.vcr;

//import javax.comm.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mbari.comm.CommUtil;
import org.mbari.util.NumberUtilities;
import org.mbari.vcr.rs422.Command;
import org.mbari.vcr.rs422.VCR;
import org.mbari.vcr.rs422.VCRReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class description
 *
 *
 * @version    $Id: $
 * @author     Brian Schlining
 */
public class VCRTestJavaComm1 {
    
    private static final Logger log = LoggerFactory.getLogger(VCRTestJavaComm1.class);
    private static long RECEIVE_TIMEOUT = 40;
    
    public VCRTestJavaComm1() {
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
    
    
    public static void run(String serialPortName) throws NoSuchPortException, IOException, PortInUseException, UnsupportedCommOperationException, InterruptedException {
        SerialPort serialPort = VCR.openSerialPort(serialPortName);
        InputStream in = serialPort.getInputStream();
        OutputStream out = serialPort.getOutputStream();
        
        byte[] response = null;
        send(out, Command.GET_STATUS);
        response = receive(in);
        send(out, Command.GET_TIMECODE);
        response = receive(in);
        send(out, Command.PLAY_FWD);
        response = receive(in);
        send(out, Command.GET_STATUS);
        response = receive(in);
        send(out, Command.GET_TIMECODE);
        response = receive(in);
        Thread.sleep(2000);
        send(out, Command.STOP);
        response = receive(in);
        send(out, Command.GET_TIMECODE);
        response = receive(in);
        Thread.sleep(2000);
        send(out, Command.GET_TIMECODE1);
        response = receive(in);
        Thread.sleep(2000);
        send(out, Command.GET_TIMECODE2);
        response = receive(in);
        Thread.sleep(2000);
        send(out, Command.GET_STATUS);
        response = receive(in);
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
    
    public static byte[] receive(InputStream inputStream) throws IOException {
        // Get the command returned by the VCR
        final byte[] cmd = new byte[2];
        inputStream.read(cmd);
        
        // Extract the number of data bytes in the command block. Then
        // read the data from the serial port
        final int numDataBytes = (int) (cmd[0] & 0x0F);    // Get the number of data blocks
        byte[] data = null;
        if (numDataBytes > 0) {
            data = new byte[numDataBytes];
            inputStream.read(data);
        }
        
        // Read the checksum that the VCR sends
        final byte[] checksum = new byte[1];
        inputStream.read(checksum);
        
    /*
     * Munge it all into a single byte array
     */
        int dataLength = data == null ? 0 : data.length;
        final byte[] c = new byte[cmd.length + dataLength + 1];
        System.arraycopy(cmd, 0, c, 0, cmd.length);
        if (data != null) {
            System.arraycopy(data, 0, c, cmd.length, data.length);
        }
        c[c.length - 1] = checksum[0];
        
        log.debug("VCR >> " + NumberUtilities.toHexString(c));
        return c;
    }
    
}
