package org.mbari.vcr4j.remote.control.commands;

import java.net.URL;
import java.util.UUID;

public class RequestVideoInfoCmd extends RCommand<RequestVideoInfoCmd.Request, RequestVideoInfoCmd.Response> {

    public static final String Command = "request information";

    public RequestVideoInfoCmd() {
        super(new Request());
    }


    @Override
    public Class<Response> responseType() {
        return Response.class;
    }

    public static class Request extends RRequest {

        public Request() {
            super(Command, null);
        }
    }

    public static class Response extends RResponse implements VideoInfo {

        private UUID uuid;
        private URL url;
        private Long durationMillis;
        private Double frameRate;

        public Response(UUID uuid, URL url, Long durationMillis, Double frameRate) {
            super(Command, null);
            this.uuid = uuid;
            this.url = url;
            this.durationMillis = durationMillis;
            this.frameRate = frameRate;
        }


        @Override
        public UUID getUuid() {
            return null;
        }

        public URL getUrl() {
            return url;
        }

        public Long getDurationMillis() {
            return durationMillis;
        }

        public Double getFrameRate() {
            return frameRate;
        }
        @Override
        public boolean success() {
            return getUuid() != null && durationMillis != null && frameRate != null;
        }
    }
}
