package org.mbari.vcr4j.examples.udp;

import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.udp.TimeServer;
import org.mbari.vcr4j.udp.UDPVideoIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Brian Schlining
 * @since 2016-02-04T15:07:00
 */
public class UDPClientServerDemo {

    public static void main(String[] args) throws Exception{


        // --- Start up our demo server that just produces local time as a timecode
        TimeServer timeServer = new TimeServer(9000);
        timeServer.start();

        // --- Configure the UPDVideoIO
        UDPVideoIO io = new UDPVideoIO("localhost", 9000);
        LoggingDecorator decorator = new LoggingDecorator<>(io);
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

        controller.stop();  // Does nothing as our UDP client just reports timecode. NO VCR Control
        controller.requestStatus();
        controller.requestTimecode();
        io.close();
        timeServer.stop();
        System.exit(0);

    }
}
