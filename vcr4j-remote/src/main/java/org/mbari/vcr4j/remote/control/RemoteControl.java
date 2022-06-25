package org.mbari.vcr4j.remote.control;

import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.decorators.VCRSyncDecorator;
import org.mbari.vcr4j.remote.control.commands.ConnectCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureDoneCmd;
import org.mbari.vcr4j.remote.player.PlayerIO;
import org.mbari.vcr4j.remote.player.RxControlRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class RemoteControl {

    private final RVideoIO videoIO;
    private final RxControlRequestHandler requestHandler;

    private final PlayerIO playerIO;

    private RemoteControl(RVideoIO videoIO,
                         PlayerIO playerIO,
                         Consumer<FrameCaptureDoneCmd> frameCaptureDoneFn) {
        this.videoIO = videoIO;
        this.playerIO = playerIO;
        this.requestHandler = new RxControlRequestHandler(frameCaptureDoneFn);
    }

    public RVideoIO getVideoIO() {
        return videoIO;
    }

    public PlayerIO getPlayerIO() {
        return playerIO;
    }

    public RxControlRequestHandler getRequestHandler() {
        return requestHandler;
    }


    public static class Builder {

        private static final Logger log = LoggerFactory.getLogger(Builder.class);

        private final UUID uuid;
        private int vcrPort = 8888;
        private String vcrHost = "localhost";
        private int selfPort = 8899;
        private String selfHost;
        private Consumer<FrameCaptureDoneCmd> frameCaptureDoneFn = (f) -> {};

        private boolean withMonitoring = false;
        private boolean withLogging = false;

        public Builder(UUID uuid) {
            this.uuid = uuid;
            try {
                this.selfHost = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                this.selfHost = "localhost";
            }
        }

        public Builder vcrPort(int port) {
            vcrPort = port;
            return this;
        }

        public Builder vcrHost(String host) {
            vcrHost = host;
            return this;
        }

        public Builder selfPort(int port) {
            selfPort = port;
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

        public Builder whenFrameCaptureIsDone(Consumer<FrameCaptureDoneCmd> fn) {
            frameCaptureDoneFn = fn;
            return this;
        }



        public Optional<RemoteControl> build() {
            try {
                var videoIo = new RVideoIO(uuid, vcrHost, vcrPort);
                var player = new RxControlRequestHandler(frameCaptureDoneFn);
                var playerIo = new PlayerIO(selfPort, player);

                var remoteControl = new RemoteControl(videoIo, playerIo, frameCaptureDoneFn);

                if (withMonitoring) {
                    new VCRSyncDecorator<>(videoIo);
                }
                if (withLogging) {
                    new LoggingDecorator<>(videoIo);
                }

                videoIo.send(new ConnectCmd(selfPort, selfHost));
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
