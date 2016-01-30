package org.mbari.vcr4j;

import java.util.Optional;

public interface VideoError {

    /**
     * @return true if an error condition occurred in response to a command
     */
    boolean hasError();

    /**
     * @return The command that triggered the error
     */
    Optional<VideoCommand> getVideoCommand();
}
