package org.mbari.vcr4j.examples.jserialcomm;

import org.docopt.Docopt;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.decorators.Decorator;
import org.mbari.vcr4j.decorators.VCRSyncDecorator;
import org.mbari.vcr4j.decorators.VideoIndexAsString;
import org.mbari.vcr4j.examples.rxtx.SimpleDemo02;
import org.mbari.vcr4j.jserialcomm.SerialCommVideoIO;
import org.mbari.vcr4j.rs422.RS422State;
import org.mbari.vcr4j.rs422.decorators.RS422StatusDecorator;
import org.mbari.vcr4j.rs422.decorators.UserbitsAsTimeDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Brian Schlining
 * @since 2016-04-08T10:42:00
 */
public class VARSDemo01 {

    public static void main(String[] args) throws Exception {
        String prog = SimpleDemo02.class.getName();
        String doc = "Usage: " + prog + " <commport> [options]\n\n" +
                "Options:\n" +
                "  -h, --help";
        Map<String, Object> opts = new Docopt(doc).parse(args);

        String portName = (String) opts.get("<commport>");

        Logger log = LoggerFactory.getLogger(VARSDemo01.class);

        SerialCommVideoIO io = SerialCommVideoIO.open(portName);

        // --- Decorate VideoIO like we would in VARS
        // VARS uses this decorator to keep the UI in sync with the VCR state and timecode
        // it uses a timer to regularly schedule requests.
        Decorator syncDecorator = new VCRSyncDecorator<>(io);

        // Some commands should do a status request immediately to update the UI state.
        // This decorator listens for those commands follows them up with a status update
        Decorator statusDecorator = new RS422StatusDecorator(io);

        // We write time to the userbits. This decorator adds userbits decoding and support
        // for Timestamp commands. Listen to it's observable for fancy indices
        UserbitsAsTimeDecorator timeDecorator = new UserbitsAsTimeDecorator(io);

        // Log updates to our fancy time index
        timeDecorator.getIndexObservable()
                .subscribe(vi -> log.debug("USERBITS: " + new VideoIndexAsString(vi).toString()));

        // --- Execute Play and just see what the traffic does.
        io.send(VideoCommands.PLAY);

        // Wait until video starts playing. Sometimes the VCR take a few seconds to get rolling
        AtomicBoolean wait = new AtomicBoolean(true);
        io.getStateObservable()
                .filter(RS422State::isPlaying)
                .take(1)
                .forEach(state -> {
                    log.info("THE DEVICE IS PLAYING");
                    wait.set(false);
                });

        while (wait.get()) {
            Thread.sleep(200);
        }

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
        syncDecorator.unsubscribe();
        statusDecorator.unsubscribe();
        timeDecorator.unsubscribe();

        // When the io object is closed exit
        io.getCommandSubject().subscribe(vc -> {}, e -> {}, () -> System.exit(0));

        // Close everything when the VCR stops
        io.getStateObservable()
                .filter(RS422State::isStopped)
                .take(1)
                .forEach(state -> io.close());
    }
}
