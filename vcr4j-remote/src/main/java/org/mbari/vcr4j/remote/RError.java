package org.mbari.vcr4j.remote;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoError;

import java.util.Optional;

public class RError implements VideoError {

    private final boolean connectionError;
    private final boolean parserError;
    private final boolean unknownError;
    private final VideoCommand<?> videoCommand;

    private String message;

    public RError(boolean connectionError,
                  boolean parserError,
                  boolean unknownError,
                  VideoCommand<?> videoCommand,
                  String message) {
        this.connectionError = connectionError;
        this.parserError = parserError;
        this.unknownError = unknownError;
        this.videoCommand = videoCommand;
        this.message = message;
    }

    public RError(boolean connectionError, boolean parserError, boolean unknownError, VideoCommand<?> videoCommand) {
        this(connectionError, parserError, unknownError, videoCommand, null);
    }

    public RError(boolean connectionError, boolean parserError, boolean unknownError) {
        this(connectionError, parserError, unknownError, null, null);
    }

    @Override
    public boolean hasError() {
        return connectionError || parserError ||unknownError;
    }

    @Override
    public Optional<VideoCommand<?>> getVideoCommand() {
        return Optional.ofNullable(videoCommand);
    }

    public boolean isConnectionError() {
        return connectionError;
    }

    public boolean isParseError() {
        return  parserError;
    }

    public boolean isUnknownError() {
        return unknownError;
    }

    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }
}
