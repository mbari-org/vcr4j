package org.mbari.vcr4j.decorators;

import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.VideoCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs the errors, indices, state, and commands for a VideoIO
 * @author Brian Schlining
 * @since 2016-01-29T11:13:00
 */
public class LoggingDecorator<S extends VideoState, E extends VideoError> {

    private final Logger log = LoggerFactory.getLogger(getClass());


    public LoggingDecorator(VideoIO<S, E> io) {

        io.getErrorObservable()
                .subscribe(this::logError,
                        ex -> log.debug("An error occurred in the error observable", ex),
                        () -> log.debug("Error observable is closed"));

        io.getStateObservable()
                .subscribe(this::logState,
                        ex -> log.debug("An error occurred in the state observable", ex),
                        () -> log.debug("State observable is closed"));

        io.getIndexObservable()
                .subscribe(this::logIndex,
                        ex -> log.debug("An error occurred in the index observable", ex),
                        () -> log.debug("Index observable is closed"));


        io.getCommandSubject()
                .subscribe(this::logCommand,
                        ex -> log.debug("An error occurred in the command subject", ex),
                        () -> log.debug("Command subject is closed"));
    }


    public void logState(VideoState state) {
        if (log.isDebugEnabled()) {
            log.debug(new VideoStateAsString(state).toString());
        }
    }

    public void logIndex(VideoIndex index) {
       if (log.isDebugEnabled()) {
           log.debug(new VideoIndexAsString(index).toString());
       }
    }

    public void logError(VideoError error) {
        if (log.isDebugEnabled()) {
            log.debug(new VideoErrorAsString(error).toString());
        }
    }

    public void logCommand(VideoCommand vc) {
        if (log.isDebugEnabled()) {
            log.debug(new VideoCommandAsString(vc).toString());
        }
    }
}
