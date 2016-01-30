package org.mbari.vcr4j.rs422.decorators;

import org.mbari.util.NumberUtilities;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.rs422.RS422Userbits;
import org.mbari.vcr4j.rs422.commands.RS422VideoCommands;
import org.mbari.vcr4j.rs422.commands.RequestUserbitsAsTimeCmd;
import rx.Observable;
import rx.subjects.Subject;

import java.time.Instant;
import java.util.Optional;

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
 * @author Brian Schlining
 * @since 2016-01-29T15:06:00
 */
public class UserbitsAsTimeDecorator {

    private final Observable<VideoIndex> indexObservable;

    public UserbitsAsTimeDecorator(Subject<VideoCommand, VideoCommand> commandSubject,
            Observable<VideoIndex> indexObservable,
            Observable<RS422Userbits> userbitsObservable) {

        // Watch for request for userbits as time. When they occur, request both timecode
        // and userbits.
        commandSubject.filter(vc -> vc == RequestUserbitsAsTimeCmd.COMMAND)
                .subscribe(vc -> {
                    commandSubject.onNext(VideoCommands.REQUEST_TIMECODE);
                    commandSubject.onNext(RS422VideoCommands.REQUEST_USERBITS);
                });

        this.indexObservable = Observable.combineLatest(indexObservable,
                userbitsObservable,
                (videoIndex, userbits) ->
                        new VideoIndex(Optional.of(userbitsAsInstant(userbits.getUserbits())),
                                videoIndex.getElapsedTime(), videoIndex.getTimecode())
                ).distinctUntilChanged();

    }

    public static Instant userbitsAsInstant(byte[] userbits) {
        final int epochSeconds = NumberUtilities.toInt(userbits, true);
        return Instant.ofEpochSecond(epochSeconds);
    }

    public Observable<VideoIndex> getIndexObservable() {
        return indexObservable;
    }
}
