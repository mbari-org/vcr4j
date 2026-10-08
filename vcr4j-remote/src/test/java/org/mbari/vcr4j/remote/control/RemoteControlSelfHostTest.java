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
package org.mbari.vcr4j.remote.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * The host sent in the {@code connect} command is where the video player sends
 * {@code frame capture done}, {@code open done}, etc. It previously defaulted to the machine's
 * host name, which can resolve (via DNS) to an address that isn't this machine, e.g. an office
 * address while on a home network/VPN. All of the player's replies were then lost.
 */
public class RemoteControlSelfHostTest {

    @Test
    public void localPlayerResolvesToLoopback() throws Exception {
        for (var host : new String[] {"localhost", "127.0.0.1", "::1"}) {
            var self = RemoteControl.Builder.resolveSelfHost(host, 8800);
            assertTrue(host + " -> " + self + " should be loopback",
                    InetAddress.getByName(self).isLoopbackAddress());
        }
    }

    @Test
    public void ipv4LoopbackResolvesToSameAddress() {
        assertEquals("127.0.0.1", RemoteControl.Builder.resolveSelfHost("127.0.0.1", 8800));
    }

    @Test
    public void remotePlayerResolvesToALocalInterfaceAddress() throws Exception {
        // Use one of this machine's own non-loopback addresses as the "remote" player, so the
        // test needs no network access. The result must be an address of this machine.
        var remote = nonLoopbackIpv4Address();
        assumeTrue("No non-loopback IPv4 interface", remote.isPresent());

        var self = RemoteControl.Builder.resolveSelfHost(remote.get().getHostAddress(), 8800);
        var selfAddress = InetAddress.getByName(self);
        assertNotNull(self + " should be an address of a local interface",
                NetworkInterface.getByInetAddress(selfAddress));
    }

    @Test
    public void unresolvableRemoteFallsBackToAHostName() {
        var self = RemoteControl.Builder.resolveSelfHost("no-such-host.invalid", 8800);
        assertNotNull(self);
        assertFalse(self.isBlank());
    }

    @Test
    public void defaultConnectSendsLoopbackHostForALocalPlayer() throws Exception {
        var connect = captureConnect(builder -> builder);
        var host = connect.get("host").getAsString();
        assertTrue("connect host " + host + " should be loopback, not the DNS host name",
                InetAddress.getByName(host).isLoopbackAddress());
    }

    @Test
    public void explicitSelfHostIsSentInConnect() throws Exception {
        var connect = captureConnect(builder -> builder.selfHost("10.1.2.3"));
        assertEquals("10.1.2.3", connect.get("host").getAsString());
    }

    @Test
    public void nullSelfHostRestoresTheDefault() throws Exception {
        var connect = captureConnect(builder -> builder.selfHost("10.1.2.3").selfHost(null));
        assertTrue(InetAddress.getByName(connect.get("host").getAsString()).isLoopbackAddress());
    }

    // -------------------------------------------------------------------------

    /**
     * Builds a RemoteControl pointed at a fake player on localhost and returns the
     * {@code connect} message it sends.
     */
    private static JsonObject captureConnect(
            java.util.function.UnaryOperator<RemoteControl.Builder> configure) throws Exception {
        try (var player = new DatagramSocket(0, InetAddress.getLoopbackAddress())) {
            player.setSoTimeout(5000);
            var thread = new Thread(() -> {
                try {
                    var builder = new RemoteControl.Builder(UUID.randomUUID())
                            .remoteHost("localhost")
                            .remotePort(player.getLocalPort())
                            .port(freePort());
                    configure.apply(builder).build().ifPresent(RemoteControl::close);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();

            var buffer = new byte[4096];
            var packet = new DatagramPacket(buffer, buffer.length);
            player.receive(packet);
            var raw = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

            // Acknowledge so the builder's send doesn't wait for its timeout
            var ack = "{\"response\":\"connect\",\"status\":\"ok\"}".getBytes(StandardCharsets.UTF_8);
            player.send(new DatagramPacket(ack, ack.length, packet.getAddress(), packet.getPort()));
            thread.join(5000);

            var json = JsonParser.parseString(raw).getAsJsonObject();
            assertEquals("connect", json.get("command").getAsString());
            return json;
        }
    }

    private static Optional<InetAddress> nonLoopbackIpv4Address() throws Exception {
        for (var nic : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (!nic.isUp() || nic.isLoopback()) {
                continue;
            }
            for (var address : Collections.list(nic.getInetAddresses())) {
                if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                    return Optional.of(address);
                }
            }
        }
        return Optional.empty();
    }

    private static int freePort() throws Exception {
        try (var socket = new DatagramSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
