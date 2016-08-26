package org.mbari.vcr4j.sharktopoda.model.request;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T11:33:00
 */
public class Pause {
    private  final String command = "pause";
    private final UUID uuid;


    public Pause(UUID uuid) {
        this.uuid = uuid;
    }


}
