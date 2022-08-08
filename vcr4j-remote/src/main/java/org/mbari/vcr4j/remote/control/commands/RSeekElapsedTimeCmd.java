package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;

/**
 * Seek to the provided time in the video
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RSeekElapsedTimeCmd
        extends RCommand<RSeekElapsedTimeCmd.Request, RSeekElapsedTimeCmd.Response> {

    public static final String Command = "seek elapsed time";

    public RSeekElapsedTimeCmd(Request value) {
        super(value);
    }

    public RSeekElapsedTimeCmd(UUID uuid, Long elapsedTimeMillis) {
        this(new Request(uuid, elapsedTimeMillis));
    }

    public static class Request extends RRequest {

        private Long elapsedTimeMillis;

        public Request(UUID uuid, Long elapsedTimeMillis) {
            super(Command, uuid);
            this.elapsedTimeMillis = elapsedTimeMillis;
        }

        public Long getElapsedTimeMillis() {
            return elapsedTimeMillis;
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
