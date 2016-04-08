package org.mbari.vcr4j.kipro;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoError;

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2016-02-04T11:17:00
 */
public class QuadError implements VideoError {

    private final Optional<VideoCommand> videoCommand;
    private final boolean error;
    private final boolean connectionError;
    private final Optional<Throwable> exception;

    public QuadError(boolean error,
            boolean connectionError,
            Optional<VideoCommand> videoCommand,
            Optional<Throwable> exception) {
        this.connectionError = connectionError;
        this.videoCommand = videoCommand;
        this.error = error;
        this.exception = exception;
    }

    @Override
    public Optional<VideoCommand> getVideoCommand() {
        return videoCommand;
    }

    @Override
    public boolean hasError() {
        return error;
    }

    public boolean hasConnectionError() {
        return connectionError;
    }

    public Optional<Throwable> getException() {
        return exception;
    }
}
