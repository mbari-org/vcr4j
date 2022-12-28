package org.mbari.vcr4j.examples.sharktopoda;

import org.docopt.Docopt;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.decorators.Decorator;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.decorators.VideoSyncDecorator;
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

/**
 * @author Brian Schlining
 * @since 2016-08-29T11:35:00
 */
public class VARSDemo01 {

    private static VideoIO<SharktopodaState, SharktopodaError> io;

    public static void main(String[] args) throws  Exception {
        String prog = VARSDemo01.class.getName();
        String doc = "Usage: " + prog + " <port> <url>\n" +
                "Options:\n" +
                "  -h, --help";

        Map<String, Object> opts = new Docopt(doc).parse(args);

        Integer port = Integer.parseInt((String) opts.get("<port>"));
        URL url = new URL((String) opts.get("<url>"));

        Logger log = LoggerFactory.getLogger(VARSDemo01.class);

        // --- Open Video
        io = new SharktopodaVideoIO(UUID.randomUUID(), "localhost", port);

        // --- Decorate VideoIO like we would in VARS
        // VARS uses this decorator to keep the UI in sync with the VCR state and timecode
        // it uses a timer to regularly schedule requests.
        Decorator syncDecorator = new VideoSyncDecorator<>(io);
        new LoggingDecorator(io);

        // --- Execute Play and just see what the traffic does.
        io.send(new OpenCmd(url));
        io.send(VideoCommands.PLAY);

        Thread.sleep(2000);

        // --- Shut down
        // Unsubscibe from observables when stopping. Not totally nexcessary but makes shutdown a bit cleaner.
        // e.g. Fewer interrupted exceptions form the VCRSyncDecorator
        log.info("STOPPING DEVICE");
        io.send(VideoCommands.STOP);
        syncDecorator.unsubscribe();


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
