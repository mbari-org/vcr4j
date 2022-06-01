package org.mbari.vcr4j.remote.commands;

import java.util.UUID;

public class RResponse {
    private final String response;
    private final String status;

    private UUID uuid;

    public RResponse(String response, String status) {
        this.response = response;
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public String getStatus() {
        return status;
    }

    public boolean isOk() {
        return status.equalsIgnoreCase("ok");
    }

    public boolean isAck() {
        return status.equalsIgnoreCase("ack");
    }
}
