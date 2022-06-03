package org.mbari.vcr4j.remote.commands;

import org.mbari.vcr4j.VideoCommand;

import java.net.URL;
import java.util.List;
import java.util.UUID;

public class RequestAllVideoInfosCmd implements VideoCommand<RequestAllVideoInfosCmd.Request> {

    public static final String Command = "request all information";

    private final Request value;

    public RequestAllVideoInfosCmd() {
        this.value = new Request();
    }

    @Override
    public String getName() {
        return Command;
    }

    @Override
    public Request getValue() {
        return value;
    }

    public static class Request {
        private String command;

        public Request() {
            this.command = Command;
        }

        public String getCommand() {
            return command;
        }
    }

    public static class Video {
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
    }

    public static class Response {
        private String response;
        private List<Video> videos;

        public Response(List<Video> videos) {
            this.response = Command;
            this.videos = List.copyOf(videos);
        }

        public String getResponse() {
            return response;
        }

        public List<Video> getVideos() {
            return videos;
        }
    }
}
