package org.mbari.vcr4j.examples.kipro;

import org.docopt.Docopt;
import org.mbari.vcr4j.SimpleVideoIO;
import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.decorators.Decorator;
import org.mbari.vcr4j.decorators.SchedulerVideoIO;
import org.mbari.vcr4j.kipro.QuadError;
import org.mbari.vcr4j.kipro.QuadState;
import org.mbari.vcr4j.kipro.QuadVideoIO;
import org.mbari.vcr4j.kipro.decorators.ConnectionPollingDecorator;
import org.mbari.vcr4j.kipro.decorators.QuadLoggingDecorator;
import rx.Observable;

import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author Brian Schlining
 * @since 2016-04-21T11:50:00
 */
public class VARSDemo01 {

    public static void main(String[] args) throws Exception {
        String prog = VARSDemo01.class.getName();
        String doc = "Usage: " + prog + " <httpAddress> [options]\n\n" +
                "Options:\n" +
                "  -h, --help";
        Map<String, Object> opts = new Docopt(doc).parse(args);

        String httpAddress = (String) opts.get("<httpAddress>");

        QuadVideoIO rawIO = QuadVideoIO.open(httpAddress);
        VideoIO<QuadState, QuadError> scheduledIO = new SchedulerVideoIO<>(rawIO, Executors.newCachedThreadPool());
        new ConnectionPollingDecorator(scheduledIO);

        // For the UI we need to filter videoIndices that don't have timecode (or the UI
        // show --:--:--:-- every few seconds
        Observable<VideoIndex> indexObservable = scheduledIO.getIndexObservable()
                .filter(vi -> vi.getTimecode().isPresent());
        VideoIO<QuadState, QuadError> io = new SimpleVideoIO<>(scheduledIO.getConnectionID(),
                scheduledIO.getCommandSubject(),
                scheduledIO.getStateObservable(),
                scheduledIO.getErrorObservable(),
                indexObservable);
        new QuadLoggingDecorator(io);

        VideoController controller = new VideoController(io); // Wrap io with a standard control
        controller.requestStatus();  // Does nothing yet
        controller.requestTimecode();
        controller.play();          // Does nothing
        Thread.sleep(1000);
        controller.requestStatus();  // Does nothing yet.
        Thread.sleep(100);
        for (int i = 0; i < 20; i++) {
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
