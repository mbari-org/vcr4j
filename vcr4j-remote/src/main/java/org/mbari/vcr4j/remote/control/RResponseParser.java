package org.mbari.vcr4j.remote.control;

import io.reactivex.rxjava3.subjects.Subject;
import org.mbari.vcr4j.VideoIndex;

import org.mbari.vcr4j.remote.control.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Parses an incoming response to it's correct type. If the response needs to be passed to an
 * observable, this class forwards it to the correct observable.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RResponseParser {

    private final UUID uuid;
    private final Subject<RState> stateSubject;
    private final Subject<RError> errorSubject;
    private final Subject<VideoIndex> indexSubject;
    private final Subject<List<VideoInfo>> videoInfoSubject;
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Constructor
     * @param uuid THe video uuid
     * @param stateSubject The status subject for {{{@link RVideoIO}}}
     * @param errorSubject THe error submject for RVideoIO
     * @param indexSubject The index subject for RVideoIO
     * @param videoInfoSubject The video info subject for RVideoIO
     */
    public RResponseParser(UUID uuid,
                           Subject<RState> stateSubject,
                           Subject<RError> errorSubject,
                           Subject<VideoIndex> indexSubject,
                           Subject<List<VideoInfo>> videoInfoSubject) {

        this.uuid = uuid;
        this.stateSubject = stateSubject;
        this.errorSubject = errorSubject;
        this.indexSubject = indexSubject;
        this.videoInfoSubject = videoInfoSubject;
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

    /**
     * Handle a response
     * @param command The command that was sent
     * @param msg The raw response to the command from the remote video.
     * @param <B> The type of the response
     */
    public <B extends RResponse> void handle(RCommand<?, B> command, String msg) {
        parse(command, msg).ifPresent(response -> {
            if (response instanceof RequestStatusCmd.Response r) {
                stateSubject.onNext(r.state());
            }
            else if (response instanceof RequestElapsedTimeCmd.Response r) {
                r.getVideoIndex().ifPresent(indexSubject::onNext);
            }
            else if (response instanceof RequestVideoInfoCmd.Response r) {
                videoInfoSubject.onNext(List.of(r));
            }
            else if (response instanceof RequestAllVideoInfosCmd.Response r) {
                videoInfoSubject.onNext(r.getVideos());
            }
        });
    }


}
