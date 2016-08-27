package org.mbari.vcr4j.sharktopoda.model.response;

/**
 * @author Brian Schlining
 * @since 2016-08-27T15:23:00
 */
public class PlayResponse {
    private String response;
    private String status;

    public PlayResponse(String status) {
        this.response = "open";
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public String getStatus() {
        return status;
    }
}
