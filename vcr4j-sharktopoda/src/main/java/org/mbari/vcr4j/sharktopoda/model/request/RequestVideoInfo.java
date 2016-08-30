package org.mbari.vcr4j.sharktopoda.model.request;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T11:30:00
 */
public class RequestVideoInfo {
    private final String command = "request video information";
    private final UUID uuid;

    public RequestVideoInfo(UUID uuid) {
        this.uuid = uuid;
    }


}
