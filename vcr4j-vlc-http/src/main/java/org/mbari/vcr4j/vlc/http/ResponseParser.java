package org.mbari.vcr4j.vlc.http;

import io.reactivex.subjects.Subject;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.vlc.http.commands.OpenCmd;
import org.mbari.vcr4j.vlc.http.commands.VlcHttpCommands;
import org.mbari.vcr4j.vlc.http.model.PlayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ResponseParser {

    private final Subject<VlcState> stateSubject;
    private final Subject<VlcError> errorSubject;
    private final Subject<VideoIndex> indexSubject;
    private final Subject<PlayList> playListSubject;

    public ResponseParser(Subject<VlcState> stateSubject,
                          Subject<VlcError> errorSubject,
                          Subject<VideoIndex> indexSubject,
                          Subject<PlayList> playListSubject) {
        this.stateSubject = stateSubject;
        this.errorSubject = errorSubject;
        this.indexSubject = indexSubject;
        this.playListSubject = playListSubject;
    }

    public void parseAndHandle(VideoCommand command, String responseBody) {

        try {
            if (command.equals(VlcHttpCommands.REQUEST_PLAYLIST)) {
                PlayList playList = Constants.GSON.fromJson(responseBody, PlayList.class);
                playListSubject.onNext(playList);
            }
            else if (command instanceof OpenCmd) {

            }

            VlcError noError = new VlcError(false, command);
            errorSubject.onNext(noError);
        }
        catch (Exception e) {
            VlcError error = new VlcError(e, command);
            errorSubject.onNext(error);
        }
    }
}
