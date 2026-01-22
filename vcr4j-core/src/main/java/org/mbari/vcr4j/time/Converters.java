package org.mbari.vcr4j.time;

/*-
 * #%L
 * vcr4j-core
 * %%
 * Copyright (C) 2008 - 2026 Monterey Bay Aquarium Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
