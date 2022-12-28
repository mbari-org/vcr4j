package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;

public class PingCmd extends RCommand<PingCmd.Request, PingCmd.Response> {

    public static final String COMMAND = "ping";

    public PingCmd(Request value) {
        super(value);
    }


    public PingCmd() {
        this(new Request());
    }

    public static class Request extends RRequest {
        public Request() {
            super(COMMAND, null);
        }
    }

    public static class Response extends RResponse {
        public Response() {
            super(COMMAND, "ok");
        }

        @Override
        public boolean success() {
            return isOk();
        }
    }

    @Override
    public Class<PingCmd.Response> responseType() {
        return PingCmd.Response.class;
    }
}
