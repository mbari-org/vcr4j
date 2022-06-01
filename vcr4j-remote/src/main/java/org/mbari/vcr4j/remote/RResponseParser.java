package org.mbari.vcr4j.remote;

import io.reactivex.rxjava3.subjects.Subject;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.VideoCommands;

import org.mbari.vcr4j.remote.commands.ConnectCmd;
import org.mbari.vcr4j.remote.commands.RRequest;
import org.mbari.vcr4j.remote.commands.PlayCmd;
import org.mbari.vcr4j.remote.commands.RResponse;
import org.mbari.vcr4j.remote.commands.OpenCmd;
import org.mbari.vcr4j.remote.model.VideoInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import static org.mbari.vcr4j.remote.commands.RRequest.Command.*

public class RResponseParser {

    private final UUID uuid;
    private final Subject<RState> stateSubject;
    private final Subject<RError> errorSubject;
    private final Subject<VideoIndex> indexSubject;
    private final Subject<VideoInformation> videoInfoSubject;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public RResponseParser(UUID uuid,
                           Subject<RState> stateSubject,
                           Subject<RError> errorSubject,
                           Subject<VideoIndex> indexSubject,
                           Subject<VideoInformation> videoInfoSubject) {

        this.uuid = uuid;
        this.stateSubject = stateSubject;
        this.errorSubject = errorSubject;
        this.indexSubject = indexSubject;
        this.videoInfoSubject = videoInfoSubject;
    }

    public void parse(VideoCommand<?> command, byte[] response) {
        String msg = new String(response, StandardCharsets.UTF_8);
        log.debug("Parsing " + command + " <<< " + msg);
        // --- route to correct subject
        try {
            if (command instanceof ConnectCmd c) handleGenericAckResponse(c, msg);
            // TODO videoInformation
            else if (command instanceof OpenCmd || command instanceof PlayCmd) handleGenericOkResponse(command, msg);
            else if (command instanceof RRequest c) {
                switch (c.getCommand()) {
                    case CLOSE.command() -> handleGenericAckResponse(c, msg);
                    case SHOW.command(), PAUSE.command() -> handleGenericOkResponse(c, msg);
                    case REQUEST_STATUS.command() ->
                }
            }

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

    private void handleResponse(VideoCommand<?> command, Runnable fn) {
        try {
            fn.run();
        }
        catch (Exception ex) {
            var e = new RError(false, true, false, command);
            errorSubject.onNext(e);
        }
    }

    private void handleGenericOkResponse(VideoCommand<?> command, String msg) {
        Runnable fn = () -> {
            var r = RVideoIO.GSON.fromJson(msg, RResponse.class);
            if (!r.getStatus().equalsIgnoreCase("ok")) {
                var e = new RError(false, false, true, command,
                        command.getName() + ": status = " + r.getResponse());
                errorSubject.onNext(e);
            }
        };
        handleResponse(command, fn);
    }

    private void handleGenericAckResponse(VideoCommand<?> command, String msg) {
        handleResponse(command, () -> RVideoIO.GSON.fromJson(msg, RResponse.class));
    }

    private void handleRequestStatus(VideoCommand<?> command, String msg) {

    }



}
