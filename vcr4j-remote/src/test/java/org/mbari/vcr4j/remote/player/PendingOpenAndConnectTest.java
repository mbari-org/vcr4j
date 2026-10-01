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
package org.mbari.vcr4j.remote.player;

import org.junit.Test;
import org.mbari.vcr4j.remote.TestUtil;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.RResponse;
import org.mbari.vcr4j.remote.control.commands.localization.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.ClearLocalizationsCmd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class PendingOpenAndConnectTest {

    /** Takes as long as the test lets it to open, and only knows the video once it is open. */
    private static class SlowOpenController extends NoopVideoController {
        final CountDownLatch release = new CountDownLatch(1);
        volatile boolean open = false;

        @Override
        public boolean hasVideo(UUID videoUuid) {
            return open;
        }

        @Override
        public VideoResult openVideo(UUID videoUuid, URL url) {
            try {
                release.await(10, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            open = true;
            return VideoResult.success();
        }
    }

    @Test
    public void localizationsForAVideoThatIsOpeningAreAcceptedAndDeliveredAfterTheOpen() throws Exception {
        var controller = new SlowOpenController();
        var handler = new RxPlayerRequestHandler(controller, new RVideoIOLifeCycle(false));
        var uuid = UUID.randomUUID();
        var delivered = Collections.synchronizedList(new ArrayList<String>());
        handler.getLocalizationsCmdObservable().subscribe(c -> delivered.add(c.getName() + "/open=" + controller.open));

        var open = handler.handleOpen(new OpenCmd.Request(uuid, new URL("file:/tmp/video.mp4")));
        assertEquals(RResponse.OK, open.getStatus());

        var add = handler.handleAddLocalizationsRequest(new AddLocalizationsCmd.Request(uuid, TestUtil.newLocalizations(1)));
        var clear = handler.handleClearLocalizationsRequest(new ClearLocalizationsCmd.Request(uuid));
        assertEquals(RResponse.OK, add.getStatus());
        assertEquals(RResponse.OK, clear.getStatus());
        assertTrue("Nothing should be delivered while the video is opening", delivered.isEmpty());

        controller.release.countDown();
        var deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (delivered.size() < 2 && System.nanoTime() < deadline) {
            Thread.sleep(20);
        }
        assertEquals(2, delivered.size());
        assertTrue(delivered.get(0), delivered.get(0).endsWith("open=true"));
        assertTrue(delivered.get(0), delivered.get(0).startsWith("add"));
        assertTrue(delivered.get(1), delivered.get(1).startsWith("clear"));
        handler.close();
    }

    @Test
    public void connectWithoutAHostPingsTheSender() throws Exception {
        var handler = new RxPlayerRequestHandler(new NoopVideoController(), new RVideoIOLifeCycle(false));
        try (var remote = new DatagramSocket(0, InetAddress.getLoopbackAddress())) {
            remote.setSoTimeout(5000);
            var request = new SimpleRequest("connect", "{\"command\":\"connect\",\"port\":" + remote.getLocalPort() + "}");
            request.setSender(InetAddress.getLoopbackAddress());

            var response = handler.composeResponse(request);
            assertEquals(RResponse.OK, response.getStatus());

            var packet = new DatagramPacket(new byte[1024], 1024);
            remote.receive(packet);
            var message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
            assertTrue(message, message.contains("\"ping\""));
        }
        finally {
            handler.close();
        }
    }
}
