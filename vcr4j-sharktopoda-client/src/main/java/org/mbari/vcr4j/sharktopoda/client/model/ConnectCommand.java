package org.mbari.vcr4j.sharktopoda.client.model;

/**
 * @author Brian Schlining
 * @since 2017-12-05T17:10:00
 */
public class ConnectCommand {
    private String command = "connect";
    private int port;
    private String host;

    public ConnectCommand() {
    }

    public ConnectCommand(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
