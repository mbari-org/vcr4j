package org.mbari.vcr4j.remote.control.commands.loc;

import java.util.Objects;
import java.util.UUID;

public class Localization {
    private UUID uuid;
    private String concept;
    private Long elapsedTimeMillis;
    private Long durationMillis; // FIXME Do we need this?
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;

    private String color = "#DDDDDD"; // hex string like "#FFDDCC"

    public Localization(UUID uuid,
                        String concept,
                        Long elapsedTimeMillis,
                        Long durationMillis,
                        Integer x,
                        Integer y,
                        Integer width,
                        Integer height,
                        String color) {
        this.uuid = uuid;
        this.concept = concept;
        this.elapsedTimeMillis = elapsedTimeMillis;
        this.durationMillis = durationMillis;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public Localization() {
    }


    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public Long getElapsedTimeMillis() {
        return elapsedTimeMillis;
    }

    public void setElapsedTimeMillis(Long elapsedTimeMillis) {
        this.elapsedTimeMillis = elapsedTimeMillis;
    }

    public Long getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(Long durationMillis) {
        this.durationMillis = durationMillis;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Localization that = (Localization) o;
        return Objects.equals(uuid, that.uuid) && Objects.equals(concept, that.concept) && Objects.equals(elapsedTimeMillis, that.elapsedTimeMillis) && Objects.equals(durationMillis, that.durationMillis) && Objects.equals(x, that.x) && Objects.equals(y, that.y) && Objects.equals(width, that.width) && Objects.equals(height, that.height) && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, concept, elapsedTimeMillis, durationMillis, x, y, width, height, color);
    }
}
