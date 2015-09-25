package org.mbari.vcr4j.util;

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
     * @param value
     * @param timescale
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
