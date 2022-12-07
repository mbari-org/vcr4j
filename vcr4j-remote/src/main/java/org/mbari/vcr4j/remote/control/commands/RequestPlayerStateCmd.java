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
