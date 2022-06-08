package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;

public class NoopCmd extends RCommand<NoopCmd.Request, NoopCmd.Response> {
    public NoopCmd() {
        super(null);
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }

    public static class Request extends RRequest {
        public Request(String command, UUID uuid) {
            super(command, uuid);
        }
    }

    public static class Response extends RResponse {
        public Response(String response, String status) {
            super(response, status);
        }

        @Override
        public boolean success() {
            return true;
        }
    }
}
