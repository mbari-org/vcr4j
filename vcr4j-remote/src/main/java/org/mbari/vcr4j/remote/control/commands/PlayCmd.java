package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;


/**
 * Play the video. THe rate field set's the playback speed. The rate should be a valid value for the video
 * player.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class PlayCmd extends RCommand<PlayCmd.Request, PlayCmd.Response> {
    public static final String COMMAND = "play";

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
            super(COMMAND, uuid);
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
