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
 * Seek to the provided time in the video
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RSeekElapsedTimeCmd
        extends RCommand<RSeekElapsedTimeCmd.Request, RSeekElapsedTimeCmd.Response> {

    public static final String COMMAND = "seek elapsed time";

    public RSeekElapsedTimeCmd(Request value) {
        super(value);
    }

    public RSeekElapsedTimeCmd(UUID uuid, Long elapsedTimeMillis) {
        this(new Request(uuid, elapsedTimeMillis));
    }

    public static class Request extends RRequest {

        private Long elapsedTimeMillis;

        public Request(UUID uuid, Long elapsedTimeMillis) {
            super(COMMAND, uuid);
            this.elapsedTimeMillis = elapsedTimeMillis;
        }

        public Long getElapsedTimeMillis() {
            return elapsedTimeMillis;
        }
    }

    // Ack
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
