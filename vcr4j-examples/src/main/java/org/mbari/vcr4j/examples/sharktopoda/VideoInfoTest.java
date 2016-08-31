package org.mbari.vcr4j.examples.sharktopoda;

import org.docopt.Docopt;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd;
import org.mbari.vcr4j.sharktopoda.commands.SharkCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2016-08-31T14:22:00
 */
public class VideoInfoTest {

    public static void main(String[] args) throws Exception {

        Logger log = LoggerFactory.getLogger(VideoInfoTest.class);


        String prog = VideoInfoTest.class.getName();
        String doc = "Usage: " + prog + " <port> <url>...\n" +
                "Options:\n" +
                "  -h, --help";

        Map<String, Object> opts = new Docopt(doc).parse(args);

        Integer port = Integer.parseInt((String) opts.get("<port>"));
        List<URL> urls = ((List<String>) opts.get("<url>")).stream()
                .map(s -> {
                    Optional<URL> url;
                    try {
                        url = Optional.of(new URL(s));
                    }
                    catch (MalformedURLException e) {
                        url = Optional.empty();
                    }
                    return url;
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());


        for (URL url: urls) {
            log.info("Opening " + url);
            Thread t = new Thread(() -> {
                try {
                    SharktopodaVideoIO io = new SharktopodaVideoIO(UUID.randomUUID(), "localhost", port);

                    io.getVideoInfoSubject().forEach(vi -> System.out.println(vi));
                    io.send(new OpenCmd(url));
                    io.send(SharkCommands.REQUEST_VIDEO_INFO);
                    io.send(VideoCommands.PLAY);
                    Thread.sleep(3000);
                    io.send(new SeekElapsedTimeCmd(Duration.ofMillis(0)));
                    io.send(SharkCommands.SHOW);
                    Thread.sleep(3000);
                    io.send(SharkCommands.SHOW);
                    Thread.sleep(3000);
                    io.send(new ShuttleCmd(-0.1));
                    io.send(SharkCommands.SHOW);
                    Thread.sleep(4000);
                    io.send(SharkCommands.CLOSE);
                }
                catch (Exception e) {
                    log.error("Thread died", e);
                }

            });
            t.setDaemon(false);
            t.start();
            Thread.sleep(1000);
        }

    }
}
