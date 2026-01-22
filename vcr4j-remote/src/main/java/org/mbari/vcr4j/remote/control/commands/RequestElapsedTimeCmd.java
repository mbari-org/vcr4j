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

import org.mbari.vcr4j.VideoIndex;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * Request the current elapsed time into the video.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RequestElapsedTimeCmd
        extends RCommand<RequestElapsedTimeCmd.Request, RequestElapsedTimeCmd.Response> {
    public static final String COMMAND = "request elapsed time";

    public RequestElapsedTimeCmd(Request value) {
        super(value);
    }

    public RequestElapsedTimeCmd(UUID uuid) {
        this(new Request(uuid));
    }

    public static class Request extends RRequest {
        public Request(UUID uuid) {
            super(COMMAND, uuid);
        }
    }

    public static class Response extends RResponse {
        private Long elapsedTimeMillis;

        public Response(Long elapsedTimeMillis) {
            super(COMMAND, RResponse.OK);
            this.elapsedTimeMillis = elapsedTimeMillis;
        }

        public Response() {
            super(COMMAND, RResponse.FAILED);
        }

        public Long getElapsedTimeMillis() {
            return elapsedTimeMillis;
        }

        @Override
        public boolean isOk() {
            return elapsedTimeMillis != null;
        }

        @Override
        public boolean success() {
            return elapsedTimeMillis != null;
        }

        public Optional<VideoIndex> getVideoIndex() {
            if (elapsedTimeMillis != null) {
                var vi = new VideoIndex(Duration.ofMillis(elapsedTimeMillis));
                return Optional.of(vi);
            }
            return Optional.empty();
        }
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }
}
