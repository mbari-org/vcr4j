package org.mbari.vcr4j.rs422.decorators;

import org.mbari.util.NumberUtilities;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.decorators.Decorator;
import org.mbari.vcr4j.rs422.RS422Userbits;
import org.mbari.vcr4j.rs422.RS422VideoIO;
import org.mbari.vcr4j.rs422.commands.RS422VideoCommands;
import org.mbari.vcr4j.rs422.commands.RequestUserbitsAsTimeCmd;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.Subject;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Decorator that listens for RequestUserbitsAsTimeCmds. Usage:
 * <pre>
 * VideoIO<RS422State, RS422Error> io = // ...
 * UserbitsAsTimeDecorator decorator = new UserbitsAsTimeDecorator(io.getCommandSubject(),
 *         io.getIndexObservable(), io.getUserbitsObservable());
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

    private final Subscriber<VideoCommand> commandSubscriber;

    public UserbitsAsTimeDecorator(RS422VideoIO io) {

        final Subject<VideoCommand, VideoCommand> commandSubject = io.getCommandSubject();

        commandSubscriber = new Subscriber<VideoCommand>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onNext(VideoCommand videoCommand) {
                commandSubject.onNext(VideoCommands.REQUEST_TIMECODE);
                commandSubject.onNext(RS422VideoCommands.REQUEST_USERBITS);
            }
        };


        // Watch for the special userbits requests and timestamp requests
        // Timestamp request are currently ignored by RS422VideoIO
        commandSubject.filter(vc -> vc == RequestUserbitsAsTimeCmd.COMMAND
                                 || vc == VideoCommands.REQUEST_TIMESTAMP)
                .subscribe(commandSubscriber);


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
     * @return
     */
    public Observable<VideoIndex> getIndexObservable() {
        return indexObservable;
    }

    @Override
    public void unsubscribe() {
        commandSubscriber.unsubscribe();
    }
}
