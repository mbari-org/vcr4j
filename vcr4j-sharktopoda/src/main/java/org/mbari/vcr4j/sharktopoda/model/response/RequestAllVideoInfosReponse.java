package org.mbari.vcr4j.sharktopoda.model.response;

import java.net.URL;
import java.util.List;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T13:26:00
 */
public class RequestAllVideoInfosReponse implements IVideoInfo {

    private String response;
    private List<Video> videos;

    public RequestAllVideoInfosReponse(List<Video> videos) {
        this.response = "request all information";
        this.videos = videos;
    }

    public String getResponse() {
        return response;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public class Video {
        UUID uuid;
        URL url;

        public UUID getUuid() {
            return uuid;
        }

        public URL getUrl() {
            return url;
        }
    }
}
