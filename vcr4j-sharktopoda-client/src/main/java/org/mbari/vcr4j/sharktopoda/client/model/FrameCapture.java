package org.mbari.vcr4j.sharktopoda.client.model;

import java.nio.file.Path;
import java.time.Duration;

/**
 * @author Brian Schlining
 * @since 2019-12-05T13:52:00
 */
public class FrameCapture {
    Path saveLocation;
    Duration snapTime;

    public FrameCapture(Path saveLocation, Duration snapTime) {
        this.saveLocation = saveLocation;
        this.snapTime = snapTime;
    }

    public FrameCapture() {
    }

    public Path getSaveLocation() {
        return saveLocation;
    }

    public Duration getSnapTime() {
        return snapTime;
    }
}
