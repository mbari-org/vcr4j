package org.mbari.vcr4j.remote.commands;

import org.mbari.vcr4j.VideoCommand;

import java.net.URL;
import java.util.UUID;

public class RequestVideoInfoCmd implements VideoCommand<RequestVideoInfoCmd.Request> {

    public static final String Command = "request video information";
    private final Request value;

    public RequestVideoInfoCmd() {
        value = new Request();
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

    public static class Response {
        private String response;
        private UUID uuid;
        private URL url;
        private Long durationMillis;
        private Double frameRate;

        public Response(String response, UUID uuid, URL url, Long durationMillis, Double frameRate) {
            this.response = response;
            this.uuid = uuid;
            this.url = url;
            this.durationMillis = durationMillis;
            this.frameRate = frameRate;
        }

        public String getResponse() {
            return response;
        }

        public UUID getUuid() {
            return uuid;
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
    }
}
