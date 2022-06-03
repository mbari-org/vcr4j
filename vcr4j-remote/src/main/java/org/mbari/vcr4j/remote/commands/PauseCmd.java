package org.mbari.vcr4j.remote.commands;

import java.util.UUID;

public class PauseCmd extends RCommand<PauseCmd.Request, PauseCmd.Response> {

    public static final String Command = "pause";

    public PauseCmd(Request value) {
        super(value);
    }

    public PauseCmd(UUID uuid) {
        this(new Request(uuid));
    }

    public static class Request extends RRequest {
        public Request(UUID uuid) {
            super(Command, uuid);
        }
    }

    public static class Response extends RResponse {
        public Response(String status, UUID uuid) {
            super(Command, status, uuid);
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
