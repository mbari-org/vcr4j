package org.mbari.vcr4j.sharktopoda.model.request;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T11:29:00
 */
public class Show {
    private final String command = "show";
    private final UUID uuid;


    public Show(UUID uuid) {
        this.uuid = uuid;
    }
}
