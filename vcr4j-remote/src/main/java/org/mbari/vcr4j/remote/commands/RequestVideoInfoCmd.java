package org.mbari.vcr4j.remote.commands;

import org.mbari.vcr4j.VideoCommand;

import java.net.URL;
import java.util.UUID;

public class RequestVideoInfoCmd
        implements VideoCommand<RequestVideoInfoCmd.Request> {

    public static final String Command = "request information";
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

    public static class Response extends RResponse {
        private URL url;
        private Long durationMillis;
        private Double frameRate;

        public Response(UUID uuid, URL url, Long durationMillis, Double frameRate) {
            super(Command, null, uuid);
            this.url = url;
            this.durationMillis = durationMillis;
            this.frameRate = frameRate;
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
