package org.mbari.vcr4j.examples.udp;

import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.decorators.SchedulerVideoIO;
import org.mbari.vcr4j.udp.TimeServer;
import org.mbari.vcr4j.udp.UDPError;
import org.mbari.vcr4j.udp.UDPState;
import org.mbari.vcr4j.udp.UDPVideoIO;

import java.util.concurrent.Executors;

/**
 * This adds a twist to a SimpleDemo. By default, all IO done by a VideoIO object is executed
 * on the calling thread. This is rarely exactly what you want, especially when running
 * VideoIO in a UI. We have a class `SchedulerVideoIO` that decorates any VideoIO object. The
 * SchedulerVideoIO will execute all IO on it's own internal thread. The responses are propagate
 * through the observables on threads determined by an Executor or Scheduler that you provide
 * through it's constructor.
 *
 * @author Brian Schlining
 * @since 2016-02-11T16:51:00
 */
public class SchedulerDemo {

    public static void main(String[] args) throws Exception {
        // --- Start up our demo server that just produces local time as a timecode
        TimeServer timeServer = new TimeServer(9000);
        timeServer.start();

        UDPVideoIO rawIO = new UDPVideoIO("localhost", 9000);
        VideoIO<UDPState, UDPError> io = new SchedulerVideoIO<>(rawIO, Executors.newCachedThreadPool());

        LoggingDecorator decorator = new LoggingDecorator<>(io); // Logs for our Scheduled IO
        //LoggingDecorator decorator1 = new LoggingDecorator(rawIO); // Logs for the underlying raw IO
        VideoController controller = new VideoController(io); // Wrap io with a standard control

        controller.requestStatus();
        controller.requestTimecode();
        controller.requestTimestamp();
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
