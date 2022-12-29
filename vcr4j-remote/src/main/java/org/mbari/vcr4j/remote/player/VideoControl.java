package org.mbari.vcr4j.remote.player;

import org.mbari.vcr4j.remote.control.RemoteControl;

import java.io.Closeable;
import java.util.Optional;

/**
 * VideoControl is for video player applications that need to recieve commands from a {@link RemoteControl}.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class VideoControl implements Closeable {

    private final PlayerIO playerIO;

    private final RVideoIOLifeCycle lifeCycle;

    private final RxPlayerRequestHandler requestHandler;

    private VideoControl(PlayerIO io) {
        playerIO = io;
        requestHandler = (RxPlayerRequestHandler) playerIO.getRequestHandler();
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
        return requestHandler;
    }


    /**
     * Builder for constructing a VideoControl.
     * <pre>
     *   var videoControl = new VideoControl.Builder(8888)
     *     .withLogging(false)
     *     .videoController(new NoopVideoController())
     *     .build();
     * </pre>
     */
    public static class Builder {

        private static final System.Logger log = System.getLogger(Builder.class.getName());

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
            log.log(System.Logger.Level.DEBUG, () -> "Building. Listening on port " + port + " using " + videoController);
            try {
                var lifeCycle = new RVideoIOLifeCycle(withLogging);
                var requestHandler = new RxPlayerRequestHandler(videoController, lifeCycle);
                var playerIo = new PlayerIO(port, requestHandler);
                var videoControl = new VideoControl(playerIo);
                return Optional.of(videoControl);
            }
            catch (Exception e) {
                log.log(System.Logger.Level.WARNING, "Failed to build RemoteControl", e);
                return Optional.empty();
            }
        }



    }
}
