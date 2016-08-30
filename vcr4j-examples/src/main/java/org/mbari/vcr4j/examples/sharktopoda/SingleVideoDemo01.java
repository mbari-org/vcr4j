package org.mbari.vcr4j.examples.sharktopoda;

import org.docopt.Docopt;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.sharktopoda.SharktopodaError;
import org.mbari.vcr4j.sharktopoda.SharktopodaState;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd;
import org.mbari.vcr4j.sharktopoda.commands.SharkCommands;

import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-29T11:09:00
 */
public class SingleVideoDemo01 {

    private static VideoIO<SharktopodaState, SharktopodaError> io;

    public static void main(String[] args) throws Exception {
        String prog = SingleVideoDemo01.class.getName();
        String doc = "Usage: " + prog + " <port> <url>\n" +
                "Options:\n" +
                "  -h, --help";

        Map<String, Object> opts = new Docopt(doc).parse(args);

        Integer port = Integer.parseInt((String) opts.get("<port>"));
        URL url = new URL((String) opts.get("<url>"));

        io = new SharktopodaVideoIO(UUID.randomUUID(), "localhost", port);
        //new LoggingDecorator<>(io);

        List<VideoCommand> cmds = Arrays.asList(new OpenCmd(url),
                VideoCommands.PLAY,
                SharkCommands.SHOW,
                SharkCommands.REQUEST_VIDEO_INFO,
                SharkCommands.REQUEST_ALL_VIDEO_INFOS,
                VideoCommands.REQUEST_ELAPSED_TIME,
                VideoCommands.REQUEST_INDEX,
                VideoCommands.REQUEST_STATUS,
                new SeekElapsedTimeCmd(Duration.ofMillis(3000)),
                new ShuttleCmd(0.5),
                new ShuttleCmd(-0.5),
                new ShuttleCmd(0.1),
                new ShuttleCmd(-0.1),
                VideoCommands.PAUSE,
                SharkCommands.CLOSE,
                VideoCommands.REQUEST_INDEX);

        for (VideoCommand cmd : cmds) {
            doCommand(cmd);
        }

    }

    private static void doCommand(VideoCommand cmd) throws Exception {
        io.send(cmd);
        Thread.sleep(500);
    }
}
