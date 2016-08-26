package org.mbari.vcr4j.sharktopoda;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd;
import org.mbari.vcr4j.sharktopoda.model.response.FramecaptureResponse;
import org.mbari.vcr4j.sharktopoda.model.response.IVideoInfo;
import org.mbari.vcr4j.sharktopoda.model.response.OpenResponse;
import org.mbari.vcr4j.sharktopoda.model.response.RequestStatusResponse;
import rx.subjects.Subject;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-25T16:45:00
 *
 */
public class SharktopodaResponseParser {

    private final UUID uuid;
    private final Subject<SharktopodaState, SharktopodaState> stateSubject;
    private final Subject<SharktopodaError, SharktopodaError> errorSubject;
    private final Subject<VideoIndex, VideoIndex> indexSubject;
    private final Subject<IVideoInfo, IVideoInfo> videoInfoSubject;
    private final Subject<FramecaptureResponse, FramecaptureResponse> framecaptureSubject;


    public SharktopodaResponseParser(UUID uuid,
            Subject<SharktopodaState, SharktopodaState> stateSubject,
            Subject<SharktopodaError, SharktopodaError> errorSubject,
            Subject<VideoIndex, VideoIndex> indexSubject,
            Subject<IVideoInfo, IVideoInfo> videoInfoSubject,
            Subject<FramecaptureResponse, FramecaptureResponse> framecaptureSubject) {

        this.uuid = uuid;
        this.stateSubject = stateSubject;
        this.errorSubject = errorSubject;
        this.indexSubject = indexSubject;
        this.videoInfoSubject = videoInfoSubject;
        this.framecaptureSubject = framecaptureSubject;
    }

    public void parse(VideoCommand command, byte[] response) {
        String msg = new String(response);

        // --- route to correct subject
        if (command instanceof OpenCmd)  handleOpen(msg);
        else if (command.equals(VideoCommands.REQUEST_STATUS)) handleRequestStatus(msg);

    }

    private void handleOpen(String msg) {
        OpenResponse r = Constants.GSON.fromJson(msg, OpenResponse.class);
        if (r.getStatus().equalsIgnoreCase("ok")) {
            SharktopodaState state = new SharktopodaState(SharktopodaState.State.PAUSED);
            stateSubject.onNext(state);
        }
        else {
            SharktopodaState state = new SharktopodaState(SharktopodaState.State.NOT_FOUND);
            stateSubject.onNext(state);
        }
    }

    private void handleRequestStatus(String msg) {
        RequestStatusResponse r = Constants.GSON.fromJson(msg, RequestStatusResponse.class);
        if (r.getUuid().equals(uuid)) {
            SharktopodaState state = SharktopodaState.parse(r.getStatus());
            stateSubject.onNext(state);
        }
    }
}
