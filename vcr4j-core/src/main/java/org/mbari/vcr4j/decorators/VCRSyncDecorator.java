package org.mbari.vcr4j.decorators;

/*-
 * #%L
 * vcr4j-core
 * %%
 * Copyright (C) 2008 - 2026 Monterey Bay Aquarium Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.commands.VideoCommands;

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
public class VCRSyncDecorator<S extends VideoState, E extends VideoError> implements Decorator {

    private Timer timer = new Timer(getClass().getSimpleName() + "-" + System.currentTimeMillis(), true);

    private Disposable disposable;

    // Kill the timer when the commandSubject gets closed
    Observer<VideoCommand<?>> subscriber = new Observer<>() {
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
            VCRSyncDecorator.this.disposable = disposable;
        }
    };

    public VCRSyncDecorator(VideoIO<S, E> io) {
        this(io, 1000, 40, 500);
    }

    public VCRSyncDecorator(VideoIO<S, E> io, long statusInterval, long timecodeInterval,  long timestampInterval) {
        io.getCommandSubject().subscribe(subscriber);

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
                io.send(VideoCommands.REQUEST_TIMECODE);
            }
        };
        timer.schedule(timecodeTask, 0, timecodeInterval);

        // Since timecode is the primary index, we ask for timestamps much less frequently.
        // Most devices don't support it anyway. But we use it at MBARI as we write time
        // to userbits. (Which requires the use of vcr4j-rs422's UserbitsAsTimeDecorator)
        TimerTask timestampTask = new TimerTask() {
            @Override
            public void run() {
                io.send(VideoCommands.REQUEST_TIMESTAMP);
            }
        };
        timer.schedule(timestampTask, 0, timestampInterval);
    }

    protected Timer getTimer() {
        return timer;
    }

    @Override
    public void unsubscribe() {
        timer.purge();
        timer.cancel();
        disposable.dispose();
    }
}
