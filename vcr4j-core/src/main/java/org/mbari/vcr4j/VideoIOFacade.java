package org.mbari.vcr4j;

import rx.Observable;
import rx.subjects.Subject;

/**
 * Sometimes you need a VideoIO object that is an amalgam of observables from different sources.
 * This facade provides a way to wrap up a VideoIO object using disparate observables
 *
 * @author Brian Schlining
 * @since 2016-04-04T16:07:00
 */
public class VideoIOFacade<S extends VideoState, E extends VideoError> implements VideoIO<S, E> {

    private final String connectionID;
    private final Subject<VideoCommand, VideoCommand> commandSubject;
    private final Observable<E> errorObservable;
    private final Observable<S> stateObservable;
    private final Observable<VideoIndex> indexObservable;

    public VideoIOFacade(String connectionID,
            Subject<VideoCommand, VideoCommand> commandSubject,
            Observable<S> stateObservable,
            Observable<E> errorObservable,
            Observable<VideoIndex> indexObservable) {
        this.connectionID = connectionID;
        this.commandSubject = commandSubject;
        this.errorObservable = errorObservable;
        this.stateObservable = stateObservable;
        this.indexObservable = indexObservable;
    }

    @Override
    public Subject<VideoCommand, VideoCommand> getCommandSubject() {
        return commandSubject;
    }

    @Override
    public Observable<E> getErrorObservable() {
        return errorObservable;
    }

    @Override
    public Observable<VideoIndex> getIndexObservable() {
        return indexObservable;
    }

    @Override
    public Observable<S> getStateObservable() {
        return stateObservable;
    }

    @Override
    public void close() {
        commandSubject.onCompleted();
    }

    @Override
    public <A extends VideoCommand> void send(A videoCommand) {
        commandSubject.onNext(videoCommand);
    }

    @Override
    public String getConnectionID() {
        return connectionID;
    }
}
