package org.mbari.vcr4j.remote.player;

import org.mbari.vcr4j.remote.control.RemoteControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class VideoControl {

    private final PlayerIO playerIO;

    private final RVideoIOLifeCycle lifeCycle;

    private final RequestHandler requestHandler;

    private VideoControl(PlayerIO io, VideoController videoController, boolean withLogging) {
        lifeCycle = new RVideoIOLifeCycle(withLogging);
        requestHandler = io.getRequestHandler();
        playerIO = io;
    }

    public PlayerIO getPlayerIO() {
        return playerIO;
    }

    public RVideoIOLifeCycle getLifeCycle() {
        return lifeCycle;
    }

    public RxPlayerRequestHandler getRequestHandler() {
        return (RxPlayerRequestHandler) requestHandler;
    }

    public static class Builder {
        private static final Logger log = LoggerFactory.getLogger(RemoteControl.Builder.class);

        private int selfPort = 8888;

        private boolean withLogging = false;

        private VideoController videoController = new NoopVideoController();

        public Builder selfPort(int port) {
            selfPort = port;
            return this;
        }

        public Builder withLogging(boolean log) {
            withLogging = log;
            return this;
        }

        public Builder videoController(VideoController videoController) {
            this.videoController = videoController;
            return this;
        }

        public Optional<VideoControl> build() {
            try {
                var lifeCycle = new RVideoIOLifeCycle(withLogging);
                var requestHandler = new RxPlayerRequestHandler(videoController, lifeCycle);
                var playerIo = new PlayerIO(selfPort, requestHandler);
                var videoControl = new VideoControl(playerIo, videoController, withLogging);
                return Optional.of(videoControl);
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
