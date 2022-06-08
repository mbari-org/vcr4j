package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;

public class FrameAdvanceCmd extends RCommand<FrameAdvanceCmd.Request, FrameAdvanceCmd.Response> {

    public static final String Command = "seek elapsed time";

    public FrameAdvanceCmd(FrameAdvanceCmd.Request value) {
        super(value);
    }

    public FrameAdvanceCmd(UUID uuid) {
        this(new Request(uuid));
    }

    public static class Request extends RRequest {

        public Request(UUID uuid) {
            super(Command, uuid);
        }
    }

    // Ack
    public static class Response extends RResponse {
        public Response(String status) {
            super(Command, status);
        }

        @Override
        public boolean success() {
            return isAck();
        }
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }
}
