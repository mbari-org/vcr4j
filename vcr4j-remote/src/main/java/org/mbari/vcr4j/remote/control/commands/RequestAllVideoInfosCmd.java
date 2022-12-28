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


    public static class Response extends RResponse {
        private List<VideoInfoBean> videos;

        public Response(List<VideoInfo> videos) {
            super(COMMAND, null);
            this.videos = videos.stream()
                    .map(VideoInfoBean::from)
                    .toList();

        }

        public List<VideoInfoBean> getVideos() {
            return videos;
        }

        @Override
        public boolean success() {
            return true;
        }
    }
}
