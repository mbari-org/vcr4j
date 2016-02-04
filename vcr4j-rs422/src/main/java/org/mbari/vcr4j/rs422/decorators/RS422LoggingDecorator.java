package org.mbari.vcr4j.rs422.decorators;

import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.rs422.IRS422VideoIO;
import org.mbari.vcr4j.rs422.RS422Error;
import org.mbari.vcr4j.rs422.RS422State;
import org.mbari.vcr4j.rs422.RS422Timecode;
import org.mbari.vcr4j.rs422.RS422Userbits;
import org.mbari.vcr4j.rs422.RS422VideoIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscriber;

/**
 * @author Brian Schlining
 * @since 2016-02-03T12:08:00
 */
public class RS422LoggingDecorator extends LoggingDecorator<RS422State, RS422Error> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    Subscriber<RS422Timecode> timecodeSubscriber = new Subscriber<RS422Timecode>() {
        @Override
        public void onCompleted() {
            log.debug("Timecode observable is closed");
        }

        @Override
        public void onError(Throwable throwable) {
            log.debug("An error occurred in the timecode observable", throwable);
        }

        @Override
        public void onNext(RS422Timecode timecode) {
            if (log.isDebugEnabled()) {
                log.debug("Received: " + new RS422TimecodeAsString(timecode).toString());
            }
        }
    };

    public RS422LoggingDecorator(IRS422VideoIO io) {
        super(io);
        io.getTimecodeObservable().subscribe(timecodeSubscriber);

    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        timecodeSubscriber.unsubscribe();

    }
}
