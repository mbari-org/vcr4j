package org.mbari.vcr4j.sharktopoda.client.localization;

import java.time.Duration;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2020-02-10T14:45:00
 */
public class LocalizationMode {

    private Duration elapsedTime;
    private UUID annotationUuid;
    private String mode;

    /**
     * create new mode means draw a new localization and create a new annotation
     */
    public static final String MODE_CREATE_NEW = "create new";

    /**
     *
     */
    public static final String MODE_LOCALIZE_EXISTING = "localize existing";
    public static final String MODE_LOCALIZE_PART = "localize part";

    public LocalizationMode(Duration elapsedTime, UUID annotationUuid, String mode) {
        this.elapsedTime = elapsedTime;
        this.annotationUuid = annotationUuid;
    }

    public LocalizationMode() {

    }

    public Duration getElapsedTime() {
        return elapsedTime;
    }

    public UUID getAnnotationUuid() {
        return annotationUuid;
    }

    public String getMode() {
        return mode;
    }

    public void setElapsedTime(Duration elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void setAnnotationUuid(UUID annotationUuid) {
        this.annotationUuid = annotationUuid;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
