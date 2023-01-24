package org.mbari.vcr4j.examples.sharktopoda;

import org.docopt.Docopt;
import org.mbari.vcr4j.commands.RemoteCommands;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.sharktopoda.SharktopodaState;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;
import org.mbari.vcr4j.sharktopoda.commands.FramecaptureCmd;
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd;
import org.mbari.vcr4j.sharktopoda.decorators.FramecaptureDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Brian Schlining
 * @since 2016-08-30T14:23:00
 */
public class FramecaptureTest {

    private static final int localPort = 6789;

    public static void main(String[] args) throws Exception {


        String prog = FramecaptureTest.class.getName();
        String doc = "Usage: " + prog + " <port> <url> <dir>\n" +
                "Options:\n" +
                "  -h, --help";

        Map<String, Object> opts = new Docopt(doc).parse(args);

        Integer port = Integer.parseInt((String) opts.get("<port>"));
        URL url = new URL((String) opts.get("<url>"));
        File file = new File((String) opts.get("<dir>"));

        file.mkdirs();

        Logger log = LoggerFactory.getLogger(SeekElapsedTimeTest.class);

        SharktopodaVideoIO io = new SharktopodaVideoIO(UUID.randomUUID(), "localhost", port);
        FramecaptureDecorator decorator = new FramecaptureDecorator(io, localPort);
        decorator.getFramecaptureObservable()
                .forEach(r -> System.out.println(r.getImageLocation()));

        // Wait until video starts playing. Sometimes the VCR take a few seconds to get rolling
        AtomicBoolean wait = new AtomicBoolean(true);
        io.getStateObservable()
                .filter(SharktopodaState::isConnected)
                .take(1)
                .forEach(state -> {
                    log.info("The video is ready.");
                    wait.set(false);
                });


        io.send(new OpenCmd(url));

        while(wait.get()) {
            Thread.sleep(1000);
        }

        //io.send(new ConnectCmd(localPort));
        io.send(new FramecaptureCmd(UUID.randomUUID(), new File(file, "trashme-0.png")));

        for (int i = 0; i < 5; i++) {
            io.send(new SeekElapsedTimeCmd(Duration.ofSeconds(i * 3)));
            Thread.sleep(1000);
            io.send(new FramecaptureCmd(UUID.randomUUID(), new File(file, "trashme-seek-" + i + ".png")));
        }

        io.send(VideoCommands.PLAY);
        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000);
            io.send(new FramecaptureCmd(UUID.randomUUID(), new File(file, "trashme-play-" + i + ".png")));
        }
        io.send(RemoteCommands.CLOSE);

    }


}
