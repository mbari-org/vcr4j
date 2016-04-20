package org.mbari.vcr4j.examples.kipro;

import org.docopt.Docopt;
import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.kipro.QuadError;
import org.mbari.vcr4j.kipro.QuadState;
import org.mbari.vcr4j.kipro.QuadVideoIO;
import org.mbari.vcr4j.kipro.decorators.QuadLoggingDecorator;

import java.util.Map;

/**
 *
 * Example:
 *  1. Configure Network on Quad: 50.1 -> Set to DHCP, 50.1 -> read ip address
 *  2. Run the following:
 *   mvn exec:java -Dexec.mainClass=org.mbari.vcr4j.examples.kipro.SimpleDemo01 -Dexec.args="http://134.89.11.144"
 *
 * @author Brian Schlining
 * @since 2016-02-11T13:13:00
 */
public class SimpleDemo01 {

    public static void main(String[] args) throws Exception {


        String prog = SimpleDemo01.class.getName();
        String doc = "Usage: " + prog + " <httpAddress> [options]\n\n" +
                "Options:\n" +
                "  -h, --help";
        Map<String, Object> opts = new Docopt(doc).parse(args);

        String httpAddress = (String) opts.get("<httpAddress>");
        QuadVideoIO io = QuadVideoIO.open(httpAddress);
        LoggingDecorator<QuadState, QuadError> decorator = new QuadLoggingDecorator(io);
        VideoController controller = new VideoController(io); // Wrap io with a standard control
        controller.requestStatus();  // Does nothing yet
        controller.requestTimecode();
        controller.play();          // Does nothing
        Thread.sleep(2000);
        controller.requestStatus();  // Does nothing yet.
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
