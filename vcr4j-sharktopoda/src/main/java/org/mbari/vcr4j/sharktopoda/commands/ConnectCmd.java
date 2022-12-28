package org.mbari.vcr4j.sharktopoda.commands;

import org.mbari.vcr4j.VideoCommand;

/**
 * @author Brian Schlining
 * @since 2016-08-25T17:21:00
 */
public class ConnectCmd implements VideoCommand<ConnectCmd.Params> {
    private final ConnectCmd.Params value;

    public ConnectCmd(int port) {
        this(port, "localhost");
    }

    public ConnectCmd(int port, String host) {
        this(new ConnectCmd.Params(port, host));
    }

    public ConnectCmd(ConnectCmd.Params params) {
        this.value = params;
    }

    @Override
    public String getName() {
        return "connect";
    }

    @Override
    public ConnectCmd.Params getValue() {
        return value;
    }

    public static class Params {
        private final int port;
        private final String host;

        public Params(int port, String host) {
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
}
