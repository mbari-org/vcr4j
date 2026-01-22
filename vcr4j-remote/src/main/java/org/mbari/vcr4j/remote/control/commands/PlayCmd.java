package org.mbari.vcr4j.remote.control.commands;

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

import java.util.UUID;


/**
 * Play the video. THe rate field set's the playback speed. The rate should be a valid value for the video
 * player.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class PlayCmd extends RCommand<PlayCmd.Request, PlayCmd.Response> {
    public static final String COMMAND = "play";

    public PlayCmd(UUID uuid, Double rate) {
        this(new Request(uuid, rate));
    }

    public PlayCmd(UUID uuid) {
        this(uuid, null);
    }

    public PlayCmd(Request request) {
        super(request);
    }

    public static class Request extends RRequest {

        private Double rate;

        public Request(UUID uuid, Double rate) {
            super(COMMAND, uuid);
            this.rate = rate;
        }

        public Request(UUID uuid) {
            this(uuid, null);
        }

        public Double getRate() {
            return rate;
        }
    }

    public static class Response extends RResponse {
        public Response(String status) {
            super(COMMAND, status);
        }

        @Override
        public boolean success() {
            return isOk();
        }
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }

}
