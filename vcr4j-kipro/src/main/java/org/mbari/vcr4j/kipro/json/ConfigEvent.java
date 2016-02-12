/*
 * @(#)QuadConfigEvent.java   by Brian Schlining
 *
 * Copyright (c) 2016 Monterey Bay Aquarium Research Institute
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mbari.vcr4j.kipro.json;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mbari.vcr4j.time.Timecode;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The KiPro web interface uses AJAX to poll for status events. We can use that like so:
 * <pre>
 *
 * `http GET 'http://134.89.11.139/config?action=wait_for_config_events&connectionid=0'`
 *
 * This may return various JSON contents. The JSON part we care about has `param_id` of `eParamID_DisplayTimecode` to get the timecode.
 *
 * ```
 * HTTP/1.1 200 OK
 * Cache-Control: no-cache
 * Connection: keep-alive
 * Content-Type: text/json
 * Date: Tue, 26 Jan 2016 16:04:16 GMT
 * Server: Seminole/2.64 (Linux; B88)
 * Transfer-encoding: chunked
 *
 * [
 * {
 * "int_value": "0",
 * "last_config_update": "0",
 * "param_id": "eParamID_DisplayTimecode",
 * "param_type": "12",
 * "str_value": "00:02:51:27"
 * },
 * {
 * "int_value": "0",
 * "last_config_update": "0",
 * "param_id": "eParamID_InputTimecode",
 * "param_type": "12",
 * "str_value": "00:00:00:00"
 * }
 * ]
 * ```
 * </pre>
 *
 * @author Brian Schlining
 * @since 2016-02-04T16:54:00
 */
public class ConfigEvent {

    private static final Gson gson = Constants.GSON;

    private int intValue;
    private int lastConfigUpdate;
    private String paramId;
    private int paramType;
    private String strValue;

    public ConfigEvent(int intValue, int lastConfigUpdate, String paramId, int paramType, String strValue) {
        this.intValue = intValue;
        this.lastConfigUpdate = lastConfigUpdate;
        this.paramId = paramId;
        this.paramType = paramType;
        this.strValue = strValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public int getLastConfigUpdate() {
        return lastConfigUpdate;
    }

    public String getParamId() {
        return paramId;
    }

    public int getParamType() {
        return paramType;
    }

    public String getStrValue() {
        return strValue;
    }

    public static ConfigEvent[] fromJSON(String json) {
        return  gson.fromJson(json, ConfigEvent[].class);
    }

    public String toJSON() {
        return gson.toJson(this);
    }

    public static String buildRequest(String httpAddress, int connectionID) {
        return httpAddress + "config?action=wait_for_config_events&connectionid=" + connectionID;
    }

    public static Optional<Timecode> toTimecode(ConfigEvent[] events) {
        List<Timecode> timecodes = Arrays.stream(events)
                .filter(e -> e.paramId.equalsIgnoreCase(Constants.EParams.DISPLAY_TIMECODE))
                .map(e -> new Timecode(e.getStrValue()))
                .collect(Collectors.toList());

        if (timecodes.isEmpty()) {
            return Optional.empty();
        }
        else {
            return Optional.of(timecodes.get(0));
        }
    }
}
