package org.mbari.vcr4j.sharktopoda;

import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.VideoState;
import rx.subjects.Subject;

/**
 * @author Brian Schlining
 * @since 2016-08-25T16:45:00
 */
public class SharktopodaResponseParser {

    private final Subject<SharktopodaState, SharktopodaState> stateSubject;
    private final Subject<SharktopodaError, SharktopodaError> errorSubject;
    private final Subject<VideoIndex, VideoIndex> indexSubject;


    public SharktopodaResponseParser(Subject<SharktopodaState, SharktopodaState> stateSubject,
            Subject<SharktopodaError, SharktopodaError> errorSubject,
            Subject<VideoIndex, VideoIndex> indexSubject) {

        this.stateSubject = stateSubject;
        this.errorSubject = errorSubject;
        this.indexSubject = indexSubject;

    }

    public void parse(byte[] response) {
        String msg = new String(response);

        // --- Parse JSON

        // --- route to correct subject
    }
}
