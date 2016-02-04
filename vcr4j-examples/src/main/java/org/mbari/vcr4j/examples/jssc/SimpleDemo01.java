package org.mbari.vcr4j.examples.jssc;

import org.docopt.Docopt;
import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.jssc.JSSCVideoIO;
import org.mbari.vcr4j.rs422.decorators.RS422LoggingDecorator;

import java.util.Map;

/**
 * @author Brian Schlining
 * @since 2016-02-04T09:55:00
 */
public class SimpleDemo01 {

    public static void main(String[] args) throws Exception{
        String prog = SimpleDemo01.class.getName();
        String doc = "Usage: " + prog + " <commport> [options]\n\n" +
                "Options:\n" +
                "  -h, --help";
        Map<String, Object> opts = new Docopt(doc).parse(args);

        String portName = (String) opts.get("<commport>");

        JSSCVideoIO io = JSSCVideoIO.open(portName);
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
