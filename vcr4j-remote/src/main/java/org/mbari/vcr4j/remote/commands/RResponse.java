package org.mbari.vcr4j.remote.commands;

import java.util.UUID;

public abstract class RResponse {
    private final String response;
    private final String status;

    private UUID uuid;

    public RResponse(String response, String status) {
        this(response, status, null);
    }

    public RResponse(String response, String status, UUID uuid) {
        this.response = response;
        this.status = status;
        this.uuid = uuid;
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

    public boolean isOk() {
        return status.equalsIgnoreCase("ok");
    }

    public boolean isAck() {
        return status.equalsIgnoreCase("ack");
    }

    public abstract boolean success();
}
