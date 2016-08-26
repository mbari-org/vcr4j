package org.mbari.vcr4j.sharktopoda.model.request;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T11:38:00
 */
public class FrameAdvance {

    private final String command = "frame advance";
    private UUID uuid;

    public FrameAdvance(UUID uuid) {
        this.uuid = uuid;
    }


}
