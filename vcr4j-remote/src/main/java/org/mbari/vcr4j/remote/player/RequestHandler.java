package org.mbari.vcr4j.remote.player;

import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.commands.*;
import org.mbari.vcr4j.remote.control.commands.loc.*;

import java.util.function.Function;

/**
 * Base interface for anything that handles incoming commands
 * @author Brian Schlining
 * @since 2022-08-08
 */
public interface RequestHandler {

    // Video control methods

    default <A extends RRequest, B extends RResponse> B handle(SimpleRequest simpleRequest,
                                           Class<A> clazz,
                                           Function<A, B> fn) {
        var request = RVideoIO.GSON.fromJson(simpleRequest.getRaw(), clazz);
        return fn.apply(request);
    }

    default RResponse composeResponse(SimpleRequest simpleRequest) {
        return switch (simpleRequest.getCommand()) {
            case CloseCmd.COMMAND -> handle(simpleRequest, CloseCmd.Request.class, this::handleClose);
            case ConnectCmd.COMMAND -> handle(simpleRequest, ConnectCmd.Request.class, this::handleConnect);
            case FrameAdvanceCmd.COMMAND -> handle(simpleRequest, FrameAdvanceCmd.Request.class, this::handleFrameAdvance);
            case OpenCmd.COMMAND -> handle(simpleRequest, OpenCmd.Request.class, this::handleOpen);
            case OpenDoneCmd.COMMAND -> handle(simpleRequest, OpenDoneCmd.Request.class, this::handleOpenDone);
            case PauseCmd.COMMAND -> handle(simpleRequest, PauseCmd.Request.class, this::handlePause);
            case PlayCmd.COMMAND -> handle(simpleRequest, PlayCmd.Request.class, this::handlePlay);
            case PingCmd.COMMAND -> handle(simpleRequest, PingCmd.Request.class, this::handlePing);
            case RSeekElapsedTimeCmd.COMMAND -> handle(simpleRequest, RSeekElapsedTimeCmd.Request.class, this::handleSeek);
            case RequestAllVideoInfosCmd.COMMAND -> handle(simpleRequest, RequestAllVideoInfosCmd.Request.class, this::handleRequestAllVideoInfos);
            case RequestElapsedTimeCmd.COMMAND -> handle(simpleRequest, RequestElapsedTimeCmd.Request.class, this::handleElapsedTime);
            case RequestPlayerStateCmd.COMMAND -> handle(simpleRequest, RequestPlayerStateCmd.Request.class, this::handleStatus);
            case RequestVideoInfoCmd.COMMAND -> handle(simpleRequest, RequestVideoInfoCmd.Request.class, this::handleRequestVideoInfo);
            case ShowCmd.COMMAND -> handle(simpleRequest, ShowCmd.Request.class, this::handleShow);
            case FrameCaptureCmd.COMMAND ->  handle(simpleRequest, FrameCaptureCmd.Request.class, this::handleFrameCaptureRequest);
            case FrameCaptureDoneCmd.COMMAND -> handle(simpleRequest, FrameCaptureDoneCmd.Request.class, this::handleFrameCaptureDoneRequest);
            case AddLocalizationsCmd.COMMAND -> handle(simpleRequest, AddLocalizationsCmd.Request.class, this::handleAddLocalizationsRequest);
            case ClearLocalizationsCmd.COMMAND -> handle(simpleRequest, ClearLocalizationsCmd.Request.class, this::handleClearLocalizationsRequest);
            case RemoveLocalizationsCmd.COMMAND -> handle(simpleRequest, RemoveLocalizationsCmd.Request.class, this::handleRemoveLocalizationsRequest);
            case UpdateLocalizationsCmd.COMMAND -> handle(simpleRequest, UpdateLocalizationsCmd.Request.class, this::handleUpdateLocalizationsRequest);
            case SelectLocalizationsCmd.COMMAND -> handle(simpleRequest, SelectLocalizationsCmd.Request.class, this::handleSelectLocalizationsRequest);
            default -> handleError(simpleRequest);
        };
    }

    OpenCmd.Response handleOpen(OpenCmd.Request request);

    OpenDoneCmd.Response handleOpenDone(OpenDoneCmd.Request request);

    CloseCmd.Response handleClose(CloseCmd.Request request);

    ShowCmd.Response handleShow(ShowCmd.Request request);

    RequestVideoInfoCmd.Response  handleRequestVideoInfo(RequestVideoInfoCmd.Request request);

    RequestAllVideoInfosCmd.Response handleRequestAllVideoInfos(RequestAllVideoInfosCmd.Request request);

    PingCmd.Response handlePing(PingCmd.Request request);
    PlayCmd.Response handlePlay(PlayCmd.Request request);

    PauseCmd.Response handlePause(PauseCmd.Request request);

    RequestElapsedTimeCmd.Response handleElapsedTime(RequestElapsedTimeCmd.Request request);

    RequestPlayerStateCmd.Response handleStatus(RequestPlayerStateCmd.Request request);

    RSeekElapsedTimeCmd.Response handleSeek(RSeekElapsedTimeCmd.Request request);

    FrameAdvanceCmd.Response handleFrameAdvance(FrameAdvanceCmd.Request request);

    ConnectCmd.Response handleConnect(ConnectCmd.Request request);



    // --- framecapture methods

    /**
     * This method is meant to be impleneted by the controller app, not the video
     * player app.
     * @param request
     */
    FrameCaptureDoneCmd.Response handleFrameCaptureDoneRequest(FrameCaptureDoneCmd.Request request);

    FrameCaptureCmd.Response handleFrameCaptureRequest(FrameCaptureCmd.Request request);

    // --- Localization methods

    AddLocalizationsCmd.Response handleAddLocalizationsRequest(AddLocalizationsCmd.Request request);

    RemoveLocalizationsCmd.Response handleRemoveLocalizationsRequest(RemoveLocalizationsCmd.Request request);

    UpdateLocalizationsCmd.Response handleUpdateLocalizationsRequest(UpdateLocalizationsCmd.Request request);

    ClearLocalizationsCmd.Response handleClearLocalizationsRequest(ClearLocalizationsCmd.Request request);

    SelectLocalizationsCmd.Response handleSelectLocalizationsRequest(SelectLocalizationsCmd.Request request);

    default NoopCmd.Response handleError(SimpleRequest request, Exception e) {
        return new NoopCmd.Response(request.getCommand(), "failed", e.getClass() + ": " + e.getMessage());
    }

    default NoopCmd.Response handleError(SimpleRequest request) {
        return new NoopCmd.Response(request.getCommand(), "failed");
    }
}
