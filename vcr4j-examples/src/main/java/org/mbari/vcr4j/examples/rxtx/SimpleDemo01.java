package org.mbari.vcr4j.examples.rxtx;

import com.sun.javafx.binding.Logging;
import org.docopt.Docopt;
import org.mbari.util.SystemUtilities;
import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.rs422.decorators.RS422LoggingDecorator;
import org.mbari.vcr4j.rxtx.RXTX;
import org.mbari.vcr4j.rxtx.RXTXVideoIO;

import java.util.Map;

/**
 * Demo that connects to a RS422 device using RXTX. Sends a play command and
 * then just requests status and timecode. A stop command is sent to the device
 * before exiting.
 */
public class SimpleDemo01 {
    

    public static void main(String[] args) throws Exception {

        String prog = SimpleDemo01.class.getName();
        String doc = "Usage: " + prog + " <commport> [options]\n\n" +
                "Options:\n" +
                "  -h, --help";
        Map<String, Object> opts = new Docopt(doc).parse(args);

        String portName = (String) opts.get("<commport>");

        RXTX.setup(); // sets up native libraries
        RXTXVideoIO io = RXTXVideoIO.open(portName); // Open serial port
        LoggingDecorator decorator = new RS422LoggingDecorator(io); // Log everything

        VideoController controller = new VideoController(io); // Wrap io with a standard control
        controller.requestStatus();
        controller.requestTimecode();
        controller.play();
        Thread.sleep(2000);
        controller.requestStatus();
        Thread.sleep(100);
        for (int i = 0; i < 10; i++) {
            controller.requestTimecode();
            Thread.sleep(400);
        }

        controller.stop();
        controller.requestStatus();
        controller.requestTimecode();
        io.close();

        System.exit(0);

    }
}