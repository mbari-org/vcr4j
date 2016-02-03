package org.mbari.vcr4j.decorators;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.commands.VideoCommands;
import rx.Observable;
import rx.Subscriber;
import rx.subjects.Subject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This decorator keeps the state, timecodes, and timestamps up to date by scheduling requests
 * frequently. This is particularly one is handy for keeping UI's up to date with the VCR and
 * is designed for devices that use timecode as their primary index.
 *
 * @author Brian Schlining
 * @since 2016-02-02T14:03:00
 */
public class VCRSyncDecorator<S extends VideoState, E extends VideoError> {

    private Timer timer = new Timer(getClass().getSimpleName() + "-" + System.currentTimeMillis(), true);

    public VCRSyncDecorator(VideoIO<S, E> io) {

        // Kill the timer when the commandSubject gets close
        Subscriber<VideoCommand> subscriber = new Subscriber<VideoCommand>() {
            @Override
            public void onCompleted() {
                timer.cancel();
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(VideoCommand videoCommand) {

            }
        };


        io.getCommandSubject().subscribe(subscriber);

        final Observable<S> stateObservable = io.getStateObservable();

        TimerTask statusTask = new TimerTask() {
            @Override
            public void run() {
                stateObservable.take(1).forEach(s -> {
                    if (s.isConnected()) {
                        io.send(VideoCommands.REQUEST_STATUS);
                    }
                });
            }
        };
        timer.schedule(statusTask, 0, 1000);

        TimerTask timecodeTask = new TimerTask() {
            @Override
            public void run() {
                stateObservable.take(1).forEach(s -> {
                    if (s.isConnected()) {
                        io.send(VideoCommands.REQUEST_TIMECODE);
                    }
                });
            }
        };
        timer.schedule(timecodeTask, 0, 40);

        // Since timecode is the primary index, we ask for timestamps much less frequently.
        // Most devices don't support it anyway. But we use it at MBARI as we write time
        // to userbits. (Which requires the use of vcr4j-rs422's UserbitsAsTimeDecorator)
        TimerTask timestampTask = new TimerTask() {
            @Override
            public void run() {
                stateObservable.take(1).forEach(s -> {
                    if (s.isConnected()) {
                        io.send(VideoCommands.REQUEST_TIMESTAMP);
                    }
                });
            }
        };
        timer.schedule(timestampTask, 0, 500);

    }


}
