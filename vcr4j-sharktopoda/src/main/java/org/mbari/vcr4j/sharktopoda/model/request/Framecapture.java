package org.mbari.vcr4j.sharktopoda.model.request;

import java.io.File;
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

    public Framecapture(UUID uuid, UUID imageReferenceUuid, String imageLocation) {
        this.uuid = uuid;
        this.imageReferenceUuid = imageReferenceUuid;
        this.imageLocation = imageLocation;

    }
}
