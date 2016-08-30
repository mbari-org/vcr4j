package org.mbari.vcr4j.examples.sharktopoda;

import org.docopt.Docopt;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.sharktopoda.SharktopodaError;
import org.mbari.vcr4j.sharktopoda.SharktopodaState;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd;
import org.mbari.vcr4j.sharktopoda.commands.SharkCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-30T10:09:00
 */
public class SeekElapsedTimeTest {

    public static void main(String[] args) throws Exception {

        String prog = SeekElapsedTimeTest.class.getName();
        String doc = "Usage: " + prog + " <port> <url>\n" +
                "Options:\n" +
                "  -h, --help";

        Map<String, Object> opts = new Docopt(doc).parse(args);

        Integer port = Integer.parseInt((String) opts.get("<port>"));
        URL url = new URL((String) opts.get("<url>"));

        Logger log = LoggerFactory.getLogger(SeekElapsedTimeTest.class);

        VideoIO<SharktopodaState, SharktopodaError> io = new SharktopodaVideoIO(UUID.randomUUID(), "localhost", port);


        io.send(new OpenCmd(url));

        io.send(new SeekElapsedTimeCmd(Duration.ofSeconds(10)));
        io.send(VideoCommands.PLAY);
        Thread.sleep(2000);
        io.send(new SeekElapsedTimeCmd(Duration.ofSeconds(1)));
        Thread.sleep(1000);
        io.send(new SeekElapsedTimeCmd(Duration.ofSeconds(25)));
        Thread.sleep(1000);
        io.send(new SeekElapsedTimeCmd(Duration.ofSeconds(5)));
        io.send(VideoCommands.STOP);
        io.send(SharkCommands.CLOSE);

    }
}
