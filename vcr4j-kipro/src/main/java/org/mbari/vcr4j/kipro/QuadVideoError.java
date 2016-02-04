package org.mbari.vcr4j.kipro;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoError;

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2016-02-04T11:17:00
 */
public class QuadVideoError implements VideoError {

    @Override
    public Optional<VideoCommand> getVideoCommand() {
        return null;
    }

    @Override
    public boolean hasError() {
        return false;
    }
}
