package org.mbari.vcr4j.rs422.decorators;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.Subject;
import org.mbari.util.NumberUtilities;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.decorators.Decorator;
import org.mbari.vcr4j.rs422.VCRVideoIO;
import org.mbari.vcr4j.rs422.commands.RS422VideoCommands;
import org.mbari.vcr4j.rs422.commands.RequestUserbitsAsTimeCmd;

import java.time.Instant;
import java.util.Optional;

/**
 * Decorator that listens for RequestUserbitsAsTimeCmds. Usage:
 * <pre>
 * VideoIO&lt;RS422State, RS422Error&gt; io = // ...
 * UserbitsAsTimeDecorator decorator = new UserbitsAsTimeDecorator(io);
 * decorator.getIndexObservable().subscribe(index -> doSomething());
 *
 * io.send(RequestUserbitsAsTimeCmd.COMMAND);
 * </pre>
 *
 * This classes indexObservable will have VideoIndexs with both timestamp and timecode fields
 * set.
 * @author Brian Schlining
 * @since 2016-01-29T15:06:00
 */
public class UserbitsAsTimeDecorator implements Decorator {

    private final Observable<VideoIndex> indexObservable;

    private final Observer<VideoCommand> commandObserver;

    private Disposable disposable;

    public UserbitsAsTimeDecorator(VCRVideoIO io) {

        final Subject<VideoCommand> commandSubject = io.getCommandSubject();

        commandObserver = new Observer<VideoCommand>() {
            @Override
            public void onComplete() {}

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onNext(VideoCommand videoCommand) {
                commandSubject.onNext(VideoCommands.REQUEST_TIMECODE);
                commandSubject.onNext(RS422VideoCommands.REQUEST_USERBITS);
            }

            @Override
            public void onSubscribe(Disposable disposable) {
                UserbitsAsTimeDecorator.this.disposable = disposable;
            }
        };


        // Watch for the special userbits requests and timestamp requests
        // Timestamp request are currently ignored by RS422VideoIO
        commandSubject.filter(vc -> vc == RequestUserbitsAsTimeCmd.COMMAND
                                 || vc == VideoCommands.REQUEST_TIMESTAMP)
                .subscribe(commandObserver);


        this.indexObservable = Observable.combineLatest(io.getIndexObservable(),
                io.getUserbitsObservable(),
                (videoIndex, userbits) ->
                        new VideoIndex(Optional.of(userbitsAsInstant(userbits.getUserbits())),
                                videoIndex.getElapsedTime(), videoIndex.getTimecode())
                ).distinctUntilChanged();

    }

    public static Instant userbitsAsInstant(byte[] userbits) {
        final int epochSeconds = NumberUtilities.toInt(userbits, true);
        return Instant.ofEpochSecond(epochSeconds);
    }

    /**
     * When using this decorator, use this indexObservable to get VideoIndexs that
     * containg both timecode and timestamp
     * @return An observable that contains both timecode and timestamp (calculated from userbits)
     */
    public Observable<VideoIndex> getIndexObservable() {
        return indexObservable;
    }

    @Override
    public void unsubscribe() {
        disposable.dispose();
    }
}
