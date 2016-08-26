package org.mbari.vcr4j.sharktopoda.model.response;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T13:29:00
 */
public class RequestElapsedTimeResponse {
    private String response;
    private UUID uuid;
    private long elapsedTimeMillis;

    public RequestElapsedTimeResponse(String response, UUID uuid, long elapsedTimeMillis) {
        this.response = response;
        this.uuid = uuid;
        this.elapsedTimeMillis = elapsedTimeMillis;
    }

    public String getResponse() {
        return response;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getElapsedTimeMillis() {
        return elapsedTimeMillis;
    }
}
