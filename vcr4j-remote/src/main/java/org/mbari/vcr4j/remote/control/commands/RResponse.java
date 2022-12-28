package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;

/**
 * Base class for all remote responses.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public abstract class RResponse {

    public static final String OK = "ok";
    public static final String FAILED = "failed";
    private final String response;
    private final String status;
    private final String cause; // Cause of failure. Optional

    public RResponse(String response, String status) {
        this(response, status, null);

    }

    public RResponse(String response, String status, String cause) {
        this.response = response;
        this.status = status;
        this.cause = cause;
    }

    public String getResponse() {
        return response;
    }

    public String getStatus() {
        return status;
    }

    public String getCause() {
        return cause;
    }

    public boolean isOk() {
        return status != null && status.equalsIgnoreCase(OK);
    }

    public abstract boolean success();
}
