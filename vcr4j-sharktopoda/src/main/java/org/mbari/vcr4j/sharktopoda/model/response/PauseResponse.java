package org.mbari.vcr4j.sharktopoda.model.response;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-29T09:47:00
 */
public class PauseResponse {

    private String response;
    private String status;
    private UUID uuid;


    public PauseResponse(UUID uuid, String status) {
        this.response = "open";
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public String getStatus() {
        return status;
    }

    public UUID getUuid() {
        return uuid;
    }
}
