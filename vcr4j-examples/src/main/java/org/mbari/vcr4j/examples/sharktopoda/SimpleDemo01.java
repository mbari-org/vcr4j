package org.mbari.vcr4j.examples.sharktopoda;

import org.docopt.Docopt;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd;

import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;

/**
 * @author Brian Schlining
 * @since 2016-08-26T14:56:00
 */
public class SimpleDemo01 {

    public static void main(String[] args) throws Exception {
        String prog = SimpleDemo01.class.getName();
        String doc = "Usage: " + prog + " <port> <url>\n" +
                "Options:\n" +
                "  -h, --help";

        Map<String, Object> opts = new Docopt(doc).parse(args);

        Integer port = Integer.parseInt((String) opts.get("<port>"));
        URL url = new URL((String) opts.get("<url>"));

        SharktopodaVideoIO io = new SharktopodaVideoIO(UUID.randomUUID(), "localhost" , port);
        io.send(new OpenCmd(url));
        Thread.sleep(2000);
        io.send(VideoCommands.PLAY);
        Thread.sleep(4000);
        io.send(VideoCommands.PAUSE);

    }
}
