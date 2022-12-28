package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;

/**
 * Focus the window for the given video.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class ShowCmd extends RCommand<ShowCmd.Request, ShowCmd.Response> {
    public static final String COMMAND = "show";

    public ShowCmd(Request value) {
        super(value);
    }

    public ShowCmd(UUID uuid) {
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
