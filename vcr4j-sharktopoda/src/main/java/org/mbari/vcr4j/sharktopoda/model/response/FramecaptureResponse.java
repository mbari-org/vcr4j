package org.mbari.vcr4j.sharktopoda.model.response;

import java.io.File;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T13:33:00
 */
public class FramecaptureResponse {
    private String response;
    private long elapsedTimeMillis;
    private UUID imageReferenceUuid;
    private File imageLocation;
    private String status;

    public String getResponse() {
        return response;
    }

    public long getElapsedTimeMillis() {
        return elapsedTimeMillis;
    }

    public UUID getImageReferenceUuid() {
        return imageReferenceUuid;
    }

    public File getImageLocation() {
        return imageLocation;
    }

    public String getStatus() {
        return status;
    }
}
