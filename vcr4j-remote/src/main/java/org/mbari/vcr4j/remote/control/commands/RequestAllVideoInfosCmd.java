package org.mbari.vcr4j.remote.control.commands;

import java.net.URL;
import java.util.List;
import java.util.UUID;

/**
 * Request information about all open videos
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RequestAllVideoInfosCmd
        extends RCommand<RequestAllVideoInfosCmd.Request, RequestAllVideoInfosCmd.Response> {

    public static final String COMMAND = "request all information";


    public RequestAllVideoInfosCmd() {
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

        public String getCommand() {
            return command;
        }
    }

    public static class Video implements VideoInfo {
        private UUID uuid;
        private URL url;

        private Long durationMillis;

        private Double frameRate;

        public Video(UUID uuid, URL url) {
            this(uuid, url, null, null);
        }

        public Video(UUID uuid, URL url, Long durationMillis, Double frameRate) {
            this.uuid = uuid;
            this.url = url;
            this.durationMillis = durationMillis;
            this.frameRate = frameRate;
        }

        public UUID getUuid() {
            return uuid;
        }

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
    }

    public static class Response extends RResponse {
        private List<VideoInfo> videos;

        public Response(List<VideoInfo> videos) {
            super(COMMAND, null);
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
