package org.mbari.vcr4j.remote.control.commands;

import java.net.URL;
import java.util.UUID;

/**
 * Open a video.
 */
public class OpenCmd extends RCommand<OpenCmd.Request, OpenCmd.Response> {
    public static final String COMMAND = "open";

    public OpenCmd(UUID uuid, URL url) {
        this(new Request(uuid, url));
    }

    public OpenCmd(Request request) {
        super(request);
    }

    public static class Request extends RRequest {

        private final URL url;

        public Request(UUID uuid, URL url) {
            super(COMMAND, uuid);
            this.url = url;
        }

        public URL getUrl() {
            return url;
        }

    }

    public static class Response extends RResponse {
        public Response(String status) {
            super(COMMAND, status);
        }

        @Override
        public boolean success() {
            return isOk();
        }
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }

}
