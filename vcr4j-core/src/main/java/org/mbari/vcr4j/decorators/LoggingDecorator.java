package org.mbari.vcr4j.decorators;

import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.VideoCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscriber;

/**
 * Logs the errors, indices, state, and commands for a VideoIO
 * @author Brian Schlining
 * @since 2016-01-29T11:13:00
 */
public class LoggingDecorator<S extends VideoState, E extends VideoError> implements Decorator {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final Subscriber<E> errorSubscriber = new Subscriber<E>() {
        @Override
        public void onCompleted() {
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
    };

    protected final Subscriber<VideoIndex> indexSubscriber = new Subscriber<VideoIndex>() {
        @Override
        public void onCompleted() {
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
    };

    protected final Subscriber<S> stateSubscriber = new Subscriber<S>() {
        @Override
        public void onCompleted() {
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
    };

    protected final Subscriber<VideoCommand> commandSubscriber = new Subscriber<VideoCommand>() {
        @Override
        public void onCompleted() {
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
    };


    public LoggingDecorator(VideoIO<S, E> io) {
        io.getCommandSubject().subscribe(commandSubscriber);
        io.getErrorObservable().subscribe(errorSubscriber);
        io.getStateObservable().subscribe(stateSubscriber);
        io.getIndexObservable().subscribe(indexSubscriber);
    }

    @Override
    public void unsubscribe() {
        errorSubscriber.unsubscribe();
        stateSubscriber.unsubscribe();
        indexSubscriber.unsubscribe();
        commandSubscriber.unsubscribe();
    }
}
