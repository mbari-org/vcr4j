package org.mbari.vcr4j.sharktopoda.client;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * @author Brian Schlining
 * @since 2020-02-11T16:14:00
 */
public class IOBus {

    /**
     * Messages to the bus are on this subject
     */
    protected final Subject<Object> incoming;

    /**
     * Messages fro the controller are on this subject
     */
    protected final Subject<Object> outgoing;

    public IOBus() {
        this.incoming = PublishSubject.create().toSerialized();
        this.outgoing = PublishSubject.create().toSerialized();
    }

    public Subject<Object> getIncoming() {
        return incoming;
    }

    public Subject<Object> getOutgoing() {
        return outgoing;
    }


}
