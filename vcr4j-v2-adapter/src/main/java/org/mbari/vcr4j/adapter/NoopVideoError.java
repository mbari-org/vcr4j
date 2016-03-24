package org.mbari.vcr4j.adapter;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoError;

import java.util.Optional;

/**
 * No-op implementation. hasError is always false.
 *
 * @author Brian Schlining
 * @since 2016-03-24T13:30:00
 */
public class NoopVideoError implements VideoError {


    @Override
    public Optional<VideoCommand> getVideoCommand() {
        return Optional.empty();
    }

    @Override
    public boolean hasError() {
        return false;
    }
}

