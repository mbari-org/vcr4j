package org.mbari.vcr4j.examples.rxtx;

import org.docopt.Docopt;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.decorators.Decorator;
import org.mbari.vcr4j.decorators.VCRSyncDecorator;
import org.mbari.vcr4j.decorators.VideoIndexAsString;
import org.mbari.vcr4j.rs422.decorators.RS422StatusDecorator;
import org.mbari.vcr4j.rs422.decorators.UserbitsAsTimeDecorator;
import org.mbari.vcr4j.rxtx.RXTX;
import org.mbari.vcr4j.rxtx.RXTXVideoIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is notional example of how we might use this in our video annotation system
 * @author Brian Schlining
 * @since 2016-02-03T14:42:00
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

        RXTX.setup(); // sets up native libraries
        RXTXVideoIO io = RXTXVideoIO.open(portName); // Open serial port

        // VARS uses this decorator to keep the UI in sync with the VCR state and timecode
        // it uses a timer to regularly schedule requests.
        Decorator syncDecorator = new VCRSyncDecorator<>(io);

        // Some commands should do a status request immediantly to update the UI state.
        // This decorator listens for those commands follows them up with a status update
        Decorator statusDecorator = new RS422StatusDecorator(io);

        // We write time to the userbits. This decorator adds userbits decoding and support
        // for Timestamp commands. Listen to it's observable for fancy indices
        UserbitsAsTimeDecorator timeDecorator = new UserbitsAsTimeDecorator(io);
        timeDecorator.getIndexObservable()
                .subscribe(vi -> log.debug("USERBITS: " + new VideoIndexAsString(vi).toString()));


        io.send(VideoCommands.PLAY);
        // Wait until video starts playing
        AtomicBoolean wait = new AtomicBoolean(true);
        io.getStateObservable()
                .filter(VideoState::isPlaying)
                .take(1)
                .forEach(state -> wait.set(false));
        while (wait.get()) {
            Thread.sleep(200);
        }

        for (int i = 0; i < 10; i++) {
            io.send(VideoCommands.REQUEST_TIMESTAMP);
            Thread.sleep(1000);
        }

        io.send(VideoCommands.STOP);
        Thread.sleep(1000);
        io.close();
        System.exit(0);
    }
}
