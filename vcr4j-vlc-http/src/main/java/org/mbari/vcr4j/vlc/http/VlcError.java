package org.mbari.vcr4j.vlc.http;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoError;

import java.util.Optional;

public class VlcError implements VideoError {

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public Optional<VideoCommand> getVideoCommand() {
        return Optional.empty();
    }
}
