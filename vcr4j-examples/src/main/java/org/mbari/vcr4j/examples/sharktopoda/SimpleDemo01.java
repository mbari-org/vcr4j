package org.mbari.vcr4j.examples.sharktopoda;

import org.docopt.Docopt;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd;

import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;

/**
 * @author Brian Schlining
 * @since 2016-08-26T14:56:00
 *
 *  http://www.sample-videos.com/video/mp4/480/big_buck_bunny_480p_1mb.mp4
 *  file:/Volumes/LBD2/i2map/2015-06-15_Test_Dives_4.mov
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
        //new LoggingDecorator<>(io);
        io.send(new OpenCmd(url));
        Thread.sleep(2000);
        io.send(VideoCommands.PLAY);
        for (int i = 0; i < 10; i++) {
            io.send(VideoCommands.REQUEST_STATUS);
            Thread.sleep(100);
        }

        io.send(VideoCommands.PAUSE);
        io.send(VideoCommands.REQUEST_STATUS);
        //io.send(VideoCommands.REQUEST_STATUS);

    }
}
