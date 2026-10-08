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

import org.mbari.vcr4j.decorators.Decorator;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.decorators.StatusDecorator;
import org.mbari.vcr4j.decorators.VideoSyncDecorator;
import org.mbari.vcr4j.remote.control.commands.ConnectCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureDoneCmd;
import org.mbari.vcr4j.remote.control.commands.OpenDoneCmd;
import org.mbari.vcr4j.remote.player.PlayerIO;
import org.mbari.vcr4j.remote.player.RxControlRequestHandler;
import org.mbari.vcr4j.util.Preconditions;


import java.io.Closeable;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * RemoteControl is for applications that need to send commands to a video player.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RemoteControl implements Closeable {

    private final RVideoIO videoIO;

    private final PlayerIO playerIO;

    // Held so close() can call unsubscribe(); otherwise decorators wired up in the builder
    // would be unreachable and their subscriptions/timers could only be torn down by
    // completing the underlying subjects via videoIO.close().
    private final List<Decorator> decorators;

    private RemoteControl(RVideoIO videoIO,
                         PlayerIO playerIO,
                         List<Decorator> decorators) {
        this.videoIO = videoIO;
        this.playerIO = playerIO;
        this.decorators = List.copyOf(decorators);
    }

    public RVideoIO getVideoIO() {
        return videoIO;
    }

    public PlayerIO getPlayerIO() {
        return playerIO;
    }

    public RxControlRequestHandler getRequestHandler() {
        return (RxControlRequestHandler) playerIO.getRequestHandler();
    }

    @Override
    public void close() {
        decorators.forEach(Decorator::unsubscribe);
        videoIO.close();
        playerIO.close();
        getRequestHandler().close();
    }


    /**
     * Builder for constructing a RemoteControl.
     * <pre>
     *   var remoteControl = new RemoteControl.Builder()
     *     .remotePort(8888)                // The port the video player is listening to. Default is 8888
     *     .port(5000)                      // The port the video player can send its commands to. Default is 8899
     *     .withLogging(true)               // Enable command logging. Default is false
     *     .withMonitoring(false)           // Enable timers to track status/timecode from video player. Default is false
     *     .withStatus(true)                // Send status command when a command is sent that can change state. Default is false
     *     .selfHost("10.0.0.5")            // The host the video player sends its commands to. Default is resolved (see below)
     *     .whenFrameCaptureIsDone(cmd -> {}); // What to do when a frame grab has been taken
     *     .build()
     * </pre>
     *
     * <p>The video player sends its commands (e.g. {@code frame capture done}, {@code open done})
     * back to the host given in the {@code connect} command. Unless set with
     * {@link #selfHost(String)}, it is resolved when {@link #build()} is called using
     * {@link #resolveSelfHost(String, int)}: the local address the OS would use to reach the
     * video player. This is a loopback address when the video player is on the same machine.</p>
     */
    public static class Builder {

        private static final System.Logger log = System.getLogger(Builder.class.getName());

        private final UUID uuid;
        private int remotePort = 8888;
        private String remoteHost = "localhost";
        private int port = 8899;
        private String selfHost; // null means resolve it in build()
        private Consumer<FrameCaptureDoneCmd> frameCaptureDoneFn = (f) -> {};
        private Consumer<OpenDoneCmd.Request> openDoneFn = (r) -> {};

        private boolean withMonitoring = false;
        private boolean withLogging = false;

        private boolean withStatus = false;

        public Builder(UUID uuid) {
            Preconditions.checkArgument(uuid != null, "UUID is required");
            this.uuid = uuid;
        }

        public Builder remotePort(int port) {
            remotePort = port;
            return this;
        }

        public Builder remoteHost(String host) {
            remoteHost = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        /**
         * @param host The host (name or address) of this machine that the video player should
         *             send its commands to. Use this when the resolved default is not reachable
         *             from the video player (e.g. NAT). {@code null} restores the default.
         */
        public Builder selfHost(String host) {
            this.selfHost = host;
            return this;
        }


        public Builder withMonitoring(boolean monitor) {
            withMonitoring = monitor;
            return this;
        }

        public Builder withLogging(boolean log) {
            withLogging = log;
            return this;
        }

        public Builder withStatus(boolean status) {
            withStatus = status;
            return this;
        }

        public Builder whenFrameCaptureIsDone(Consumer<FrameCaptureDoneCmd> fn) {
            frameCaptureDoneFn = fn;
            return this;
        }

        public Builder whenOpenIsDone(Consumer<OpenDoneCmd.Request> fn) {
            openDoneFn = fn;
            return this;
        }


        public Optional<RemoteControl> build() {

            log.log(System.Logger.Level.DEBUG,
                    () -> "Building. Listening on port " + port + ". Sending commands to " +
                            remoteHost + ":" + remotePort);

            try {
                var videoIo = new RVideoIO(uuid, remoteHost, remotePort);
                var player = new RxControlRequestHandler(frameCaptureDoneFn, openDoneFn);
                var playerIo = new PlayerIO(port, player);

                var decorators = new ArrayList<Decorator>();
                if (withMonitoring) {
                    decorators.add(new VideoSyncDecorator<>(videoIo));
                }
                if (withLogging) {
                    decorators.add(new LoggingDecorator<>(videoIo));
                }
                if (withStatus) {
                    decorators.add(new StatusDecorator<>(videoIo));
                }

                var remoteControl = new RemoteControl(videoIo, playerIo, decorators);

                var host = selfHost == null ? resolveSelfHost(remoteHost, remotePort) : selfHost;
                log.log(System.Logger.Level.DEBUG,
                        () -> "Asking the video player to send its commands to " + host + ":" + port);
                videoIo.send(new ConnectCmd(port, host, uuid));
                return Optional.of(remoteControl);
            }
            catch (Exception e) {
                log.log(System.Logger.Level.WARNING, "Failed to build RemoteControl", e);
                return Optional.empty();
            }

        }

        /**
         * Finds the address of this machine that the video player can send commands back to.
         *
         * <p>The machine's host name is not used first because it may resolve (via DNS) to an
         * address that isn't currently this machine, e.g. a laptop's office address while it is
         * on a home network or VPN. The video player's replies would then be lost.</p>
         *
         * <ol>
         *   <li>If the video player is on this machine (loopback), use the same loopback address.</li>
         *   <li>Otherwise use the local address the OS routes through to reach the video player.
         *       Connecting a UDP socket sends no packets; it only selects the route.</li>
         *   <li>Fall back to the host name, then {@code localhost}.</li>
         * </ol>
         *
         * @param remoteHost The video player's host
         * @param remotePort The video player's port
         * @return A host name or address literal for the {@code connect} command
         */
        public static String resolveSelfHost(String remoteHost, int remotePort) {
            try {
                var remote = InetAddress.getByName(remoteHost);
                if (remote.isLoopbackAddress()) {
                    return remote.getHostAddress();
                }
                try (var socket = new DatagramSocket()) {
                    socket.connect(remote, remotePort);
                    var local = socket.getLocalAddress();
                    if (local != null && !local.isAnyLocalAddress()) {
                        return local.getHostAddress();
                    }
                }
            }
            catch (Exception e) {
                log.log(System.Logger.Level.DEBUG,
                        "Unable to find a route to " + remoteHost + ". Falling back to the host name", e);
            }

            try {
                return InetAddress.getLocalHost().getHostName();
            }
            catch (UnknownHostException e) {
                return "localhost";
            }
        }
    }
}
