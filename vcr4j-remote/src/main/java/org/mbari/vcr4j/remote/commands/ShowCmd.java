package org.mbari.vcr4j.remote.commands;

import java.util.UUID;

public class ShowCmd extends RCommand<ShowCmd.Request> {
    public static final String Command = "show";

    public ShowCmd(ShowCmd.Request value) {
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
