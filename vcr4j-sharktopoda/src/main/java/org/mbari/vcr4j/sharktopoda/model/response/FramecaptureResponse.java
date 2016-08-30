package org.mbari.vcr4j.sharktopoda.model.response;

import java.io.File;
import java.net.URL;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T13:33:00
 */
public class FramecaptureResponse {
    private String response = "framecapture";
    private long elapsedTimeMillis;
    private UUID imageReferenceUuid;
    private URL imageLocation;
    private String status;

    public FramecaptureResponse(long elapsedTimeMillis, UUID imageReferenceUuid, URL imageLocation, String status) {
        this.elapsedTimeMillis = elapsedTimeMillis;
        this.imageReferenceUuid = imageReferenceUuid;
        this.imageLocation = imageLocation;
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public long getElapsedTimeMillis() {
        return elapsedTimeMillis;
    }

    public UUID getImageReferenceUuid() {
        return imageReferenceUuid;
    }

    public URL getImageLocation() {
        return imageLocation;
    }

    public String getStatus() {
        return status;
    }
}
