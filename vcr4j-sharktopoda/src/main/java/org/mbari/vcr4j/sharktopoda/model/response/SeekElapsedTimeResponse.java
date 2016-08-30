package org.mbari.vcr4j.sharktopoda.model.response;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-30T10:07:00
 */
public class SeekElapsedTimeResponse {

    private String response;
    private UUID uuid;
    private Long elapsedTimeMillis;

    public SeekElapsedTimeResponse(String response, UUID uuid, Long elapsedTimeMillis) {
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

    public Long getElapsedTimeMillis() {
        return elapsedTimeMillis;
    }
}
