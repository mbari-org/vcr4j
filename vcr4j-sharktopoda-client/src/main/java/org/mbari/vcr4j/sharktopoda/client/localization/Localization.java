package org.mbari.vcr4j.sharktopoda.client.localization;

import com.google.gson.annotations.SerializedName;

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
    @SerializedName("elapsedTimeMillis")
    private Duration elapsedTime;

    /**
     * This is the amount of time that the localization applies to the annotation.
     * It is optional and if not present should be considtered to be 0. At a
     * minimum the bounding box should be drawn from `elapsedTime` to
     * `elapsedTime + duration`
     * */
    @SerializedName("durationMillis")
    private Duration duration;

    /**
     * this is the UUID for the video that the localization applies to.
     */
    private UUID videoReferenceUuid;

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

    /**
     * Default constructor. Will automatically set a localization UUID as this is required in the
     * hashcode, equals method.
     */
    public Localization() {
        localizationUuid = UUID.randomUUID();
    }

    /**
     * Copy constructor
     *
     * @param n
     */
    public Localization(Localization n) {
        this(n.concept, n.elapsedTime, n.localizationUuid, n.x, n.y, n.width, n.height, n.duration, n.annotationUuid);
        this.videoReferenceUuid = n.videoReferenceUuid;
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
        Preconditions.require(localizationUuid != null, "The localizationUuid can not be null");
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
        this(concept, elapsedTime, localizationUuid, x, y, width, height, Duration.ZERO, annotationUuid);
    }

    public Localization(String concept,
                        Duration elapsedTime,
                        UUID localizationUuid,
                        Integer x,
                        Integer y,
                        Integer width,
                        Integer height) {
        this(concept, elapsedTime, localizationUuid, x, y, width, height, Duration.ZERO, null);
    }

    public Localization(String concept,
                        Duration elapsedTime,
                        UUID localizationUuid,
                        Integer x,
                        Integer y,
                        Integer width,
                        Integer height,
                        Duration duration) {
        this(concept, elapsedTime, localizationUuid, x, y, width, height, duration, null);
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
        Preconditions.require(localizationUuid != null, "The localizationUuid can not be null");
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

    public UUID getVideoReferenceUuid() {
        return videoReferenceUuid;
    }

    public void setVideoReferenceUuid(UUID videoReferenceUuid) {
        this.videoReferenceUuid = videoReferenceUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Localization that = (Localization) o;

        return localizationUuid.equals(that.localizationUuid);
    }

    @Override
    public int hashCode() {
        return localizationUuid.hashCode();
    }
}
