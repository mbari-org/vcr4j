package org.mbari.vcr4j.examples.sharktopoda;

import org.docopt.Docopt;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd;

import java.net.URL;
import java.util.Map;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-29T09:53:00
 */
public class PauseTest {

    public static void main(String[] args) throws Exception {
        String prog = PauseTest.class.getName();
        String doc = "Usage: " + prog + " <port> <url>\n" +
                "Options:\n" +
                "  -h, --help";

        Map<String, Object> opts = new Docopt(doc).parse(args);

        Integer port = Integer.parseInt((String) opts.get("<port>"));
        URL url = new URL((String) opts.get("<url>"));

        SharktopodaVideoIO io = new SharktopodaVideoIO(UUID.randomUUID(), "localhost" , port);
        //new LoggingDecorator<>(io);


        io.send(new OpenCmd(url));
        io.send(VideoCommands.PLAY);
        Thread.sleep(2000);
        io.send(VideoCommands.PAUSE);
        Thread.sleep(200);
        io.send(VideoCommands.REQUEST_INDEX);
        Thread.sleep(200);
        io.send(VideoCommands.FAST_FORWARD);
        Thread.sleep(4000);
        io.send(VideoCommands.PAUSE);
        io.send(VideoCommands.REQUEST_INDEX);
        Thread.sleep(200);
        io.send(VideoCommands.REWIND);
        Thread.sleep(3000);
        io.send(VideoCommands.PAUSE);
        io.send(VideoCommands.REQUEST_INDEX);
        Thread.sleep(200);
        io.send(new ShuttleCmd(0.4));
        Thread.sleep(2000);
        io.send(VideoCommands.PAUSE);
        io.send(VideoCommands.REQUEST_INDEX);
        Thread.sleep(200);

        io.send(VideoCommands.REQUEST_INDEX);
        io.send(VideoCommands.REQUEST_STATUS);
        io.send(VideoCommands.REQUEST_ELAPSED_TIME);
        //io.send(VideoCommands.REQUEST_STATUS);

    }
}
