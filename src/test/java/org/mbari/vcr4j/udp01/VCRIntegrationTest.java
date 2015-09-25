/*
 * Copyright 2007 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.mbari.vcr4j.udp01;

import java.net.SocketException;
import java.net.UnknownHostException;
import junit.framework.TestCase;
import org.mbari.vcr4j.IVCR;
import org.mbari.vcr4j.time.Timecode;

/**
 * Class description
 *
 *
 * @version    $Id: $
 * @author     Brian Schlining    
 */
public class VCRIntegrationTest extends TestCase {

    /**
     * Constructs ...
     *
     *
     * @param arg0
     */
    public VCRIntegrationTest(String arg0) {
        super(arg0);
    }

    /**
     * Method description
     *
     */
    public void testPL() {
        try {
            IVCR vcr = new VCR("navproc.pl.mbari.org", 9000);
            Timecode timecode = vcr.getVcrTimecode().getTimecode();
            System.out.println(timecode.toString());
            vcr.requestLTimeCode();
            vcr.requestVTimeCode();
            vcr.requestTimeCode();
            vcr.disconnect();
        }
        catch (UnknownHostException e) {
            fail("Unknown host");
        }
        catch (SocketException e) {
            fail("SocketExcpetion");
        }
        catch (Exception e) {
            fail("" + e.getClass());
        }
    }

    /**
     * Method description
     *
     */
    public void testWF() {
        try {
            IVCR vcr = new VCR("itchy.wf.mbari.org", 9001);
            Timecode timecode = vcr.getVcrTimecode().getTimecode();
            System.out.println(timecode.toString());
            vcr.requestLTimeCode();
            vcr.requestVTimeCode();
            vcr.requestTimeCode();
            vcr.disconnect();
        }
        catch (UnknownHostException e) {
            fail("Unknown host");
        }
        catch (SocketException e) {
            fail("SocketExcpetion");
        }
    }

    /**
     * Method description
     *
     *
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(VCRIntegrationTest.class);
    }
}
