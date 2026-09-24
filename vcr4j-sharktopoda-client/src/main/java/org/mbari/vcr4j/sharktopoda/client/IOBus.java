/*
 * Copyright © 2008 MBARI (brian@mbari.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mbari.vcr4j.sharktopoda.client;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

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
