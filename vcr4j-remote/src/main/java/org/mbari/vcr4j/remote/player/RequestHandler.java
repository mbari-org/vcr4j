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

import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.commands.*;
import org.mbari.vcr4j.remote.control.commands.localization.*;

import com.google.gson.JsonParseException;

import java.net.MalformedURLException;
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
        if (request == null || (request.getUuid() == null && requiresUuid(simpleRequest.getCommand()))) {
            throw new IllegalArgumentException("A video uuid is required for the '" +
                    simpleRequest.getCommand() + "' command");
        }
        return fn.apply(request);
    }

    private static boolean requiresUuid(String command) {
        return switch (command) {
            case ConnectCmd.COMMAND, PingCmd.COMMAND, RequestVideoInfoCmd.COMMAND,
                    RequestAllVideoInfosCmd.COMMAND -> false;
            default -> true;
        };
    }

    /**
     * An open request is validated synchronously: a url that can't be parsed fails with
     * "Malformed URL" rather than "Invalid message".
     */
    private OpenCmd.Response handleOpenMessage(SimpleRequest simpleRequest) {
        try {
            return handle(simpleRequest, OpenCmd.Request.class, this::handleOpen);
        }
        catch (JsonParseException e) {
            for (Throwable t = e; t != null; t = t.getCause()) {
                if (t instanceof MalformedURLException) {
                    return new OpenCmd.Response(RResponse.FAILED, VideoResult.MALFORMED_URL);
                }
            }
            throw e;
        }
    }

    default RResponse composeResponse(SimpleRequest simpleRequest) {
        if (simpleRequest.getCommand() == null) {
            return handleError(simpleRequest, new IllegalArgumentException("No command"));
        }
        return switch (simpleRequest.getCommand()) {
            case CloseCmd.COMMAND -> handle(simpleRequest, CloseCmd.Request.class, this::handleClose);
            case ConnectCmd.COMMAND -> handle(simpleRequest, ConnectCmd.Request.class, this::handleConnect);
            case FrameAdvanceCmd.COMMAND -> handle(simpleRequest, FrameAdvanceCmd.Request.class, this::handleFrameAdvance);
            case OpenCmd.COMMAND -> handleOpenMessage(simpleRequest);
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
            default -> handleUnknownCommand(simpleRequest);
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

    /**
     * Used when a message can't be parsed, or its structure is wrong (e.g. missing or mistyped
     * fields). As per the protocol, the cause is always "Invalid message".
     * @param request The request
     * @param e The reason, which is not reported to the remote app
     * @return A failed response
     */
    default NoopCmd.Response handleError(SimpleRequest request, Exception e) {
        if (e instanceof JsonParseException
                || e instanceof NullPointerException
                || e instanceof IllegalArgumentException) {
            return invalidMessage(request.getCommand());
        }
        return new NoopCmd.Response(request.getCommand(), RResponse.FAILED, e.getClass() + ": " + e.getMessage());
    }

    default NoopCmd.Response handleError(SimpleRequest request) {
        return handleUnknownCommand(request);
    }

    default NoopCmd.Response handleUnknownCommand(SimpleRequest request) {
        if (request.getCommand() == null) {
            return invalidMessage(null);
        }
        return new NoopCmd.Response("unknown", RResponse.FAILED, "unknown command: " + request.getCommand());
    }

    /**
     * @param command The command of the invalid request. Null if it could not be determined
     * @return The protocol's response to an invalid message
     */
    static NoopCmd.Response invalidMessage(String command) {
        return new NoopCmd.Response(command == null ? "unknown" : command,
                RResponse.FAILED, VideoResult.INVALID_MESSAGE);
    }
}
