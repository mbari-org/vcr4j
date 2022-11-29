package org.mbari.vcr4j.examples.remote;

import org.docopt.Docopt;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.*;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                new SeekElapsedTimeCmd(Duration.ofMillis(10000)),
                new PlayCmd(uuid, 0.01),
                new FrameCaptureCmd(uuid, UUID.randomUUID(), File.createTempFile("trashme", ".png").getAbsolutePath()),
                new SeekElapsedTimeCmd(Duration.ofMillis(1000)),
                new PlayCmd(uuid, 0.01),
                new OpenCmd(uuid, url),
                RemoteCommands.CLOSE);

        var ports = List.of(5000, 5555);
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
        }

        System.exit(0);
    }
}
