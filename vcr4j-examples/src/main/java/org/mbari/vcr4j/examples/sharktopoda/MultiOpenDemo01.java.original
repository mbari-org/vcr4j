package org.mbari.vcr4j.examples.sharktopoda;

import org.docopt.Docopt;
import org.mbari.vcr4j.commands.RemoteCommands;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *  mvn exec:java -Dexec.mainClass="org.mbari.vcr4j.examples.sharktopoda.MultiOpenDemo01" -Dexec.args="8800 http://m3.shore.mbari.org/videos/M3/mezzanine/DocRicketts/2019/04/1142/D1142_20190419T233245Z_h264.mp4 http://m3.shore.mbari.org/videos/M3/mezzanine/Ventana/2018/09/4146/V4146_20180925T174651Z_h264.mp4 http://m3.shore.mbari.org/videos/M3/mezzanine/Ventana/2018/02/4096/V4096_20180213T194931Z_h264.mp4 http://m3.shore.mbari.org/videos/M3/mezzanine/DocRicketts/2018/10/1088/D1088_20181020T180924Z_h264.mp4"
 * @author Brian Schlining
 * @since 2019-06-03T11:15:00
 */
public class MultiOpenDemo01 {

    public static void main(String[] args) throws Exception {

        Logger log = LoggerFactory.getLogger(VideoInfoTest.class);


        String prog = MultiOpenDemo01.class.getName();
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



        long interval = urls.size() * 1000;
        for (URL url: urls) {
            log.info("Opening " + url);
            Thread t = new Thread(() -> {
                try {
                    SharktopodaVideoIO io = new SharktopodaVideoIO(UUID.randomUUID(), "localhost", port);

                    io.getVideoInfoSubject().forEach(vi -> System.out.println(vi));
                    io.send(new OpenCmd(url));
                    io.send(RemoteCommands.REQUEST_VIDEO_INFO);
                    Thread.sleep(interval + 2000);
                    io.send(VideoCommands.PLAY);
                    Thread.sleep(interval);
                    for (int i = 1; i < 6; i++) {
                        double rate = 1 - (1D / ((Integer) i).doubleValue());
                        System.out.println("----" + rate);
                        io.send(RemoteCommands.SHOW);
                        io.send(new ShuttleCmd(rate));
                        Thread.sleep(interval);
                    }
                    Thread.sleep(interval);
                    io.send(VideoCommands.STOP);
                    Thread.sleep(interval);
                    io.send(RemoteCommands.CLOSE);
                }
                catch (Exception e) {
                    log.error("Thread died", e);
                }

            });
            t.setDaemon(false);
            t.start();
        }

    }

}
