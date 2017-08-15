package org.mbari.vcr4j.kipro.decorators;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.kipro.QuadError;
import org.mbari.vcr4j.kipro.QuadState;


/**
 * @author Brian Schlining
 * @since 2016-02-11T13:23:00
 */
public class QuadLoggingDecorator extends LoggingDecorator<QuadState, QuadError> {

    private Disposable disposable;

    protected final Observer<QuadError> quadErrorSubscriber = new Observer<QuadError>() {
        @Override
        public void onComplete() {
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

        @Override
        public void onSubscribe(Disposable disposable) {
            QuadLoggingDecorator.this.disposable = disposable;
        }
    };

    public QuadLoggingDecorator(VideoIO<QuadState, QuadError> io) {
        super(io);
        io.getErrorObservable().subscribe(quadErrorSubscriber);
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        disposable.dispose();
    }


}
