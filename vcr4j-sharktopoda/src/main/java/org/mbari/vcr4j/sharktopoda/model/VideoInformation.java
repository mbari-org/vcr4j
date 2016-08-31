package org.mbari.vcr4j.sharktopoda.model;


import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-31T13:40:00
 */
public class VideoInformation {

    private final Collection<Video> videos;
    private final RequestType requestType;

    public VideoInformation(RequestType requestType, Collection<Video> videos) {
        this.requestType = requestType;
        this.videos = videos;
    }

    public enum RequestType {
        ALL,
        FOCUSED
    }

    public static class Video {
        UUID uuid;
        URL url;

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
    }

    public Collection<Video> getVideos() {
        return new ArrayList<>(videos);
    }
}
