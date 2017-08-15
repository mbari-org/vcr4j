package org.mbari.vcr4j;



import io.reactivex.Observable;
import io.reactivex.subjects.Subject;

import java.io.Closeable;

public interface VideoIO<S extends VideoState, E extends VideoError> extends Closeable {



    <A extends VideoCommand> void send(A videoCommand);

    /**
     * We foresee needing to chain several VideoIO services together. The current plan is
     * to use RX to monitor commands sent between different services so that one may augment
     * anothers capabilities. For example, we may talk to a video player, but also need to
     * fetch information from a Media Asset Manager simultaneously to get recorded dates
     * for each frame.
     * @return A Subject that is the pipeline for all commands sent to the VideoIO provider
     */
    Subject<VideoCommand> getCommandSubject();


    String getConnectionID();

    void close();

    Observable<E> getErrorObservable();
    Observable<S> getStateObservable();
    Observable<VideoIndex> getIndexObservable();

}