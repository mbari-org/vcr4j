package org.mbari.vcr4j.sharktopoda.model.request;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T11:35:00
 */
public class SeekElapsedTime {

    private final String command = "seek elapsed time";
    private final UUID uuid;
    private final long elapsedTimeMillis;

    public SeekElapsedTime(UUID uuid, long elapsedTimeMillis) {
        this.uuid = uuid;
        this.elapsedTimeMillis = elapsedTimeMillis;
    }
}
