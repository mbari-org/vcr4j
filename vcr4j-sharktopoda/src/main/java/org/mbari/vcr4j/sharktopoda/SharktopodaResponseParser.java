package org.mbari.vcr4j.sharktopoda;

import io.reactivex.subjects.Subject;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd;
import org.mbari.vcr4j.sharktopoda.commands.SharkCommands;
import org.mbari.vcr4j.sharktopoda.model.VideoInformation;
import org.mbari.vcr4j.sharktopoda.model.response.OpenResponse;
import org.mbari.vcr4j.sharktopoda.model.response.PlayResponse;
import org.mbari.vcr4j.sharktopoda.model.response.RequestAllVideoInfosReponse;
import org.mbari.vcr4j.sharktopoda.model.response.RequestElapsedTimeResponse;
import org.mbari.vcr4j.sharktopoda.model.response.RequestStatusResponse;
import org.mbari.vcr4j.sharktopoda.model.response.RequestVideoInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2016-08-25T16:45:00
 *
 */
public class SharktopodaResponseParser {

    private final UUID uuid;
    private final Subject<SharktopodaState> stateSubject;
    private final Subject<SharktopodaError> errorSubject;
    private final Subject<VideoIndex> indexSubject;
    private final Subject<VideoInformation> videoInfoSubject;
    private final Logger log = LoggerFactory.getLogger(getClass());


    public SharktopodaResponseParser(UUID uuid,
            Subject<SharktopodaState> stateSubject,
            Subject<SharktopodaError> errorSubject,
            Subject<VideoIndex> indexSubject,
            Subject<VideoInformation> videoInfoSubject) {

        this.uuid = uuid;
        this.stateSubject = stateSubject;
        this.errorSubject = errorSubject;
        this.indexSubject = indexSubject;
        this.videoInfoSubject = videoInfoSubject;
    }

    public void parse(VideoCommand command, byte[] response) {
        String msg = new String(response);
        log.debug("Parsing " + command + " <<< " + msg);
        // --- route to correct subject
        try {
            if (command instanceof OpenCmd) handleOpen(msg);
            else if (command.equals(VideoCommands.PLAY)) handlePlay(msg);
            else if (command.equals(VideoCommands.PAUSE) || command.equals(VideoCommands.STOP)) handlePause(msg);
            else if (command.equals(VideoCommands.REQUEST_STATUS)) handleRequestStatus(msg);
            else if (command.equals(VideoCommands.REQUEST_INDEX)) handleRequestIndex(msg);
            else if (command.equals(VideoCommands.REQUEST_ELAPSED_TIME)) handleRequestIndex(msg);
            else if (command.equals(SharkCommands.REQUEST_VIDEO_INFO)) handleRequestVideoInfo(msg);
            else if (command.equals(SharkCommands.REQUEST_ALL_VIDEO_INFOS)) handleRequestAllVideoInfos(msg);
        }
        catch (Exception e) {
            SharktopodaError error = new SharktopodaError(false, true, false, Optional.of(command));
            errorSubject.onNext(error);
        }

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

    private void handlePlay(String msg) {
        PlayResponse r = Constants.GSON.fromJson(msg, PlayResponse.class);
        if (r.getStatus().equalsIgnoreCase("ok")) {
            SharktopodaState state = new SharktopodaState(SharktopodaState.State.PLAYING);
            stateSubject.onNext(state);
        }
        else {
            SharktopodaState state = new SharktopodaState(SharktopodaState.State.UNKNOWN_ERROR);
            stateSubject.onNext(state);
            SharktopodaError error = new SharktopodaError(false, true, true, Optional.of(VideoCommands.PLAY));
            errorSubject.onNext(error);
        }
    }

    private void handlePause(String msg) {
        PlayResponse r = Constants.GSON.fromJson(msg, PlayResponse.class);
        if (r.getStatus().equalsIgnoreCase("ok")) {
            SharktopodaState state = new SharktopodaState(SharktopodaState.State.PAUSED);
            stateSubject.onNext(state);
        }
        else {
            SharktopodaState state = new SharktopodaState(SharktopodaState.State.UNKNOWN_ERROR);
            stateSubject.onNext(state);
        }
    }

    private void handleRequestIndex(String msg) {
        RequestElapsedTimeResponse r = Constants.GSON.fromJson(msg, RequestElapsedTimeResponse.class);
        if (r.getUuid().equals(uuid)) {
            VideoIndex videoIndex = new VideoIndex(Duration.ofMillis(r.getElapsedTimeMillis()));
            indexSubject.onNext(videoIndex);
        }
    }

    private void handleRequestVideoInfo(String msg) {
        RequestVideoInfoResponse r = Constants.GSON.fromJson(msg, RequestVideoInfoResponse.class);
        if (r.getUuid().equals(uuid)) {
            List<VideoInformation.Video> videos = Collections.singletonList(new VideoInformation.Video(r.getUuid(), r.getUrl()));
            VideoInformation info = new VideoInformation(VideoInformation.RequestType.FOCUSED, videos);
            videoInfoSubject.onNext(info);
        }
    }

    private void handleRequestAllVideoInfos(String msg) {
        RequestAllVideoInfosReponse r = Constants.GSON.fromJson(msg, RequestAllVideoInfosReponse.class);
        List<VideoInformation.Video> videos = r.getVideos()
                .stream()
                .map(v -> new VideoInformation.Video(v.getUuid(), v.getUrl()))
                .collect(Collectors.toList());
        VideoInformation info = new VideoInformation(VideoInformation.RequestType.ALL, videos);
        videoInfoSubject.onNext(info);
    }

}
