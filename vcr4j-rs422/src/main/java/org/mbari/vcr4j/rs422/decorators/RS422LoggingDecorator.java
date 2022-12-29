package org.mbari.vcr4j.rs422.decorators;


import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.rs422.VCRVideoIO;
import org.mbari.vcr4j.rs422.RS422Error;
import org.mbari.vcr4j.rs422.RS422State;
import org.mbari.vcr4j.rs422.RS422Timecode;


/**
 * @author Brian Schlining
 * @since 2016-02-03T12:08:00
 */
public class RS422LoggingDecorator extends LoggingDecorator<RS422State, RS422Error> {

    private Disposable disposable;

    private Observer<RS422Timecode> timecodeSubscriber = new Observer<>() {
        @Override
        public void onComplete() {
            log.log(System.Logger.Level.DEBUG, "Timecode observable is closed");
        }

        @Override
        public void onError(Throwable throwable) {
            log.log(System.Logger.Level.DEBUG, "An error occurred in the timecode observable", throwable);
        }

        @Override
        public void onNext(RS422Timecode timecode) {
            if (log.isLoggable(System.Logger.Level.DEBUG)) {
                log.log(System.Logger.Level.DEBUG, "Received: " + new RS422TimecodeAsString(timecode).toString());
            }
        }

        @Override
        public void onSubscribe(Disposable disposable) {
            RS422LoggingDecorator.this.disposable = disposable;
        }
    };

    public RS422LoggingDecorator(VCRVideoIO io) {
        super(io);
        io.getTimecodeObservable().subscribe(timecodeSubscriber);

    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        disposable.dispose();
    }
}
