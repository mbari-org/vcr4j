package org.mbari.vcr4j.adapter.noop;


import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.VideoCommands;

import java.util.Optional;

/**
 * This is a No-op implementation that can be used as a place holder.
 * @author Brian Schlining
 * @since 2016-03-24T13:28:00
 */
public class NoopVideoIO implements VideoIO<NoopVideoState, NoopVideoError> {

    private final Subject<VideoCommand> commandSubject;
    private final Subject<VideoIndex> indexObservable;
    private final Subject<NoopVideoState> stateObservable;
    private final Subject<NoopVideoError> errorObservable;


    public static final NoopVideoState STATE = new NoopVideoState();
    public static final NoopVideoError ERROR = new NoopVideoError();
    public static final VideoIndex INDEX = new VideoIndex(Optional.empty(), Optional.empty(), Optional.empty());


    public NoopVideoIO() {

        PublishSubject<VideoCommand> s1 = PublishSubject.create();
        commandSubject = s1.toSerialized();
        PublishSubject<VideoIndex> s2 = PublishSubject.create();
        indexObservable = s2.toSerialized();
        PublishSubject<NoopVideoState> s3 = PublishSubject.create();
        stateObservable = s3.toSerialized();
        PublishSubject<NoopVideoError> s4 = PublishSubject.create();
        errorObservable = s4.toSerialized();

        commandSubject.filter(vc -> vc.equals(VideoCommands.REQUEST_STATUS))
                .forEach(vc -> stateObservable.onNext(STATE));

        commandSubject.filter(vc -> vc.equals(VideoCommands.REQUEST_INDEX)
                || vc.equals(VideoCommands.REQUEST_ELAPSED_TIME)
                || vc.equals(VideoCommands.REQUEST_TIMECODE)
                || vc.equals(VideoCommands.REQUEST_TIMESTAMP))
                .forEach(vc -> indexObservable.onNext(INDEX));


    }

    @Override
    public void close() {
        commandSubject.onComplete();
        indexObservable.onComplete();
        stateObservable.onComplete();
        errorObservable.onComplete();
    }

    @Override
    public <A extends VideoCommand> void send(A videoCommand) {
        commandSubject.onNext(videoCommand);
    }

    @Override
    public Subject<VideoCommand> getCommandSubject() {
        return commandSubject;
    }

    @Override
    public String getConnectionID() {
        return "not connected";
    }

    @Override
    public Observable<NoopVideoError> getErrorObservable() {
        return errorObservable;
    }

    @Override
    public Observable<NoopVideoState> getStateObservable() {
        return stateObservable;
    }

    @Override
    public Observable<VideoIndex> getIndexObservable() {
        return indexObservable;
    }
}
