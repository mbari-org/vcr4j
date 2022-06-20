package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T11:32:00
 */
public class PlayCmd extends RCommand<PlayCmd.Request, PlayCmd.Response> {
    public static final String Command = "play";

    public PlayCmd(UUID uuid, Double rate) {
        this(new Request(uuid, rate));
    }

    public PlayCmd(UUID uuid) {
        this(uuid, null);
    }

    public PlayCmd(Request request) {
        super(request);
    }

    public static class Request extends RRequest {

        private Double rate;

        public Request(UUID uuid, Double rate) {
            super(Command, uuid);
            this.rate = rate;
        }

        public Request(UUID uuid) {
            this(uuid, null);
        }

        public Double getRate() {
            return rate;
        }
    }

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
