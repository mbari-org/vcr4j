package org.mbari.vcr4j.remote;

import io.reactivex.rxjava3.subjects.Subject;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIndex;

import org.mbari.vcr4j.remote.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class RResponseParser {

    private final UUID uuid;
    private final Subject<RState> stateSubject;
    private final Subject<RError> errorSubject;
    private final Subject<VideoIndex> indexSubject;
//    private final Subject<VideoInformation> videoInfoSubject;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public RResponseParser(UUID uuid,
                           Subject<RState> stateSubject,
                           Subject<RError> errorSubject,
                           Subject<VideoIndex> indexSubject) {
//                           Subject<VideoInformation> videoInfoSubject) {

        this.uuid = uuid;
        this.stateSubject = stateSubject;
        this.errorSubject = errorSubject;
        this.indexSubject = indexSubject;
//        this.videoInfoSubject = videoInfoSubject;
    }


    private <B extends RResponse> Optional<B> parse(RCommand<?, B> command, String msg) {
        try {
            var response = RVideoIO.GSON.fromJson(msg, command.responseType());
            if (!response.success()) {
                var e = new RError(false, false, true, command,
                        "The command was unsuccessful", null);
                errorSubject.onNext(e);
            }
            return Optional.of(response);
        }
        catch (Exception ex) {
            var e = new RError(false, true, false, command);
            errorSubject.onNext(e);
            return Optional.empty();
        }
    }

    private <B extends RResponse> void handle(RCommand<?, B> command, String msg) {
        parse(command, msg).ifPresent(response -> {
            if (response instanceof RequestStatusCmd.Response r) {
                stateSubject.onNext(r.getState());
            }
            else if (response instanceof RequestElapsedTimeCmd.Response r) {
                r.getVideoIndex().ifPresent(indexSubject::onNext);
            }
        });
    }


}
