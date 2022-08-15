package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;

/**
 * Pause video playback
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class PauseCmd extends RCommand<PauseCmd.Request, PauseCmd.Response> {

    public static final String COMMAND = "pause";

    public PauseCmd(Request value) {
        super(value);
    }

    public PauseCmd(UUID uuid) {
        this(new Request(uuid));
    }

    public static class Request extends RRequest {
        public Request(UUID uuid) {
            super(COMMAND, uuid);
        }
    }

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
