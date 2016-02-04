package org.mbari.vcr4j.udp;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoError;

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2016-02-04T13:27:00
 */
public class UDPError implements VideoError {

    private final Optional<VideoCommand> videoCommand;
    private final boolean connectionError;
    private final boolean parserError;

    public UDPError(boolean connectionError, boolean parserError, Optional<VideoCommand> videoCommand) {
        this.connectionError = connectionError;
        this.parserError = parserError;
        this.videoCommand = videoCommand;
    }

    @Override
    public Optional<VideoCommand> getVideoCommand() {
        return videoCommand;
    }

    @Override
    public boolean hasError() {
        return connectionError || parserError;
    }

    public boolean isConnectionError() {
        return connectionError;
    }

    public boolean isParseError() {
        return  parserError;
    }


}
