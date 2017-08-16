package org.mbari.vcr4j.examples.sharktopoda;

import org.docopt.Docopt;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.decorators.*;
import org.mbari.vcr4j.sharktopoda.SharktopodaError;
import org.mbari.vcr4j.sharktopoda.SharktopodaState;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd;
import org.mbari.vcr4j.sharktopoda.commands.SharkCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * @author Brian Schlining
 * @since 2017-08-15T15:04:00
 */
public class VARSDemo02 {

    public static void main(String[] args) throws  Exception {
        String prog = VARSDemo02.class.getName();
        String doc = "Usage: " + prog + " <port> <url>\n" +
                "Options:\n" +
                "  -h, --help";

        Map<String, Object> opts = new Docopt(doc).parse(args);

        Integer port = Integer.parseInt((String) opts.get("<port>"));
        URL url = new URL((String) opts.get("<url>"));

        Logger log = LoggerFactory.getLogger(org.mbari.vcr4j.examples.rxtx.VARSDemo01.class);

        // --- Open Video
        VideoIO<SharktopodaState, SharktopodaError> videoIO = new SharktopodaVideoIO(UUID.randomUUID(), "localhost", port);

        // --- Decorate VideoIO like we would in VARS
        // VARS uses this decorator to keep the UI in sync with the VCR state and timecode
        // it uses a timer to regularly schedule requests.
        new StatusDecorator<>(videoIO);
        new VCRSyncDecorator<>(videoIO, 1000, 100, 3000000);
        new LoggingDecorator(videoIO);
        VideoIO<SharktopodaState, SharktopodaError> io =
                new SchedulerVideoIO<>(videoIO, Executors.newCachedThreadPool());

        // --- Execute Play and just see what the traffic does.
        io.send(new OpenCmd(url));
        io.send(SharkCommands.SHOW);
        io.send(VideoCommands.PLAY);

        Thread.sleep(2000);

        // When we grab annotations in VARS we need to get the video index. Requesting a timestamp when
        // The UserbitsAsTimeDecorator is attached will request both a timecode and userbits.
        log.info("DEVICE IS IN PLAY MODE. REQUESTING TIME AT 1 SEC INTERVALS");
        for (int i = 0; i < 5; i++) {
            io.send(VideoCommands.REQUEST_TIMESTAMP);
            Thread.sleep(1000);
        }

        // --- Shut down
        // Unsubscibe from observables when stopping. Not totally nexcessary but makes shutdown a bit cleaner.
        // e.g. Fewer interrupted exceptions form the VCRSyncDecorator
        log.info("STOPPING DEVICE");
        io.send(VideoCommands.STOP);


        // When the io object is closed exit
        io.getCommandSubject().subscribe(vc -> {}, e -> {}, () -> System.exit(0));

        // Close everything when the VCR stops
        io.getStateObservable()
                .filter(VideoState::isStopped)
                .take(1)
                .forEach(state -> {
                    io.send(SharkCommands.CLOSE);
                    io.close();
                });

        io.send(VideoCommands.REQUEST_STATUS);

    }


}
