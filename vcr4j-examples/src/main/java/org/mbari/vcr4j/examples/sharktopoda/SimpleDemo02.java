package org.mbari.vcr4j.examples.sharktopoda;

import org.docopt.Docopt;
import org.mbari.vcr4j.VideoCommand;
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
 * @since 2016-08-29T09:06:00
 */
public class SimpleDemo02 {
    public static void main(String[] args) throws Exception {
        String prog = SimpleDemo02.class.getName();
        String doc = "Usage: " + prog + " <port> <url>\n" +
                "Options:\n" +
                "  -h, --help";

        Map<String, Object> opts = new Docopt(doc).parse(args);

        Integer port = Integer.parseInt((String) opts.get("<port>"));
        URL url = new URL((String) opts.get("<url>"));

        SharktopodaVideoIO io = new SharktopodaVideoIO(UUID.randomUUID(), "localhost" , port);
        new LoggingDecorator<>(io);


        io.send(new OpenCmd(url));
        io.send(VideoCommands.REQUEST_INDEX);
        io.send(VideoCommands.PLAY);
        io.send(VideoCommands.REQUEST_STATUS);
        Thread.sleep(2000);
        io.send(VideoCommands.REQUEST_INDEX);
        io.send(VideoCommands.FAST_FORWARD);
        io.send(VideoCommands.REQUEST_STATUS);
        Thread.sleep(4000);
        io.send(VideoCommands.PAUSE);
        io.send(VideoCommands.REQUEST_INDEX);
        Thread.sleep(500);
        io.send(VideoCommands.REQUEST_INDEX);
        io.send(VideoCommands.REWIND);
        io.send(VideoCommands.REQUEST_STATUS);
        Thread.sleep(3000);
        io.send(VideoCommands.REQUEST_INDEX);
        io.send(new ShuttleCmd(0.4));
        io.send(VideoCommands.REQUEST_STATUS);
        Thread.sleep(2000);
        io.send(VideoCommands.REQUEST_INDEX);
        io.send(new ShuttleCmd(-0.2));
        io.send(VideoCommands.REQUEST_STATUS);
        Thread.sleep(2000);

        io.send(VideoCommands.REQUEST_INDEX);
        io.send(VideoCommands.PAUSE);
        io.send(VideoCommands.REQUEST_STATUS);
        io.send(VideoCommands.REQUEST_ELAPSED_TIME);
        //io.send(VideoCommands.REQUEST_STATUS);

    }
}
