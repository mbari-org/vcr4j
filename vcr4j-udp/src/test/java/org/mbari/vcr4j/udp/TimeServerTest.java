package org.mbari.vcr4j.udp;

/*-
 * #%L
 * vcr4j-udp
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
import static org.junit.Assert.*;
import java.net.SocketException;

/**
 * @author Brian Schlining
 * @since 2016-02-04T15:25:00
 */
public class TimeServerTest {

    @Test
    public void testServer() {
        try {
            TimeServer server = new TimeServer(9000);
            server.start();
            Thread.sleep(200);
            server.stop();
        }
        catch (SocketException e) {
            fail("An exception occurred with the Socket: " + e.getMessage());
        }
        catch (InterruptedException e) {
            fail("An interruption occurred while running a simple socket test");
        }

    }
}
