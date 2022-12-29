package org.mbari.vcr4j.examples.purejavacomm;

import org.docopt.Docopt;
import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.decorators.VCRSyncDecorator;
import org.mbari.vcr4j.purejavacomm.PJCVideoIO;
import org.mbari.vcr4j.rs422.decorators.RS422LoggingDecorator;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Brian Schlining
 * @since 2016-02-03T14:11:00
 */
public class SimpleDemo02 {

    public static void main(String[] args) throws Exception {
        String prog = SimpleDemo02.class.getName();
        String doc = "Usage: " + prog + " <commport> [options]\n\n" +
                "Options:\n" +
                "  -h, --help";
        Map<String, Object> opts = new Docopt(doc).parse(args);

        String portName = (String) opts.get("<commport>");

        PJCVideoIO io = PJCVideoIO.open(portName); // Open serial port
        LoggingDecorator loggingDecorator = new RS422LoggingDecorator(io); // Log everything

        // This decorator starts a lot of chatter with the device. Useful
        // for keeping timecode and status in sync with a VCR
        VCRSyncDecorator syncDecorator = new VCRSyncDecorator(io);

        VideoController controller = new VideoController(io); // Wrap io with a standard control
        controller.play();
        controller.requestStatus();

        // Wait until video starts playing
        AtomicBoolean wait = new AtomicBoolean(true);
        io.getStateObservable()
                .filter(state -> state.isPlaying() && !state.isStandingBy())
                .take(1)
                .forEach(state -> wait.set(false));
        while (wait.get()) {
            Thread.sleep(200);
        }

        controller.requestTimecode();
        Thread.sleep(4000);
        controller.stop();
        Thread.sleep(1000);
        io.close();
        System.exit(0);

    }
}
