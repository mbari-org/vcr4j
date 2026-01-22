package org.mbari.vcr4j.remote;

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
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.commands.RemoteCommands;
import org.mbari.vcr4j.remote.player.VideoControl;

import java.util.UUID;

public class PingTest {


    @Test
    public void testPing() throws Exception {

        var videoControl = new VideoControl.Builder()
                .port(8989)
                .build()
                .get();

        var remoteControl = new RemoteControl.Builder(UUID.randomUUID())
                .port(5002)
                .remotePort(8989)
                .build()
                .get();
        Thread.sleep(200);
        remoteControl.getVideoIO().send(RemoteCommands.PING);
        Thread.sleep(200);
        videoControl.getLifeCycle()
                .get()
                .get()
                .send(RemoteCommands.PING);
        Thread.sleep(200);
        remoteControl.close();
    }




}
