package org.mbari.vcr4j.sharktopoda.model.request;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T11:37:00
 */
public class Framecapture {
    private final String command = "framecapture";
    private UUID uuid;
    private String imageLocation;
    private UUID imageReferenceUuid;

    public Framecapture(UUID uuid, String imageLocation, UUID imageReferenceUuid) {
        this.uuid = uuid;
        this.imageLocation = imageLocation;
        this.imageReferenceUuid = imageReferenceUuid;
    }
}
