package org.mbari.vcr4j.remote.control;

/*-
 * #%L
 * vcr4j-remote
 * %%
 * Copyright (C) 2008 - 2026 Monterey Bay Aquarium Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import io.reactivex.rxjava3.subjects.Subject;
import org.mbari.vcr4j.VideoIndex;

import org.mbari.vcr4j.remote.control.commands.*;

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
    private final Subject<List<? extends VideoInfo>> videoInfoSubject;

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
                           Subject<List<? extends VideoInfo>> videoInfoSubject) {

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
            var e = new RError(false, true, false, command, "An exception was throw while parsing the reponse", ex);
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
    public <B extends RResponse> Optional<B> handle(RCommand<?, B> command, String msg) {
        var opt = parse(command, msg);
        opt.ifPresent(response -> {
            if (response instanceof RequestPlayerStateCmd.Response r) {
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
        return opt;
    }


}
