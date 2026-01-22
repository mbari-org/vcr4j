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
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.VideoCommand;

import java.util.ArrayList;
import java.util.List;


/**
 * Logs the errors, indices, state, and commands for a VideoIO
 * @author Brian Schlining
 * @since 2016-01-29T11:13:00
 */
public class LoggingDecorator<S extends VideoState, E extends VideoError> implements Decorator {

//    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final System.Logger log = System.getLogger(getClass().getName());

    List<Disposable> disposables = new ArrayList<>();

    protected final Observer<E> errorSubscriber = new Observer<>() {
        @Override
        public void onComplete() {
            log.log(System.Logger.Level.DEBUG, "Error observable is closed");
        }

        @Override
        public void onError(Throwable throwable) {
            log.log(System.Logger.Level.DEBUG, "An error occurred in the error observable", throwable);
        }

        @Override
        public void onNext(E error) {
            log.log(System.Logger.Level.DEBUG, () -> "Received: " + new VideoErrorAsString(error));
        }

        @Override
        public void onSubscribe(Disposable disposable) {
            disposables.add(disposable);
        }
    };

    protected final Observer<VideoIndex> indexSubscriber = new Observer<>() {
        @Override
        public void onComplete() {
            log.log(System.Logger.Level.DEBUG, "Index observable is closed");
        }

        @Override
        public void onError(Throwable throwable) {
            log.log(System.Logger.Level.DEBUG, "An error occurred in the index observable", throwable);
        }

        @Override
        public void onNext(VideoIndex index) {
            log.log(System.Logger.Level.DEBUG, () -> "Received: " + new VideoIndexAsString(index));
        }

        @Override
        public void onSubscribe(Disposable disposable) {
            disposables.add(disposable);
        }
    };

    protected final Observer<S> stateSubscriber = new Observer<>() {
        @Override
        public void onComplete() {
            log.log(System.Logger.Level.DEBUG, "State observable is closed");
        }

        @Override
        public void onError(Throwable throwable) {
            log.log(System.Logger.Level.DEBUG, "An error occurred in the state observable", throwable);
        }

        @Override
        public void onNext(S state) {
            log.log(System.Logger.Level.DEBUG, () -> "Received: " + new VideoStateAsString(state));
        }

        @Override
        public void onSubscribe(Disposable disposable) {
            disposables.add(disposable);
        }
    };

    protected final Observer<VideoCommand> commandSubscriber = new Observer<VideoCommand>() {
        @Override
        public void onComplete() {
            log.log(System.Logger.Level.DEBUG, "State observable is closed");
        }

        @Override
        public void onError(Throwable throwable) {
            log.log(System.Logger.Level.DEBUG, "An error occurred in the state observable", throwable);
        }

        @Override
        public void onNext(VideoCommand command) {
            log.log(System.Logger.Level.DEBUG, () -> "Sending: " + new VideoCommandAsString(command).toString());
        }

        @Override
        public void onSubscribe(Disposable disposable) {
            disposables.add(disposable);
        }
    };


    public LoggingDecorator(VideoIO<S, E> io) {
        io.getCommandSubject().subscribe(commandSubscriber);
        io.getErrorObservable().subscribe(errorSubscriber);
        io.getStateObservable().subscribe(stateSubscriber);
        io.getIndexObservable().subscribe(indexSubscriber);
    }

    @Override
    public void unsubscribe() {
        disposables.forEach(Disposable::dispose);
    }
}
