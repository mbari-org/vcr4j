package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;

/**
 * Command to close the remote video
 */
public class CloseCmd extends RCommand<CloseCmd.Request, CloseCmd.Response> {
    public static final String COMMAND = "close";

    public CloseCmd(CloseCmd.Request value) {
        super(value);
    }

    public CloseCmd(UUID uuid) {
        this(new Request(uuid));
    }

    public static class Request extends RRequest {
        public Request(UUID uuid) {
            super(COMMAND, uuid);
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
