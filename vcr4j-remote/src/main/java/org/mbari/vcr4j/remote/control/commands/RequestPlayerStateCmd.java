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
package org.mbari.vcr4j.remote.control.commands;

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

        private Long elapsedTimeMillis;

        /**
         * A successful response
         * @param state The name of the state, see {@link RState.State#getName()}
         * @param rate The playback rate
         * @param elapsedTimeMillis The elapsed time of the displayed frame. May be null
         */
        public Response(String state, Double rate, Long elapsedTimeMillis) {
            super(COMMAND, RResponse.OK);
            this.state = state;
            this.rate = rate;
            this.elapsedTimeMillis = elapsedTimeMillis;
        }

        /**
         * A successful response
         * @param state The name of the state, see {@link RState.State#getName()}
         * @param rate The playback rate
         */
        public Response(String state, Double rate) {
            this(state, rate, null);
        }

        /**
         * A successful response
         * @param state The name of the state, see {@link RState.State#getName()}
         */
        public Response(String state) {
            this(state, null, null);
        }

        /**
         * A failed response
         * @param cause Why the request failed
         * @return A response with status "failed"
         */
        public static Response failed(String cause) {
            return new Response(cause, true);
        }

        private Response(String cause, boolean failed) {
            super(COMMAND, RResponse.FAILED, cause);
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
            // "not found" means the player has no video loaded — a well-formed response,
            // but callers using success() as a proxy for "video is accessible" would be
            // misled if we returned true for it. Same rationale for UNKNOWN_ERROR
            // (unrecognized state string).
            if (!isOk() || state == null) {
                return false;
            }
            var s = state().getState();
            return s != RState.State.NOT_FOUND && s != RState.State.UNKNOWN_ERROR;
        }
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }
}
