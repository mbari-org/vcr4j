package org.mbari.vcr4j.sharktopoda.model.response;

import org.mbari.vcr4j.sharktopoda.SharktopodaState;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T13:30:00
 */
public class RequestStatusResponse {

    private String response;
    private UUID uuid;
    private String status;

    public RequestStatusResponse(String response, UUID uuid, String status) {
        this.response = response;
        this.uuid = uuid;
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getStatus() {
        return status;
    }

    public SharktopodaState.State getState() {
        if (status.equalsIgnoreCase("playing")) return SharktopodaState.State.PLAYING;
        else if (status.equalsIgnoreCase("shuttling forward")) return SharktopodaState.State.SHUTTLE_FORWARD;
        else if (status.equalsIgnoreCase("shuttling reverse")) return SharktopodaState.State.SHUTTLE_REVERSE;
        else if (status.equalsIgnoreCase("paused")) return SharktopodaState.State.PAUSED;
        else return SharktopodaState.State.NOT_FOUND;
    }
}
