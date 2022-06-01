package org.mbari.vcr4j.remote.commands;

import org.mbari.vcr4j.VideoCommand;

import java.net.URL;
import java.util.UUID;

public class OpenCmd extends RCommand<OpenCmd.Request> {
    public static final String Command = "open";

    public OpenCmd(UUID uuid, URL url) {
        this(new Request(uuid, url));
    }

    public OpenCmd(Request request) {
        super(request);
    }

    public static class Request extends RRequest {

        private final URL url;

        public Request(UUID uuid, URL url) {
            super(Command, uuid);
            this.url = url;
        }

        public URL getUrl() {
            return url;
        }

    }

    public static class Response extends RResponse {
        public Response(String response, String status) {
            super(response, status);
        }
    }

}
