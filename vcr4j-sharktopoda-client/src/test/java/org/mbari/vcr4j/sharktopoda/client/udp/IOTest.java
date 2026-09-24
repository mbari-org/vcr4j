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
package org.mbari.vcr4j.sharktopoda.client.udp;

import org.junit.Test;
import org.mbari.vcr4j.commands.RemoteCommands;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;
import org.mbari.vcr4j.sharktopoda.commands.ConnectCmd;
import org.mbari.vcr4j.sharktopoda.commands.FramecaptureCmd;
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd;
import org.mbari.vcr4j.sharktopoda.client.ClientController;
import org.mbari.vcr4j.sharktopoda.decorators.FramecaptureDecorator;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

public class IOTest {



    ClientController clientController = new MockClientController();


    @Test
    public void testCommands() throws Exception {
        int port = 5005;
        UUID uuid = UUID.randomUUID();
        IO io = new IO(clientController, port);
        SharktopodaVideoIO videoIO = new SharktopodaVideoIO(uuid, "localhost", port);
        videoIO.send(new OpenCmd(new URL("http://www.nowhere.org/movie.mp4")));
        videoIO.send(RemoteCommands.SHOW);
        videoIO.send(RemoteCommands.REQUEST_ALL_VIDEO_INFOS);
        videoIO.send(VideoCommands.PLAY);
        videoIO.send(VideoCommands.PAUSE);
        videoIO.send(VideoCommands.REQUEST_TIMESTAMP);
        videoIO.send(VideoCommands.REQUEST_ELAPSED_TIME);
        videoIO.send(new SeekElapsedTimeCmd(Duration.ofSeconds(10)));
        videoIO.send(RemoteCommands.FRAMEADVANCE);
        videoIO.send(RemoteCommands.REQUEST_VIDEO_INFO);
        videoIO.send(RemoteCommands.CLOSE);
        videoIO.close();
        io.close();
    }

    @Test
    public void testFramecapture() throws Exception {
        int port = 5006;
        int framecapturePort = 8123;
        UUID uuid = UUID.randomUUID();
        IO io = new IO(clientController, port);
        SharktopodaVideoIO videoIO = new SharktopodaVideoIO(uuid, "localhost", port);
        // Decorator handles framecapture stuff
        FramecaptureDecorator decorator = new FramecaptureDecorator(videoIO, framecapturePort);
        decorator.getFramecaptureObservable().subscribe(System.out::println);

        videoIO.send(new OpenCmd(new URL("http://www.nowhere.org/movie.mp4")));
        videoIO.send(new ConnectCmd(framecapturePort));
        videoIO.send(new FramecaptureCmd(UUID.randomUUID(), new File("/Foo")));
        videoIO.send(RemoteCommands.CLOSE);
        videoIO.close();
        io.close();
    }

    @Test
    public void testNoVideoFocused() throws Exception {
        ClientController controller = new BadMockClientController();
        int port = 5007;
        UUID uuid = UUID.randomUUID();
        IO io = new IO(clientController, port);
        SharktopodaVideoIO videoIO = new SharktopodaVideoIO(uuid, "localhost", port);
        videoIO.send(RemoteCommands.REQUEST_VIDEO_INFO);
        videoIO.close();
        io.close();
    }


}