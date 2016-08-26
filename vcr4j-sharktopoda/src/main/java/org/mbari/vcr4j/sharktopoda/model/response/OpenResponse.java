package org.mbari.vcr4j.sharktopoda.model.response;

/**
 * @author Brian Schlining
 * @since 2016-08-26T13:22:00
 */
public class OpenResponse {
    private String response;
    private String status;



    public OpenResponse(String status) {
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
