package org.mbari.vcr4j.adapter.noop;


import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.VideoCommands;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import java.util.Optional;

/**
 * This is a No-op implementation that can be used as a place holder.
 * @author Brian Schlining
 * @since 2016-03-24T13:28:00
 */
public class NoopVideoIO implements VideoIO<NoopVideoState, NoopVideoError> {

    private final Subject<VideoCommand, VideoCommand> commandSubject = new SerializedSubject<>(PublishSubject.create());
    private final Subject<VideoIndex, VideoIndex> indexObservable = new SerializedSubject<>(PublishSubject.create());
    private final Subject<NoopVideoState, NoopVideoState> stateObservable = new SerializedSubject<>(PublishSubject.create());
    private final Subject<NoopVideoError, NoopVideoError> errorObservable = new SerializedSubject<>(PublishSubject.create());

    public static final NoopVideoState STATE = new NoopVideoState();
    public static final NoopVideoError ERROR = new NoopVideoError();
    public static final VideoIndex INDEX = new VideoIndex(Optional.empty(), Optional.empty(), Optional.empty());


    public NoopVideoIO() {
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
        commandSubject.onCompleted();
        indexObservable.onCompleted();
        stateObservable.onCompleted();
        errorObservable.onCompleted();
    }

    @Override
    public <A extends VideoCommand> void send(A videoCommand) {
        commandSubject.onNext(videoCommand);
    }

    @Override
    public Subject<VideoCommand, VideoCommand> getCommandSubject() {
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
