package org.mbari.vcr4j.sharktopoda.model.request;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T11:32:00
 */
public class Play {
    private final String command = "play";
    private final Double rate;
    private final UUID uuid;

    public Play(UUID uuid, Double rate) {
        this.rate = rate;
        this.uuid = uuid;
    }

    public Play(UUID uuid) {
        this(uuid, null);
    }


}
