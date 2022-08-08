package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;

/**
 * Advance a single frame
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class FrameAdvanceCmd extends RCommand<FrameAdvanceCmd.Request, FrameAdvanceCmd.Response> {

    public static final String Command = "frame advance";

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
            return isOk();
        }
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }
}
