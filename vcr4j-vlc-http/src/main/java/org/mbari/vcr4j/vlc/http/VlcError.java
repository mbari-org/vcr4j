package org.mbari.vcr4j.vlc.http;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoError;

import java.util.Optional;

public class VlcError implements VideoError {

    private final boolean error;
    private VideoCommand command;
    private Throwable throwable;

    public VlcError(boolean error) {
        this.error = error;
    }

    public VlcError(Throwable throwable) {
        this.error = true;
        this.throwable = throwable;
    }

    public VlcError(boolean error, VideoCommand cmd) {
        this.error = error;
        command = cmd;
    }

    public VlcError(Throwable throwable, VideoCommand cmd) {
        this.error = true;
        command = cmd;
        this.throwable = throwable;
    }

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public Optional<VideoCommand> getVideoCommand() {
        return Optional.ofNullable(command);
    }

    public Optional<Throwable> getThrowable() {
        return Optional.ofNullable(throwable);
    }
}
