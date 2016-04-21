package org.mbari.vcr4j.kipro.decorators;

import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.decorators.VideoErrorAsString;
import org.mbari.vcr4j.kipro.QuadError;
import org.mbari.vcr4j.kipro.QuadState;
import org.mbari.vcr4j.kipro.QuadVideoIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscriber;

/**
 * @author Brian Schlining
 * @since 2016-02-11T13:23:00
 */
public class QuadLoggingDecorator extends LoggingDecorator<QuadState, QuadError> {

    protected final Subscriber<QuadError> quadErrorSubscriber = new Subscriber<QuadError>() {
        @Override
        public void onCompleted() {
            log.debug("Error observable is closed");
        }

        @Override
        public void onError(Throwable throwable) {
            log.debug("An error occurred in the error observable", throwable);
        }

        @Override
        public void onNext(QuadError error) {
            if (log.isDebugEnabled()) {
                log.debug("Received: " + new QuadErrorAsString(error).toString());
            }
        }
    };

    public QuadLoggingDecorator(VideoIO<QuadState, QuadError> io) {
        super(io);
        errorSubscriber.unsubscribe();
        io.getErrorObservable().subscribe(quadErrorSubscriber);
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        quadErrorSubscriber.unsubscribe();
    }


}
