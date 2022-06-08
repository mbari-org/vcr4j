package org.mbari.vcr4j.remote.control.commands;

import org.mbari.vcr4j.VideoCommand;

/**
 * @author Brian Schlining
 * @since 2016-08-25T17:21:00
 */
public class ConnectCmd implements VideoCommand<ConnectCmd.Request> {

    public static final String Command = "connect";
    private final Request value;

    public ConnectCmd(int port) {
        this(port, "localhost");
    }

    public ConnectCmd(int port, String host) {
        this(new Request(port, host));
    }

    public ConnectCmd(Request request) {
        this.value = request;
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
        private final int port;
        private final String host;

        private final String command = Command;

        public Request(int port, String host) {
            this.port = port;
            this.host = host;
        }

        public String getCommand() {
            return command;
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
            return isAck();
        }
    }

}
