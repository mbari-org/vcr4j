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

import org.mbari.vcr4j.remote.control.RState;

import java.util.UUID;

/**
 * Request status fo video (e.g. playing, paused, shuttling forward, shuttling reverse)
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RequestPlayerStateCmd extends RCommand<RequestPlayerStateCmd.Request, RequestPlayerStateCmd.Response> {

    public static final String COMMAND = "request player state";

    public RequestPlayerStateCmd(Request value) {
        super(value);
    }

    public RequestPlayerStateCmd(UUID uuid) {
        this(new Request(uuid));
    }

    public static class Request extends RRequest {
        public Request(UUID uuid) {
            super(COMMAND, uuid);
        }
    }

    public static class Response extends RResponse {

        private String state;

        private Double rate;

        // OK, this is a bit of a hack. GSOn assigns this value when parsing but we
        // don't include it in the constructor to respect how VideoControl normally requests state
        // via a call to requestRate ( a holdover from VCR support)
        private Long elapsedTimeMillis;

        public Response(String status, Double rate) {
            super(COMMAND, status);
            this.state = status;
            this.rate = rate;
        }

        public Response(String status) {
            this(status, null);
        }

        public String getState() {
            return state;
        }

        public RState state() {
            return RState.parse(state);
        }

        public Double getRate() {
            return rate;
        }

        public Long getElapsedTimeMillis() {
            return elapsedTimeMillis;
        }

        @Override
        public boolean success() {
            return state != null;
        }
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }
}
