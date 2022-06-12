package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;

public abstract class RResponse {

    public static final String OK = "ok";
    public static final String FAILED = "failed";
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
        return status != null && status.equalsIgnoreCase(OK);
    }

    public abstract boolean success();
}
