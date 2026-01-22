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

import java.io.Closeable;

public interface VideoIO<S extends VideoState, E extends VideoError> extends Closeable {


    /**
     * Send a command to the video player
     * @param videoCommand The command to send
     * @param <A> The type of the video Command
     */
    <A extends VideoCommand<?>> void send(A videoCommand);

    /**
     * We foresee needing to chain several VideoIO services together. The current plan is
     * to use RX to monitor commands sent between different services so that one may augment
     * anothers capabilities. For example, we may talk to a video player, but also need to
     * fetch information from a Media Asset Manager simultaneously to get recorded dates
     * for each frame.
     * @return A Subject that is the pipeline for all commands sent to the VideoIO provider
     */
    Subject<VideoCommand<?>> getCommandSubject();


    /**
     *
     * @return A string unique to the particular video io connection
     */
    String getConnectionID();

    /**
     * Close the video io and free it's resources.
     */
    void close();

    /**
     * All errors that occur will be sent to this observable
     * @return stream of errors
     */
    Observable<E> getErrorObservable();

    /**
     * Listen to this observable for responses to status requests
     * @return Status stream
     */
    Observable<S> getStateObservable();

    /**
     *
     * @return THis observable streams timecode/elapsedtime/timestamp responses.
     */
    Observable<VideoIndex> getIndexObservable();

}
