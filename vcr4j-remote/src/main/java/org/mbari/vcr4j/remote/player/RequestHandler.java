package org.mbari.vcr4j.remote.player;

import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.commands.*;
import org.mbari.vcr4j.remote.control.commands.loc.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.loc.ClearLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.loc.RemoveLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.loc.UpdateLocalizationsCmd;

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
            case CloseCmd.Command -> handle(simpleRequest, CloseCmd.Request.class, this::handleClose);
            case ConnectCmd.Command -> handle(simpleRequest, ConnectCmd.Request.class, this::handleConnect);
            case FrameAdvanceCmd.Command -> handle(simpleRequest, FrameAdvanceCmd.Request.class, this::handleFrameAdvance);
            case OpenCmd.Command -> handle(simpleRequest, OpenCmd.Request.class, this::handleOpen);
            case PauseCmd.Command -> handle(simpleRequest, PauseCmd.Request.class, this::handlePause);
            case PlayCmd.Command -> handle(simpleRequest, PlayCmd.Request.class, this::handlePlay);
            case RSeekElapsedTimeCmd.Command -> handle(simpleRequest, RSeekElapsedTimeCmd.Request.class, this::handleSeek);
            case RequestAllVideoInfosCmd.Command -> handle(simpleRequest, RequestAllVideoInfosCmd.Request.class, this::handleRequestAllVideoInfos);
            case RequestElapsedTimeCmd.Command -> handle(simpleRequest, RequestElapsedTimeCmd.Request.class, this::handleElapsedTime);
            case RequestStatusCmd.Command -> handle(simpleRequest, RequestStatusCmd.Request.class, this::handleStatus);
            case RequestVideoInfoCmd.Command -> handle(simpleRequest, RequestVideoInfoCmd.Request.class, this::handleRequestVideoInfo);
            case ShowCmd.Command -> handle(simpleRequest, ShowCmd.Request.class, this::handleShow);
            case FrameCaptureCmd.Command ->  handle(simpleRequest, FrameCaptureCmd.Request.class, this::handleFrameCaptureRequest);
            case FrameCaptureDoneCmd.Command -> handle(simpleRequest, FrameCaptureDoneCmd.Request.class, this::handleFrameCaptureDoneRequest);
            case AddLocalizationsCmd.Command -> handle(simpleRequest, AddLocalizationsCmd.Request.class, this::handleAddLocalizationsRequest);
            case ClearLocalizationsCmd.Command -> handle(simpleRequest, ClearLocalizationsCmd.Request.class, this::handleClearLocalizationsRequest);
            case RemoveLocalizationsCmd.Command -> handle(simpleRequest, RemoveLocalizationsCmd.Request.class, this::handleRemoveLocalizationsRequest);
            case UpdateLocalizationsCmd.Command -> handle(simpleRequest, UpdateLocalizationsCmd.Request.class, this::handleUpdateLocalizationsRequest);
            default -> handleError(simpleRequest);
        };
    }

    OpenCmd.Response handleOpen(OpenCmd.Request request);

    CloseCmd.Response handleClose(CloseCmd.Request request);

    ShowCmd.Response handleShow(ShowCmd.Request request);

    RequestVideoInfoCmd.Response  handleRequestVideoInfo(RequestVideoInfoCmd.Request request);

    RequestAllVideoInfosCmd.Response handleRequestAllVideoInfos(RequestAllVideoInfosCmd.Request request);

    PlayCmd.Response handlePlay(PlayCmd.Request request);

    PauseCmd.Response handlePause(PauseCmd.Request request);

    RequestElapsedTimeCmd.Response handleElapsedTime(RequestElapsedTimeCmd.Request request);

    RequestStatusCmd.Response handleStatus(RequestStatusCmd.Request request);

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

    default NoopCmd.Response handleError(SimpleRequest request, Exception e) {
        return new NoopCmd.Response(request.getCommand(), "failed", e.getClass() + ": " + e.getMessage());
    }

    default NoopCmd.Response handleError(SimpleRequest request) {
        return new NoopCmd.Response(request.getCommand(), "failed");
    }
}
