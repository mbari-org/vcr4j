package org.mbari.vcr4j.remote.control;

public class SimpleResponse {
    private String response;
    private String status;

    private String raw;

    public SimpleResponse(String response, String status, String raw) {
        this.response = response;
        this.status = status;
        this.raw = raw;
    }

    public SimpleResponse(String response, String status) {
        this(response, status, null);
    }

    public String getResponse() {
        return response;
    }

    public String getStatus() {
        return status;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }
}
