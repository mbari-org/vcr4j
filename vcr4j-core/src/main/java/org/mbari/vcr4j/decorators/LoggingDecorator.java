package org.mbari.vcr4j.decorators;


import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.VideoCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Logs the errors, indices, state, and commands for a VideoIO
 * @author Brian Schlining
 * @since 2016-01-29T11:13:00
 */
public class LoggingDecorator<S extends VideoState, E extends VideoError> implements Decorator {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    List<Disposable> disposables = new ArrayList<>();

    protected final Observer<E> errorSubscriber = new Observer<E>() {
        @Override
        public void onComplete() {
            log.debug("Error observable is closed");
        }

        @Override
        public void onError(Throwable throwable) {
            log.debug("An error occurred in the error observable", throwable);
        }

        @Override
        public void onNext(E error) {
            if (log.isDebugEnabled()) {
                log.debug("Received: " + new VideoErrorAsString(error).toString());
            }
        }

        @Override
        public void onSubscribe(Disposable disposable) {
            disposables.add(disposable);
        }
    };

    protected final Observer<VideoIndex> indexSubscriber = new Observer<VideoIndex>() {
        @Override
        public void onComplete() {
            log.debug("Index observable is closed");
        }

        @Override
        public void onError(Throwable throwable) {
            log.debug("An error occurred in the index observable", throwable);
        }

        @Override
        public void onNext(VideoIndex index) {
            if (log.isDebugEnabled()) {
                log.debug("Received: " + new VideoIndexAsString(index).toString());
            }
        }

        @Override
        public void onSubscribe(Disposable disposable) {
            disposables.add(disposable);
        }
    };

    protected final Observer<S> stateSubscriber = new Observer<S>() {
        @Override
        public void onComplete() {
            log.debug("State observable is closed");
        }

        @Override
        public void onError(Throwable throwable) {
            log.debug("An error occurred in the state observable", throwable);
        }

        @Override
        public void onNext(S state) {
            if (log.isDebugEnabled()) {
                log.debug("Received: " + new VideoStateAsString(state).toString());
            }
        }

        @Override
        public void onSubscribe(Disposable disposable) {
            disposables.add(disposable);
        }
    };

    protected final Observer<VideoCommand> commandSubscriber = new Observer<VideoCommand>() {
        @Override
        public void onComplete() {
            log.debug("State observable is closed");
        }

        @Override
        public void onError(Throwable throwable) {
            log.debug("An error occurred in the state observable", throwable);
        }

        @Override
        public void onNext(VideoCommand command) {
            if (log.isDebugEnabled()) {
                log.debug("Sending: " + new VideoCommandAsString(command).toString());
            }
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
