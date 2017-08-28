package org.mbari.vcr4j.decorators;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.commands.VideoCommands;

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

    private Disposable disposable;

    // Kill the timer when the commandSubject gets closed
    Observer<VideoCommand> observer = new Observer<VideoCommand>() {
        @Override
        public void onComplete() {
            timer.cancel();
        }

        @Override
        public void onError(Throwable throwable) {
            unsubscribe();
        }

        @Override
        public void onNext(VideoCommand videoCommand) {

        }

        @Override
        public void onSubscribe(Disposable disposable) {
            VideoSyncDecorator.this.disposable = disposable;
        }
    };

    public VideoSyncDecorator(VideoIO<S, E> io) {
        this(io, 1000, 333);
    }

    public VideoSyncDecorator(VideoIO<S, E> io, long statusInterval, long indexInterval) {
        io.getCommandSubject().subscribe(observer);

        TimerTask statusTask = new TimerTask() {
            @Override
            public void run() {
                io.send(VideoCommands.REQUEST_STATUS);
            }
        };
        timer.schedule(statusTask, 0, statusInterval);

        TimerTask timecodeTask = new TimerTask() {
            @Override
            public void run() {
                io.send(VideoCommands.REQUEST_INDEX);
            }
        };
        timer.schedule(timecodeTask, 0, indexInterval);
    }

    protected Timer getTimer() {
        return timer;
    }

    @Override
    public void unsubscribe() {
        timer.cancel();
        disposable.dispose();
    }
}
