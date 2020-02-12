package org.mbari.vcr4j.sharktopoda.client.localization;

import java.time.Duration;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2020-02-10T13:46:00
 */
public class Localization {

    private String concept;

    /**
     * This is the `frame` that the annotation is associated with. It MUST always
     * be present
     */
    private Duration elapsedTime;

    /**
     * This is the amount of time that the localization applies to the annotation.
     * It is optional and if not present should be considtered to be 0. At a
     * minimum the bounding box should be drawn from `elapsedTime` to
     * `elapsedTime + duration`
     * */
    private Duration duration;

    /**
     * This is the UUID for the annotation that the bounding box is associated
     * with. An observation may actually have several bounding boxes attached
     * to it.
     */
    private UUID annotationUuid;

    /**
     * This is the UUID for the object that represents the bounding box info
     */
    private UUID localizationUuid;

    /**
     * The upper left corner of the bounding box in pixel image coordinates
     * (+x - right). X is always given in unscaled coordinates relative to the
     * unscale video size
     */
    private Integer x;

    /**
     * The upper left corner of the bounding box in pixel image coordinates
     * (+y is down). Y is always given in unscaled coordinates relative to the
     * unscaled video size
     */
    private Integer y;

    /**
     * The width of the bounding box in pixels The right edge will be located at
     * x + width. Width is always given in unscaled coordinates relative to the
     * unscaled video size
     */
    private Integer width;

    /**
     * The height of the bounding box in pixels. The bottom edge will be located
     * at y + height. Height is always given in unscaled coordinates relative to the
     * unscaled video size
     */
    private Integer height;

    public Localization() {
    }

    public Localization(String concept,
                        Duration elapsedTime,
                        UUID localizationUuid,
                        Integer x,
                        Integer y,
                        Integer width,
                        Integer height,
                        Duration duration,
                        UUID annotationUuid) {
        this.concept = concept;
        this.elapsedTime = elapsedTime;
        this.duration = duration;
        this.annotationUuid = annotationUuid;
        this.localizationUuid = localizationUuid;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Localization(String concept,
                        Duration elapsedTime,
                        UUID localizationUuid,
                        Integer x,
                        Integer y,
                        Integer width,
                        Integer height,
                        UUID annotationUuid) {
        this.concept = concept;
        this.elapsedTime = elapsedTime;
        this.duration = Duration.ZERO;
        this.annotationUuid = annotationUuid;
        this.localizationUuid = localizationUuid;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Localization(String concept,
                        Duration elapsedTime,
                        UUID localizationUuid,
                        Integer x,
                        Integer y,
                        Integer width,
                        Integer height) {
        this.concept = concept;
        this.elapsedTime = elapsedTime;
        this.duration = Duration.ZERO;
        this.localizationUuid = localizationUuid;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Localization(String concept,
                        Duration elapsedTime,
                        UUID localizationUuid,
                        Integer x,
                        Integer y,
                        Integer width,
                        Integer height,
                        Duration duration) {
        this.concept = concept;
        this.elapsedTime = elapsedTime;
        this.duration = duration;
        this.localizationUuid = localizationUuid;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public Duration getElapsedTime() {
        return elapsedTime;
    }

    public Duration getEndTime() {
        if (duration == null) {
            return elapsedTime;
        }
        else {
            return elapsedTime.plus(duration);
        }
    }

    public void setElapsedTime(Duration elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public UUID getAnnotationUuid() {
        return annotationUuid;
    }

    public void setAnnotationUuid(UUID annotationUuid) {
        this.annotationUuid = annotationUuid;
    }

    public UUID getLocalizationUuid() {
        return localizationUuid;
    }

    public void setLocalizationUuid(UUID localizationUuid) {
        this.localizationUuid = localizationUuid;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
}