package org.mbari.vcr4j.remote.control;

import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.decorators.StatusDecorator;
import org.mbari.vcr4j.decorators.VCRSyncDecorator;
import org.mbari.vcr4j.decorators.VideoSyncDecorator;
import org.mbari.vcr4j.remote.control.commands.ConnectCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureDoneCmd;
import org.mbari.vcr4j.remote.player.PlayerIO;
import org.mbari.vcr4j.remote.player.RxControlRequestHandler;
import org.mbari.vcr4j.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

    private RemoteControl(RVideoIO videoIO,
                         PlayerIO playerIO,
                         Consumer<FrameCaptureDoneCmd> frameCaptureDoneFn) {
        this.videoIO = videoIO;
        this.playerIO = playerIO;
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
        videoIO.close();
        playerIO.close();
        getRequestHandler().close();
    }


    /**
     * Builder for constructing a RemoteControl.
     * {@snippet :
     *   var remoteControl = new RemoteControl.Builder()
     *     .remotePort(8888)                // The port the video player is listening to. Default is 8888
     *     .port(5000)                      // The port the video player can send its commands to. Default is 8899
     *     .withLogging(true)               // Enable command logging. Default is false
     *     .withMonitoring(false)           // Enable timers to track status/timecode from video player. Default is false
     *     .withStatus(true)                // Send status command when a command is sent that can change state. Default is false
     *     .whenFrameCaptureIsDone(cmd -> {}); // What to do when a frame grab has been taken
     *     .build()
     * }
     */
    public static class Builder {

        private static final Logger log = LoggerFactory.getLogger(Builder.class);

        private final UUID uuid;
        private int remotePort = 8888;
        private String remoteHost = "localhost";
        private int port = 8899;
        private String selfHost;
        private Consumer<FrameCaptureDoneCmd> frameCaptureDoneFn = (f) -> {};

        private boolean withMonitoring = false;
        private boolean withLogging = false;

        private boolean withStatus = false;

        public Builder(UUID uuid) {
            Preconditions.checkArgument(uuid != null, "UUID is required");
            this.uuid = uuid;
            try {
                this.selfHost = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                this.selfHost = "localhost";
            }
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


        public Optional<RemoteControl> build() {

            log.atDebug()
                    .log(() -> "Building. Listening on port " + port + ". Sending commands to " +
                            remoteHost + ":" + remotePort);

            try {
                var videoIo = new RVideoIO(uuid, remoteHost, remotePort);
                var player = new RxControlRequestHandler(frameCaptureDoneFn);
                var playerIo = new PlayerIO(port, player);

                var remoteControl = new RemoteControl(videoIo, playerIo, frameCaptureDoneFn);

                if (withMonitoring) {
                    new VideoSyncDecorator<>(videoIo);
                }
                if (withLogging) {
                    new LoggingDecorator<>(videoIo);
                }
                if (withStatus) {
                    new StatusDecorator<>(videoIo);
                }

                videoIo.send(new ConnectCmd(port, selfHost, uuid));
                return Optional.of(remoteControl);
            }
            catch (Exception e) {
                log.atWarn()
                        .setCause(e)
                        .log("Failed to build RemoteControl");
                return Optional.empty();
            }

        }
    }
}
