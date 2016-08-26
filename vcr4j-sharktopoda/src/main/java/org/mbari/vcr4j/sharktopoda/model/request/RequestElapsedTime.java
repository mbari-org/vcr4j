package org.mbari.vcr4j.sharktopoda.model.request;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T11:34:00
 */
public class RequestElapsedTime {
    private final String command = "request elapsed time";
    private final UUID uuid;

    public RequestElapsedTime(UUID uuid) {
        this.uuid = uuid;
    }
}
