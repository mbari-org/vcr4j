package org.mbari.vcr4j.remote.control.commands;

import org.mbari.vcr4j.remote.control.RState;

import java.util.UUID;

/**
 * Request status fo video (e.g. playing, paused, shuttling forward, shuttling reverse)
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RequestStatusCmd extends RCommand<RequestStatusCmd.Request, RequestStatusCmd.Response> {

    public static final String COMMAND = "request status";

    public RequestStatusCmd(Request value) {
        super(value);
    }

    public RequestStatusCmd(UUID uuid) {
        this(new Request(uuid));
    }

    public static class Request extends RRequest {
        public Request(UUID uuid) {
            super(COMMAND, uuid);
        }
    }

    public static class Response extends RResponse {

        private transient String state;
        public Response(String status) {
            super(COMMAND, status);
            this.state = status;
        }

        public String getState() {
            return state;
        }

        public RState state() {
            return RState.parse(state);
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
