package org.mbari.vcr4j.examples.sharktopoda;

import org.docopt.Docopt;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd;
import org.mbari.vcr4j.sharktopoda.commands.SharkCommands;

import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/**
 * Created by brian on 9/1/16.
 */
public class FrameAdvanceTest {

    public static void main(String[] args) throws Exception {
        String prog = FrameAdvanceTest.class.getName();
        String doc = "Usage: " + prog + " <port> <url>\n" +
                "Options:\n" +
                "  -h, --help";

        Map<String, Object> opts = new Docopt(doc).parse(args);

        Integer port = Integer.parseInt((String) opts.get("<port>"));
        URL url = new URL((String) opts.get("<url>"));

        SharktopodaVideoIO io = new SharktopodaVideoIO(UUID.randomUUID(), "localhost" , port);

        io.send(new OpenCmd(url));
        io.send(new SeekElapsedTimeCmd(Duration.ofSeconds(2)));

        for (int i = 0; i < 100; i++) {
            io.send(SharkCommands.FRAMEADVANCE);
            Thread.sleep(200);
        }

        io.send(SharkCommands.CLOSE);
    }
}
