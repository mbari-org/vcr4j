package org.mbari.vcr4j.remote.control.commands;

import org.mbari.vcr4j.VideoCommand;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-25T17:21:00
 */
public class ConnectCmd extends RCommand<ConnectCmd.Request, ConnectCmd.Response> {

    public static final String Command = "connect";

    public ConnectCmd(int port) {
        this(port, "localhost");
    }

    public ConnectCmd(int port, String host) {
        this(port, host, null);
    }

    public ConnectCmd(int port, String host, UUID uuid) {
        this(new Request(port, host, uuid));
    }

    public ConnectCmd(Request request) {
        super(request);
    }
    

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }


    public static class Request extends RRequest {
        private final int port;
        private final String host;

        public Request(int port, String host) {
            this(port, host, null);
        }

        public Request(int port, String host, UUID uuid) {
            super(Command, uuid);
            this.port = port;
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public String getHost() {
            return host;
        }
    }

    public static class Response extends RResponse {
        public Response(String status) {
            super(Command, status);
        }

        @Override
        public boolean success() {
            return isOk();
        }
    }

}
