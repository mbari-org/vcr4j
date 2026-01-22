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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import org.junit.Test;
import org.mbari.vcr4j.commands.RemoteCommands;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.*;
import org.mbari.vcr4j.remote.player.VideoControl;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class CommandsTest {

    private static RemoteControl remoteControl;
    private static VideoControl videoControl;

    private static UUID uuid = UUID.randomUUID();

    @BeforeClass
    public static void setup() throws Exception {

        videoControl = new VideoControl.Builder()
                .port(8889)
                .build()
                .get();

        remoteControl = new RemoteControl.Builder(uuid)
                .port(5001)
                .remotePort(8889)
                .build()
                .get();

        Thread.sleep(200);

    }

    @AfterClass
    public static void teardown() {
        videoControl.close();
        remoteControl.close();
    }


    @Test
    public void test() throws Exception {
        var url = new URL("http://iwantmy.mtv/movie.mp4");
        var commands = List.of(new OpenCmd(uuid, url),
                VideoCommands.PLAY,
                VideoCommands.FAST_FORWARD,
                VideoCommands.REWIND,
                VideoCommands.PAUSE,
                VideoCommands.PLAY,
                VideoCommands.REQUEST_ELAPSED_TIME,
                VideoCommands.REQUEST_INDEX,
                VideoCommands.REQUEST_STATUS,
                VideoCommands.REQUEST_DEVICE_TYPE,
                VideoCommands.REQUEST_TIMECODE,
                VideoCommands.REQUEST_TIMESTAMP,
                RemoteCommands.FRAMEADVANCE,
                RemoteCommands.REQUEST_ALL_VIDEO_INFOS,
                RemoteCommands.REQUEST_VIDEO_INFO,
                RemoteCommands.SHOW,
                RemoteCommands.PING,
                new PlayCmd(uuid, -2.0),
                new PlayCmd(uuid, -2.0),
                new ShuttleCmd(0.02),
                new ShuttleCmd(-0.02),
                new SeekElapsedTimeCmd(Duration.ofMillis(1000)),
                new PlayCmd(uuid, 0.01),
                new SeekElapsedTimeCmd(Duration.ofMillis(10000)),
                new PlayCmd(uuid, 0.01),
                new FrameCaptureCmd(uuid, UUID.randomUUID(), File.createTempFile("trashme", ".png").getAbsolutePath()),
                new SeekElapsedTimeCmd(Duration.ofMillis(1000)),
                new PlayCmd(uuid, 0.01),
                new OpenCmd(uuid, url),
                RemoteCommands.CLOSE);

        for (var cmd: commands) {
            try {
                remoteControl.getVideoIO().send(cmd);
            }
            catch (Exception e) {
                fail("Failed with exception: " + e.getMessage());
            }
        }

        for (var cmd: commands) {
            try {
                videoControl.getLifeCycle()
                        .get()
                        .get()
                        .send(cmd);
            }
            catch (Exception e) {
                fail("Failed with exception: " + e.getMessage());
            }
        }
    }
}
