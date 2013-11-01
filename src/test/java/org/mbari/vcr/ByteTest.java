/*
 * Copyright 2007 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
Created on Sep 24, 2004
 *
TODO To change the template for this generated file go to
Window - Preferences - Java - Code Style - Code Templates
 */
package org.mbari.vcr;

import java.util.Date;
import org.junit.Test;
import org.mbari.util.NumberUtilities;

/**
 * @author brian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ByteTest {
    

    /**
     * Method description
     *
     */
    @Test()
    public void test1() {
        final byte[] b = new byte[] { 1, 2, 3, 4 };
        final int epicSeconds = NumberUtilities.toInt(b);
        final Date epicDate = new Date(((long) epicSeconds) * 1000L);
        //System.out.println(epicSeconds + " seconds = " + epicDate);
    }

    /**
     * Method description
     *
     *
     * @param args
     */
    public static void main(String[] args) {
        (new ByteTest()).test1();
    }
}
