package org.mbari.vcr4j;

import org.mbari.vcr4j.time.Timecode;
import org.mbari.vcr4j.util.Preconditions;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Represents the index into the video. One or more fields may be defined.
 */
public class VideoIndex {

    private final Optional<Instant> timestamp;
    private final Optional<Duration> elapsedTime;
    private final Optional<Timecode> timecode;

    public VideoIndex(Optional<Instant> timestamp, Optional<Duration> elapsedTime, Optional<Timecode> timecode) {
        Preconditions.checkArgument(timestamp != null && elapsedTime != null && timecode != null,
            "VideoIndex does not except null arguments");
        this.timestamp   = timestamp;
        this.elapsedTime = elapsedTime;
        this.timecode    = timecode;
    }
    
    public VideoIndex(Instant timestamp) {
        this(Optional.ofNullable(timestamp), Optional.empty(), Optional.empty());
    }
    
    public VideoIndex(Duration elapsedTime) {
        this(Optional.empty(), Optional.ofNullable(elapsedTime), Optional.empty());
    }
    
    public VideoIndex(Timecode timecode) {
        this(Optional.empty(), Optional.empty(), Optional.ofNullable(timecode));
    }
    
    public Optional<Instant> getTimestamp() {
        return timestamp;
    }
    
    public Optional<Duration> getElapsedTime() {
        return elapsedTime;
    }
    
    public Optional<Timecode> getTimecode() {
        return timecode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VideoIndex that = (VideoIndex) o;

        String tcThis = timecode.map(Timecode::toString).orElse(Timecode.EMPTY_TIMECODE_STRING);
        String tcThat = that.getTimecode().map(Timecode::toString).orElse(Timecode.EMPTY_TIMECODE_STRING);

        if (!tcThis.equals(tcThat)) {
            return false;
        }

        Duration ecThis = elapsedTime.orElse(Duration.ZERO);
        Duration ecThat = that.getElapsedTime().orElse(Duration.ZERO);
        if (!ecThis.equals(ecThat)) {
            return false;
        }

        Instant tsThis = timestamp.orElse(Instant.MIN);
        Instant tsThat = that.getTimestamp().orElse(Instant.MIN);
        return tsThis.equals(tsThat);

    }

    @Override
    public int hashCode() {
        String tcThis = timecode.map(Timecode::toString).orElse(Timecode.EMPTY_TIMECODE_STRING);
        Duration ecThis = elapsedTime.orElse(Duration.ZERO);
        Instant tsThis = timestamp.orElse(Instant.MIN);
        return ((tcThis.hashCode() * 31) + ecThis.hashCode() * 31) + tsThis.hashCode();
    }
}
