package org.mbari.vcr4j.remote.control;

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
import org.mbari.vcr4j.remote.control.commands.CloseCmd;
import org.mbari.vcr4j.remote.control.commands.FrameAdvanceCmd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class RVideoIOCloseTest {

    /**
     * The command subject is serialized: send() from one thread while another thread is
     * mid-emission (blocked in sendCommand waiting for the player's UDP response) only
     * QUEUES the command and returns. If close() then disposes the internal subscribers
     * right away, the queued command is silently dropped. For a controller app the
     * dropped command is typically the CloseCmd, so the video stays open in the player.
     *
     * The fake player here acks everything except "frame advance", so an in-flight
     * frame-advance reliably occupies the emitter loop while the CloseCmd is sent.
     */
    @Test
    public void closeDeliversQueuedCommands() throws Exception {
        int remotePort;
        try (var s = new DatagramSocket(0)) {
            remotePort = s.getLocalPort();
        }

        var closeReceived = new CountDownLatch(1);
        var playerSocket = new DatagramSocket(remotePort);
        var player = new Thread(() -> {
            var buffer = new byte[4096];
            try {
                while (!playerSocket.isClosed()) {
                    var packet = new DatagramPacket(buffer, buffer.length);
                    playerSocket.receive(packet);
                    var msg = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    if (msg.contains("\"close\"")) {
                        closeReceived.countDown();
                    }
                    if (!msg.contains("frame advance")) {
                        var ack = "{}".getBytes(StandardCharsets.UTF_8);
                        playerSocket.send(new DatagramPacket(ack, ack.length,
                                packet.getAddress(), packet.getPort()));
                    }
                }
            }
            catch (Exception e) {
                // Socket closed; test is over
            }
        }, "fake-player");
        player.setDaemon(true);
        player.start();

        try {
            var uuid = UUID.randomUUID();
            var io = new RVideoIO(uuid, "localhost", remotePort);

            // Occupy the serialized command subject: this send blocks ~1s waiting for
            // an ack the fake player deliberately withholds
            var inFlight = new Thread(() -> io.send(new FrameAdvanceCmd(uuid)));
            inFlight.setDaemon(true);
            inFlight.start();
            Thread.sleep(500);

            io.send(new CloseCmd(uuid)); // queued behind the in-flight frame advance
            io.close();                  // must not drop the queued CloseCmd

            assertTrue("The CloseCmd queued before close() never reached the player",
                    closeReceived.await(10, TimeUnit.SECONDS));
        }
        finally {
            playerSocket.close();
        }
    }
}
