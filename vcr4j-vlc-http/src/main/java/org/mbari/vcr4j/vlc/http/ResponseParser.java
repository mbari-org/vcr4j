package org.mbari.vcr4j.vlc.http;

import io.reactivex.subjects.Subject;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIndex;


public class ResponseParser {

    private final Subject<VlcState> stateSubject;
    private final Subject<VlcError> errorSubject;
    private final Subject<VideoIndex> indexSubject;

    public ResponseParser(Subject<VlcState> stateSubject, Subject<VlcError> errorSubject, Subject<VideoIndex> indexSubject) {
        this.stateSubject = stateSubject;
        this.errorSubject = errorSubject;
        this.indexSubject = indexSubject;
    }

    public void parseAndHandle(VideoCommand command, String responseBody) {
        try {
            // TODO use jackson or GSOn to parse json response
        }
        catch (Exception e) {

        }
    }
}
