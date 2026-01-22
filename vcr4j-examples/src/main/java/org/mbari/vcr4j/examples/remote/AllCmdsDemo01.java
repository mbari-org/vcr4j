package org.mbari.vcr4j.examples.remote;

/*-
 * #%L
 * vcr4j-examples
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

import org.docopt.Docopt;
import org.mbari.vcr4j.commands.RemoteCommands;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.*;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// mvn install -DskipTests -Dgpg.skip
// mvn exec:java -Dexec.mainClass=org.mbari.vcr4j.examples.remote.AllCmdsDemo01 -Dexec.args="8800 file:/Users/brian/Downloads/V4003_20170301T210458.233Z_t4s4_1280_tc03560915_h264.mp4" -pl vcr4j-examples
public class AllCmdsDemo01 {

    public static void main(String[] args) throws Exception {
        String prog = AllCmdsDemo01.class.getName();
        String doc = "Usage: " + prog + " <port> <url>\n" +
                "Options:\n" +
                "  -h, --help";

        Map<String, Object> opts = new Docopt(doc).parse(args);
        var port = Integer.parseInt((String) opts.get("<port>"));
        URL url = new URL((String) opts.get("<url>"));

        var uuid = UUID.randomUUID();

        var commands = List.of(new OpenCmd(uuid, url),
                VideoCommands.PLAY,
                VideoCommands.FAST_FORWARD,
                VideoCommands.REWIND,
                VideoCommands.PAUSE,
                VideoCommands.PLAY,
                VideoCommands.REQUEST_ELAPSED_TIME,
                VideoCommands.REQUEST_INDEX,
                VideoCommands.REQUEST_STATUS,
                VideoCommands.REQUEST_DEVICE_TYPE,
                VideoCommands.REQUEST_TIMECODE,
                VideoCommands.REQUEST_TIMESTAMP,
                RemoteCommands.FRAMEADVANCE,
                RemoteCommands.REQUEST_ALL_VIDEO_INFOS,
                RemoteCommands.REQUEST_VIDEO_INFO,
                RemoteCommands.SHOW,
                new PlayCmd(uuid, -2.0),
                new PlayCmd(uuid, -2.0),
                new ShuttleCmd(0.02),
                new ShuttleCmd(-0.02),
                new SeekElapsedTimeCmd(Duration.ofMillis(1000)),
                new PlayCmd(uuid, 0.01),
                new SeekElapsedTimeCmd(Duration.ofMillis(50000)),
                new PlayCmd(uuid, 0.01),
                new FrameCaptureCmd(uuid, UUID.randomUUID(), (new File("trashme-" + UUID.randomUUID() + ".png").getAbsolutePath())),
                new SeekElapsedTimeCmd(Duration.ofMillis(10000)),
                new PlayCmd(uuid, 0.01),
                new OpenCmd(uuid, url),
                RemoteCommands.CLOSE);

        var ports = List.of(5000);
        for (var listenPort: ports) {

            var io = new RemoteControl.Builder(uuid)
                    .port(listenPort)
                    .remotePort(port)
                    .remoteHost("localhost")
                    .build()
                    .get();

            Thread.sleep(1000);

            for (var cmd: commands) {
                io.getVideoIO().send(cmd);
                Thread.sleep(1000);
            }
            Thread.sleep(1000);
            io.close();
        }

        System.exit(0);
    }
}
