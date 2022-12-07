package org.mbari.vcr4j.remote.control.commands;

import java.net.URL;
import java.util.UUID;

/**
 * Request info about the video in the focused window in the video player
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RequestVideoInfoCmd extends RCommand<RequestVideoInfoCmd.Request, RequestVideoInfoCmd.Response> {

    public static final String COMMAND = "request information";

    public RequestVideoInfoCmd() {
        super(new Request());
    }


    @Override
    public Class<Response> responseType() {
        return Response.class;
    }

    public static class Request extends RRequest {

        public Request() {
            super(COMMAND, null);
        }
    }

    public static class Response extends RResponse implements VideoInfo {

        private UUID uuid;
        private URL url;
        private Long durationMillis;
        private Double frameRate;
        private Boolean isKey;

        public Response(UUID uuid, URL url, Long durationMillis, Double frameRate) {
            super(COMMAND, null);
            this.uuid = uuid;
            this.url = url;
            this.durationMillis = durationMillis;
            this.frameRate = frameRate;
        }

        public Response() {
            super(COMMAND, RResponse.FAILED);
        }


        @Override
        public UUID getUuid() {
            return uuid;
        }

        @Override
        public URL getUrl() {
            return url;
        }

        @Override
        public Long getDurationMillis() {
            return durationMillis;
        }

        @Override
        public Double getFrameRate() {
            return frameRate;
        }

        @Override
        public Boolean isKey() {
            return isKey;
        }

        @Override
        public boolean success() {
            return getUuid() != null && durationMillis != null && frameRate != null;
        }
    }
}
