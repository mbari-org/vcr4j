package org.mbari.vcr4j.remote.player;

import org.mbari.vcr4j.remote.control.RemoteControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Optional;

public class VideoControl implements Closeable {

    private final PlayerIO playerIO;

    private final RVideoIOLifeCycle lifeCycle;

    private final RxPlayerRequestHandler requestHandler;

    private VideoControl(PlayerIO io, RxPlayerRequestHandler requestHandler, boolean withLogging) {
        this.requestHandler = requestHandler;
        playerIO = io;
        lifeCycle = requestHandler.getLifeCycle();
    }

    @Override
    public void close() {
        playerIO.close();
        lifeCycle.disconnect();
        getRequestHandler().close();
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

        private int port = 8888;

        private boolean withLogging = false;

        private VideoController videoController = new NoopVideoController();

        public Builder port(int port) {
            this.port = port;
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
                var playerIo = new PlayerIO(port, requestHandler);
                var videoControl = new VideoControl(playerIo, requestHandler, withLogging);
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
