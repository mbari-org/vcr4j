package org.mbari.vcr4j.sharktopoda.commands;

import org.mbari.vcr4j.VideoCommand;

/**
 * @author Brian Schlining
 * @since 2016-08-25T17:21:00
 */
public class ConnectCmd implements VideoCommand<String> {
    private final int port;
    private final String host;

    public ConnectCmd(int port) {
        this(port, "localhost");
    }

    public ConnectCmd(int port, String host) {
        this.port = port;
        this.host = host;
    }

    @Override
    public String getName() {
        return "connect";
    }

    @Override
    public String getValue() {
        return "{\"command\": \"connect\", \"port\"" + port + "\", \"host\": \"" + host + "\"}";
    }
}
