package org.mbari.vcr4j;

import org.mbari.vcr4j.time.Timecode;

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
    
}
