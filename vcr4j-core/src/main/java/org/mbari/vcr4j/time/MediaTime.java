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

/**
 * Time modeled after Apple's CoreMedia CMTime.
 *
 * A MediaTime is represented as a rational number, with a numerator (an long value),
 * and a denominator (an int timescale). Conceptually, the timescale specifies
 * the fraction of a second each unit in the numerator occupies. Thus if the
 * timescale is 4, each unit represents a quarter of a second; if the timescale
 * is 10, each unit represents a tenth of a second, and so on.
 *
 * @author Brian Schlining
 * @since 2015-09-24T10:45:00
 */
public class MediaTime {

    private final long value;
    private final int timescale;
    private final double seconds;
    private final boolean rounded;
    private String stringRep;

    /**
     * For example to create a MediaTime that's 200 seconds into a video that
     * has 29.97 frame rate use <code>new MediaTime(20000, 2997)</code>
     * @param value numerator
     * @param timescale denominator
     * @param rounded Indiates if this value is estimated
     */
    public MediaTime(long value, int timescale, boolean rounded) {
        this.timescale = timescale;
        this.value = value;
        this.seconds = value / (double) timescale;
        this.rounded = rounded;
    }

    public MediaTime(long value, int timescale) {
        this(value, timescale, false);
    }

    public long getValue() {
        return value;
    }

    public int getTimescale() {
        return timescale;
    }

    public double getSeconds() {
        return seconds;
    }

    public boolean isRounded() {
        return rounded;
    }

    public static MediaTime fromSeconds(double seconds, int timescale) {
        double value = seconds * timescale;
        boolean rounded = value % 1 != 0;
        return new MediaTime(Math.round(value), timescale, rounded);
    }

    @Override
    public String toString() {
        if (stringRep == null) {
            stringRep = "MediaTime{" +
                    "seconds=" + seconds +
                    " (" + value + "/" + timescale + ")" +
                    ", rounded=" + rounded +
                    '}';
        }
        return stringRep;
    }


}
