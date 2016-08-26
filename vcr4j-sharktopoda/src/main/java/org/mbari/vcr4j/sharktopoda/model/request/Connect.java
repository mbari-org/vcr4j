package org.mbari.vcr4j.sharktopoda.model.request;

/**
 * @author Brian Schlining
 * @since 2016-08-26T11:28:00
 */
public class Connect {
    private final String command = "connect";
    private int port;
    private String host;

    public Connect(int port, String host) {
        this.port = port;
        this.host = host;
    }

    
}
