package org.mbari.vcr4j.remote.player;

/*-
 * #%L
 * vcr4j-remote
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

import org.junit.Test;

import java.net.DatagramSocket;

public class PlayerIOCloseTest {

    /**
     * When the requested port is already in use, PlayerIO.init() logs the bind failure
     * and leaves its server socket null. close() must handle that instead of throwing a
     * NullPointerException — callers close the PlayerIO through RemoteControl.close()
     * during error cleanup, exactly when the socket is most likely to be null.
     * Regression test for the fix in fda51f5.
     */
    @Test
    public void closeDoesNotThrowWhenThePortWasAlreadyBound() throws Exception {
        try (var portBlocker = new DatagramSocket(0)) {
            var playerIO = new PlayerIO(portBlocker.getLocalPort(),
                    new RxControlRequestHandler(cmd -> {}));
            playerIO.close();
        }
    }
}
