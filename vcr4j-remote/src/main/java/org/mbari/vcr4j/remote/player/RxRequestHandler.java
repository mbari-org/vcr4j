/*
 * Copyright © 2008 MBARI (brian@mbari.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mbari.vcr4j.remote.player;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.mbari.vcr4j.remote.control.RState;
import org.mbari.vcr4j.remote.control.commands.*;
import org.mbari.vcr4j.remote.control.commands.localization.*;

import java.io.Closeable;
import java.time.Duration;


/**
 * This takes all the localization cmd/requests and dumps them into an
 * observable so the implementation can do whatever it needs to do to manage the
 * localizations. Localization requests are answered with OK, unless {@link VideoController#hasVideo(java.util.UUID)}
 * is false for the target video, in which case they fail and are not emitted.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public abstract class RxRequestHandler implements RequestHandler, Closeable {

    /** Cause reported when a localization command targets an unknown video. */
    public static final String NO_VIDEO_FOR_UUID = VideoResult.NO_VIDEO_FOR_UUID;

    private final Subject<LocalizationsCmd<?, ?>> localizationsCmdSubject;
    private final VideoController videoController;


    public RxRequestHandler(VideoController videoController) {
        PublishSubject<LocalizationsCmd<?, ?>> pubSub = PublishSubject.create();
        this.localizationsCmdSubject = pubSub.toSerialized();
        this.videoController = videoController;
    }

    @Override
    public void close() {
        localizationsCmdSubject.onComplete();
    }

    public VideoController getVideoController() {
        return videoController;
    }

    @Override
    public OpenCmd.Response handleOpen(OpenCmd.Request request) {
        if (request.getUrl() == null) {
            throw new IllegalArgumentException("A url is required to open a video");
        }
        var result = videoController.openVideo(request.getUuid(), request.getUrl());
        return result.ok() ? new OpenCmd.Response(RResponse.OK)
                : new OpenCmd.Response(RResponse.FAILED, result.cause());
    }

    @Override
    public OpenDoneCmd.Response handleOpenDone(OpenDoneCmd.Request request) {
        return new OpenDoneCmd.Response();
    }

    @Override
    public CloseCmd.Response handleClose(CloseCmd.Request request) {
        // Close is idempotent: if the video is already closed that's the state the caller wanted
        videoController.close(request.getUuid());
        return new CloseCmd.Response(RResponse.OK);
    }

    @Override
    public ShowCmd.Response handleShow(ShowCmd.Request request) {
        var result = videoController.showVideo(request.getUuid());
        return result.ok() ? new ShowCmd.Response(RResponse.OK)
                : new ShowCmd.Response(RResponse.FAILED, result.cause());
    }

    @Override
    public RequestVideoInfoCmd.Response handleRequestVideoInfo(RequestVideoInfoCmd.Request request) {
        var videoInfo = videoController.requestVideoInfo();
        return videoInfo
                .map(vi -> new RequestVideoInfoCmd.Response(vi.getUuid(), vi.getUrl(),
                        vi.getDurationMillis(), vi.getFrameRate(), vi.isKey()))
                .orElse(new RequestVideoInfoCmd.Response());
    }

    @Override
    public RequestAllVideoInfosCmd.Response handleRequestAllVideoInfos(RequestAllVideoInfosCmd.Request request) {
        var videoInfos = videoController.requestAllVideoInfos();
        return new RequestAllVideoInfosCmd.Response(videoInfos);
    }

    @Override
    public PingCmd.Response handlePing(PingCmd.Request request) {
        return new PingCmd.Response();
    }

    @Override
    public PlayCmd.Response handlePlay(PlayCmd.Request request) {
        var rate = request.getRate() == null ? 1.0 : request.getRate();
        var result = videoController.playVideo(request.getUuid(), rate);
        return result.ok() ? new PlayCmd.Response(RResponse.OK)
                : new PlayCmd.Response(RResponse.FAILED, result.cause());
    }

    @Override
    public PauseCmd.Response handlePause(PauseCmd.Request request) {
        var result = videoController.pauseVideo(request.getUuid());
        return result.ok() ? new PauseCmd.Response(RResponse.OK)
                : new PauseCmd.Response(RResponse.FAILED, result.cause());
    }

    @Override
    public RequestElapsedTimeCmd.Response handleElapsedTime(RequestElapsedTimeCmd.Request request) {
        var opt = videoController.requestElapsedTime(request.getUuid());
        return opt
                .map(d -> new RequestElapsedTimeCmd.Response(d.toMillis()))
                .orElse(new RequestElapsedTimeCmd.Response());
    }

    @Override
    public RequestPlayerStateCmd.Response handleStatus(RequestPlayerStateCmd.Request request) {
        var opt = videoController.requestRate(request.getUuid());
        var elapsedTimeMillis = videoController.requestElapsedTime(request.getUuid())
                .map(Duration::toMillis)
                .orElse(null);
        return opt
                .map(r -> new RequestPlayerStateCmd.Response(RState.fromRate(r).getName(), r, elapsedTimeMillis))
                .orElse(RequestPlayerStateCmd.Response.failed(NO_VIDEO_FOR_UUID));
    }

    @Override
    public RSeekElapsedTimeCmd.Response handleSeek(RSeekElapsedTimeCmd.Request request) {
        if (request.getElapsedTimeMillis() == null) {
            throw new IllegalArgumentException("elapsedTimeMillis is required to seek");
        }
        var result = videoController.seekVideo(request.getUuid(), Duration.ofMillis(request.getElapsedTimeMillis()));
        return result.ok() ? new RSeekElapsedTimeCmd.Response(RResponse.OK)
                : new RSeekElapsedTimeCmd.Response(RResponse.FAILED, result.cause());
    }

    @Override
    public FrameAdvanceCmd.Response handleFrameAdvance(FrameAdvanceCmd.Request request) {
        // direction: positive (1) is forward, negative (-1) is back. Absent is treated as forward.
        var direction = request.getDirection();
        var result = videoController.advanceFrame(request.getUuid(), direction == null || direction >= 0);
        return result.ok() ? new FrameAdvanceCmd.Response(RResponse.OK)
                : new FrameAdvanceCmd.Response(RResponse.FAILED, result.cause());
    }

    /**
     * @return True if localization commands for this video are accepted. Subclasses can widen this
     *  to videos that are about to exist.
     */
    protected boolean isVideoKnown(java.util.UUID videoUuid) {
        return videoController.hasVideo(videoUuid);
    }

    /** Hands a localization command to subscribers. Subclasses can defer it, but must keep order. */
    protected void dispatch(java.util.UUID videoUuid, LocalizationsCmd<?, ?> cmd) {
        localizationsCmdSubject.onNext(cmd);
    }

    public Observable<LocalizationsCmd<?, ?>> getLocalizationsCmdObservable() {
        return localizationsCmdSubject;
    }

    @Override
    public AddLocalizationsCmd.Response handleAddLocalizationsRequest(AddLocalizationsCmd.Request request) {
        if (request.getLocalizations() == null) {
            throw new IllegalArgumentException("localizations is required");
        }
        if (!isVideoKnown(request.getUuid())) {
            return new AddLocalizationsCmd.Response(RResponse.FAILED, NO_VIDEO_FOR_UUID);
        }
        dispatch(request.getUuid(), new AddLocalizationsCmd(request));
        return new AddLocalizationsCmd.Response(RResponse.OK);
    }

    @Override
    public RemoveLocalizationsCmd.Response handleRemoveLocalizationsRequest(RemoveLocalizationsCmd.Request request) {
        if (request.getLocalizations() == null) {
            throw new IllegalArgumentException("localizations is required");
        }
        if (!isVideoKnown(request.getUuid())) {
            return new RemoveLocalizationsCmd.Response(RResponse.FAILED, NO_VIDEO_FOR_UUID);
        }
        dispatch(request.getUuid(), new RemoveLocalizationsCmd(request));
        return new RemoveLocalizationsCmd.Response(RResponse.OK);
    }

    @Override
    public UpdateLocalizationsCmd.Response handleUpdateLocalizationsRequest(UpdateLocalizationsCmd.Request request) {
        if (request.getLocalizations() == null) {
            throw new IllegalArgumentException("localizations is required");
        }
        if (!isVideoKnown(request.getUuid())) {
            return new UpdateLocalizationsCmd.Response(RResponse.FAILED, NO_VIDEO_FOR_UUID);
        }
        dispatch(request.getUuid(), new UpdateLocalizationsCmd(request));
        return new UpdateLocalizationsCmd.Response(RResponse.OK);
    }

    @Override
    public ClearLocalizationsCmd.Response handleClearLocalizationsRequest(ClearLocalizationsCmd.Request request) {
        if (!isVideoKnown(request.getUuid())) {
            return new ClearLocalizationsCmd.Response(RResponse.FAILED, NO_VIDEO_FOR_UUID);
        }
        dispatch(request.getUuid(), new ClearLocalizationsCmd(request));
        return new ClearLocalizationsCmd.Response(RResponse.OK);
    }

    @Override
    public SelectLocalizationsCmd.Response handleSelectLocalizationsRequest(SelectLocalizationsCmd.Request request) {
        if (request.getLocalizations() == null) {
            throw new IllegalArgumentException("localizations is required");
        }
        if (!isVideoKnown(request.getUuid())) {
            return new SelectLocalizationsCmd.Response(RResponse.FAILED, NO_VIDEO_FOR_UUID);
        }
        dispatch(request.getUuid(), new SelectLocalizationsCmd(request));
        return new SelectLocalizationsCmd.Response(RResponse.OK);
    }
}
