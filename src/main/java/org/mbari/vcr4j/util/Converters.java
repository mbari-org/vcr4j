package org.mbari.vcr4j.util;

import javax.print.attribute.standard.Media;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2015-09-24T14:08:00
 */
public class Converters {

    public static Optional<MediaTime> toMediaTime(Timecode timecode) {
        MediaTime mt = null;
        try {
            if (timecode.isComplete()) {
                final double estimatedFrameRate = Math.round(timecode.getFrameRate());
                final double estimatedFrames = Math.round(timecode.getFrames());
                final boolean rounded = (estimatedFrameRate % 1) + (estimatedFrames % 1) != 0;
                mt = new MediaTime((long) estimatedFrames, (int) estimatedFrameRate, rounded);
            }
        }
        catch (Exception e) {
            // Do nothing
        }
        return Optional.ofNullable(mt);
    }

    /**
     * Returns a timecode representation of the mediatime. The frame
     * section of the timecode is represented by decimal fractional seconds.
     * @param mediaTime Value to convert
     * @return A timecode estimating the mediaTime
     */
    public static Timecode toTimecode(MediaTime mediaTime) {
        double seconds = mediaTime.getSeconds();
        return new Timecode(seconds * 100, 100);
    }

    /**
     * Convert an HMSF to a timecode.
     * @param hmsf The HMSF to convert
     * @return An timecode where isComplete is false.
     */
    public static Timecode toTimecode(HMSF hmsf) {
        return new Timecode(hmsf.toString());
    }
}
