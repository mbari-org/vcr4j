package org.mbari.vcr4j.remote.control.commands;

import org.mbari.vcr4j.VideoCommand;

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
        this(new Request(port, host));
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
            super(Command, null);
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
