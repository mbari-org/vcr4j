package org.mbari.vcr4j.decorators;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.commands.VideoCommands;
import rx.Subscriber;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This decorator schedules requests for status and video index frequently for keepin a UI's
 * state in sync with the video
 * @author Brian Schlining
 * @since 2016-08-29T16:23:00
 */
public class VideoSyncDecorator<S extends VideoState, E extends VideoError> implements Decorator {

    private Timer timer = new Timer(getClass().getSimpleName() + "-" + System.currentTimeMillis(), true);


    // Kill the timer when the commandSubject gets closed
    Subscriber<VideoCommand> subscriber = new Subscriber<VideoCommand>() {
        @Override
        public void onCompleted() {
            timer.cancel();
        }

        @Override
        public void onError(Throwable throwable) {
            unsubscribe();
        }

        @Override
        public void onNext(VideoCommand videoCommand) {

        }
    };

    public VideoSyncDecorator(VideoIO<S, E> io) {

        io.getCommandSubject().subscribe(subscriber);

        TimerTask statusTask = new TimerTask() {
            @Override
            public void run() {
                io.send(VideoCommands.REQUEST_STATUS);
            }
        };
        timer.schedule(statusTask, 0, 1000);

        TimerTask timecodeTask = new TimerTask() {
            @Override
            public void run() {
                io.send(VideoCommands.REQUEST_INDEX);
            }
        };
        timer.schedule(timecodeTask, 0, 333);


    }

    protected Timer getTimer() {
        return timer;
    }

    @Override
    public void unsubscribe() {
        timer.cancel();
        subscriber.unsubscribe();
    }
}
