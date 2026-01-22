package org.mbari.vcr4j;

/*-
 * #%L
 * vcr4j-core
 * %%
 * Copyright (C) 2008 - 2026 Monterey Bay Aquarium Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.Subject;

/**
 * Sometimes you need a VideoIO object that is an amalgam of observables from different sources.
 * This class provides a way assemble a VideoIO object using disparate observables
 *
 * @author Brian Schlining
 * @since 2016-04-04T16:07:00
 */
public class SimpleVideoIO<S extends VideoState, E extends VideoError> implements VideoIO<S, E> {

    private final String connectionID;
    private final Subject<VideoCommand<?>> commandSubject;
    private final Observable<E> errorObservable;
    private final Observable<S> stateObservable;
    private final Observable<VideoIndex> indexObservable;

    public SimpleVideoIO(String connectionID,
            Subject<VideoCommand<?>> commandSubject,
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
    public Subject<VideoCommand<?>> getCommandSubject() {
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
        commandSubject.onComplete();
    }

    @Override
    public <A extends VideoCommand<?>> void send(A videoCommand) {
        commandSubject.onNext(videoCommand);
    }

    @Override
    public String getConnectionID() {
        return connectionID;
    }
}
