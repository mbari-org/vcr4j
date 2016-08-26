package org.mbari.vcr4j.sharktopoda.model.request;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T11:35:00
 */
public class RequestStatus {
    private final String command = "request status";
    private final UUID uuid;

    public RequestStatus(UUID uuid) {
        this.uuid = uuid;
    }
}
