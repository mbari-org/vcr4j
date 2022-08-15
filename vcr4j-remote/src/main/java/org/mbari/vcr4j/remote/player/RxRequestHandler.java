package org.mbari.vcr4j.remote.player;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.mbari.vcr4j.remote.control.RState;
import org.mbari.vcr4j.remote.control.commands.*;
import org.mbari.vcr4j.remote.control.commands.loc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.time.Duration;


/**
 * This takes all the localization cmd/requests and dumps them into an
 * observable so the implementation can do whatever it needs to do to manage the
 * localizations. As implemented all localization requests response with an OK.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public abstract class RxRequestHandler implements RequestHandler, Closeable {

    private final Subject<LocalizationsCmd<?, ?>> localizationsCmdSubject;
    private final VideoController videoController;

    private final Logger log = LoggerFactory.getLogger(getClass());

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
        var ok = videoController.open(request.getUuid(), request.getUrl());
        var s = ok ? RResponse.OK : RResponse.FAILED;
        return new OpenCmd.Response(s);
    }

    @Override
    public CloseCmd.Response handleClose(CloseCmd.Request request) {
        var ok = videoController.close(request.getUuid());
        var s = ok ? RResponse.OK : RResponse.FAILED;
        return new CloseCmd.Response(s);
    }

    @Override
    public ShowCmd.Response handleShow(ShowCmd.Request request) {
        var ok = videoController.show(request.getUuid());
        var s = ok ? RResponse.OK : RResponse.FAILED;
        return new ShowCmd.Response(s);
    }

    @Override
    public RequestVideoInfoCmd.Response handleRequestVideoInfo(RequestVideoInfoCmd.Request request) {
        var videoInfo = videoController.requestVideoInfo();
        return videoInfo
                .map(vi -> new RequestVideoInfoCmd.Response(vi.getUuid(), vi.getUrl(), vi.getDurationMillis(), vi.getFrameRate()))
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
        var ok = videoController.play(request.getUuid(), rate);
        var s = ok ? RResponse.OK : RResponse.FAILED;
        return new PlayCmd.Response(s);
    }

    @Override
    public PauseCmd.Response handlePause(PauseCmd.Request request) {
        var ok = videoController.pause(request.getUuid());
        var s = ok ? RResponse.OK : RResponse.FAILED;
        return new PauseCmd.Response(s);
    }

    @Override
    public RequestElapsedTimeCmd.Response handleElapsedTime(RequestElapsedTimeCmd.Request request) {
        var opt = videoController.requestElapsedTime(request.getUuid());
        return opt
                .map(d -> new RequestElapsedTimeCmd.Response(d.toMillis()))
                .orElse(new RequestElapsedTimeCmd.Response());
    }

    @Override
    public RequestStatusCmd.Response handleStatus(RequestStatusCmd.Request request) {
        var opt = videoController.requestRate(request.getUuid());
        return opt
                .map(r -> new RequestStatusCmd.Response(RState.fromRate(r).getName()))
                .orElse(new RequestStatusCmd.Response(RState.State.NOT_FOUND.getName()));
    }

    @Override
    public RSeekElapsedTimeCmd.Response handleSeek(RSeekElapsedTimeCmd.Request request) {
        var ok = videoController.seekElapsedTime(request.getUuid(), Duration.ofMillis(request.getElapsedTimeMillis()));
        var s = ok ? RResponse.OK : RResponse.FAILED;
        return new RSeekElapsedTimeCmd.Response(s);
    }

    @Override
    public FrameAdvanceCmd.Response handleFrameAdvance(FrameAdvanceCmd.Request request) {
        var ok = videoController.frameAdvance(request.getUuid());
        var s = ok ? RResponse.OK : RResponse.FAILED;
        return new FrameAdvanceCmd.Response(s);
    }

    public Observable<LocalizationsCmd<?, ?>> getLocalizationsCmdObservable() {
        return localizationsCmdSubject;
    }

    @Override
    public AddLocalizationsCmd.Response handleAddLocalizationsRequest(AddLocalizationsCmd.Request request) {
        localizationsCmdSubject.onNext(new AddLocalizationsCmd(request));
        return new AddLocalizationsCmd.Response(RResponse.OK);
    }

    @Override
    public RemoveLocalizationsCmd.Response handleRemoveLocalizationsRequest(RemoveLocalizationsCmd.Request request) {
        localizationsCmdSubject.onNext(new RemoveLocalizationsCmd(request));
        return new RemoveLocalizationsCmd.Response(RResponse.OK);
    }

    @Override
    public UpdateLocalizationsCmd.Response handleUpdateLocalizationsRequest(UpdateLocalizationsCmd.Request request) {
        localizationsCmdSubject.onNext(new UpdateLocalizationsCmd(request));
        return new UpdateLocalizationsCmd.Response(RResponse.OK);
    }

    @Override
    public ClearLocalizationsCmd.Response handleClearLocalizationsRequest(ClearLocalizationsCmd.Request request) {
        localizationsCmdSubject.onNext(new ClearLocalizationsCmd(request));
        return new ClearLocalizationsCmd.Response(RResponse.OK);
    }
}
