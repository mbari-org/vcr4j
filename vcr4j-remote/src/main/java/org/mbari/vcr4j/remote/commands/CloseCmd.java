package org.mbari.vcr4j.remote.commands;

import java.util.UUID;

public class CloseCmd extends RCommand<CloseCmd.Request> {
    public static final String Command = "close";

    public CloseCmd(CloseCmd.Request value) {
        super(value);
    }

    public static class Request extends RRequest {
        public Request(UUID uuid) {
            super(Command, uuid);
        }
    }

    // Ack
    public static class Response extends RResponse {
        public Response(String response, String status) {
            super(response, status);
        }
    }
}
