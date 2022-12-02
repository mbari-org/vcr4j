package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;

public class OpenDoneCmd {

    public static final String COMMAND = "open done";

    public static class Request extends RRequest {

        private String status;

        public Request(UUID uuid, String status) {
            super(COMMAND, uuid);
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        public boolean isOk() {
            return status != null && status.equalsIgnoreCase(RResponse.OK);
        }
    }

    public static class Response extends RResponse {
        public Response() {
            super(COMMAND, RResponse.OK);
        }

        @Override
        public boolean success() {
            return isOk();
        }
    }
}
