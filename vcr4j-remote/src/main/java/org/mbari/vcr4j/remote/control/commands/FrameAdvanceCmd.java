package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;

/**
 * Advance a single frame
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class FrameAdvanceCmd extends RCommand<FrameAdvanceCmd.Request, FrameAdvanceCmd.Response> {

    public static final String COMMAND = "frame advance";

    public FrameAdvanceCmd(FrameAdvanceCmd.Request value) {
        super(value);
    }

    public FrameAdvanceCmd(UUID uuid) {
        this(new Request(uuid));
    }

    public FrameAdvanceCmd(UUID uuid, boolean forward) {
        this(new Request(uuid, forward));
    }

    public static class Request extends RRequest {

        private Integer direction;

        public Request(UUID uuid) {
            this(uuid, true);
        }

        public Request(UUID uuid, boolean forward) {
            super(COMMAND, uuid);
            direction = forward ? 1 : -1;
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
