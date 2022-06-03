package org.mbari.vcr4j.remote.commands;

import java.util.UUID;

public class SeekElapsedTimeCmd
        extends RCommand<SeekElapsedTimeCmd.Request, SeekElapsedTimeCmd.Response> {

    public static final String Command = "seek elapsed time";

    public SeekElapsedTimeCmd(Request value) {
        super(value);
    }

    public SeekElapsedTimeCmd(UUID uuid, Long elapsedTimeMillis) {
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
            return isAck();
        }
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }
}
