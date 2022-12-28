package org.mbari.vcr4j;

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2016-03-25T10:59:00
 */
public class SimpleVideoError implements VideoError {

    private final Optional<VideoCommand<?>> videoCommand;
    private final boolean error;

    public SimpleVideoError(boolean error, VideoCommand<?> videoCommand) {
        this.error = error;
        this.videoCommand = Optional.ofNullable(videoCommand);
    }

    public SimpleVideoError(boolean error) {
        this(error, null);
    }

    @Override
    public Optional<VideoCommand<?>> getVideoCommand() {
        return videoCommand;
    }

    @Override
    public boolean hasError() {
        return error;
    }
}