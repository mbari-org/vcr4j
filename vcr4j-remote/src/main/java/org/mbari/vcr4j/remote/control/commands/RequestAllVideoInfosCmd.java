package org.mbari.vcr4j.remote.control.commands;

import java.net.URL;
import java.util.List;
import java.util.UUID;

public class RequestAllVideoInfosCmd
        extends RCommand<RequestAllVideoInfosCmd.Request, RequestAllVideoInfosCmd.Response> {

    public static final String Command = "request all information";


    public RequestAllVideoInfosCmd() {
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

        public String getCommand() {
            return command;
        }
    }

    public static class Video implements VideoInfo {
        private UUID uuid;
        private URL url;

        public Video(UUID uuid, URL url) {
            this.uuid = uuid;
            this.url = url;
        }

        public UUID getUuid() {
            return uuid;
        }

        public URL getUrl() {
            return url;
        }

        @Override
        public Long getDurationMillis() {
            return null;
        }

        @Override
        public Double getFrameRate() {
            return null;
        }
    }

    public static class Response extends RResponse {
        private List<VideoInfo> videos;

        public Response(List<VideoInfo> videos) {
            super(Command, null);
            this.videos = List.copyOf(videos);

        }


        public List<VideoInfo> getVideos() {
            return videos;
        }

        @Override
        public boolean success() {
            return true;
        }
    }
}
