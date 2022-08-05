package org.mbari.vcr4j.remote.control.commands;

import org.mbari.vcr4j.remote.control.RState;

import java.util.UUID;

public class RequestStatusCmd extends RCommand<RequestStatusCmd.Request, RequestStatusCmd.Response> {

    public static final String Command = "request status";

    public RequestStatusCmd(Request value) {
        super(value);
    }

    public RequestStatusCmd(UUID uuid) {
        this(new Request(uuid));
    }

    public static class Request extends RRequest {
        public Request(UUID uuid) {
            super(Command, uuid);
        }
    }

    public static class Response extends RResponse {

        private transient String state;
        public Response(String status) {
            super(Command, status);
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
