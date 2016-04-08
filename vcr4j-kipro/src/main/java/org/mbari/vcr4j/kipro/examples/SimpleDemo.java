/*
 * @(#)SimpleDemo.java   by Brian Schlining
 *
 * Copyright (c) 2016 Monterey Bay Aquarium Research Institute
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mbari.vcr4j.kipro.examples;

import java.util.Optional;
import org.mbari.vcr4j.kipro.QuadVideoIO;
import org.mbari.vcr4j.kipro.json.ConfigEvent;
import org.mbari.vcr4j.kipro.json.ConnectionID;
import org.mbari.vcr4j.time.Timecode;

/**
 * @author Brian Schlining
 * @since 2016-02-11T08:51:00
 */
public class SimpleDemo {

    public static void main(String[] args) throws Exception {

        String httpAddress = args[0];

        // Get connection id
        String connectRequest = ConnectionID.buildRequest(httpAddress);
        System.out.println("REQUEST: " + connectRequest);
        String cid = QuadVideoIO.sendRequest(connectRequest);
        System.out.println("RESPONSE: " + cid);
        int connectionID = ConnectionID.fromJSON(cid).getConnectionid();
        System.out.println("ConnectionID = " + connectionID);


        // Poll for timecode
        for (int i = 0; i < 10; i++) {
            String request = ConfigEvent.buildRequest(httpAddress, connectionID);
            System.out.println(request);
            String r = QuadVideoIO.sendRequest(request);
            System.out.println("RESPONSE: " + r);
            ConfigEvent[] ces = ConfigEvent.fromJSON(r);
            Optional<Timecode> timecode = ConfigEvent.toTimecode(ces);

            if (!timecode.isPresent()) {
                System.out.println("No timecode was found");
            }
            else {
                System.out.println("Timecode: " + timecode.get());
            }

            Thread.sleep(1000);
        }

    }
}
