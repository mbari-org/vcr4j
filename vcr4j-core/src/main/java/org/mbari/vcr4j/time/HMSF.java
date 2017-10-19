package org.mbari.vcr4j.time;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * HMSF is a container for hour, minute, second, frame as can be
 * represented in a Timecode. Only valid HMSF instances can be constructed.
 * Usage is:
 *
 * <code>
 *     HMSF h1 = new HMSF(1, 2, 3, 4);                      // hour, minute,second, frame
 *     Optional&lt;HMSF&gt; h2 = HMSF.from("01:02:03:04"); // A timecode string
 *     HMSF h3 = HMSF.from(200, 29.97)                      // frames, framerate
 * </code>
 * @author Brian Schlining
 * @since 2015-09-24T14:32:00
 */
public class HMSF {

    private final int hour;
    private final int minute;
    private final int second;
    private final int frame;
    private String stringRepresentation;

    private static final NumberFormat FORMAT = new DecimalFormat() {
        {
            setMaximumFractionDigits(0);
            setMinimumIntegerDigits(2);
            setMaximumIntegerDigits(2);
        }
    };

    private static final Pattern REGEX = Pattern.compile("^\\d{2}:\\d{2}:\\d{2}:\\d{2}");

    /**
     * Default constructor
     * @param hour 0-99
     * @param minute 0-59
     * @param second 0-59
     * @param frame &gt;= 0
     */
    public HMSF(int hour, int minute, int second, int frame) {
        checkArgs(hour, minute, second, frame);
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.frame = frame;
    }

    private void checkArgs(int hour, int minute, int second, int frame) {
        if (hour < 0 || minute < 0 || second < 0 || frame < 0) {
            throw new IllegalArgumentException("All values must be positive.");
        }

        if (hour > 99) {
            throw new IllegalArgumentException("Hour can not be greater than 99");
        }

        if (minute > 59 || second > 59) {
            throw new IllegalArgumentException("Minutes and Seconds can not be greater than 59");
        }
    }

    /**
     * Build an HMSF from a timecode string ("HH:MM:SS::FF")
     * @param timecode A string like "01:02:03:04"
     * @return Optional containing an HMSF if the string is a valid timecode string.
     */
    public static Optional<HMSF> from(String timecode) {
        HMSF hmsf = null;
        if (REGEX.matcher(timecode).matches()) {
            try {
                final int h = Integer.parseInt(timecode.substring(0, 2));
                final int m = Integer.parseInt(timecode.substring(3, 5));
                final int s = Integer.parseInt(timecode.substring(6, 8));
                final int f = Integer.parseInt(timecode.substring(9, 11));
                hmsf = new HMSF(h, m, s, f);
            }
            catch (Exception e) {
                // Do nothing
            }
        }
        return Optional.ofNullable(hmsf);
    }

    public static HMSF from(final double frames, final double frameRate) {
        double f = frames;
        int hour = (int) Math.floor((f / 60.0 / 60.0 / frameRate));
        f = f - (double) hour * 60.0 * 60.0 * frameRate;
        int minute = (int) Math.floor(f / 60.0 / frameRate);
        f = f - (double) minute * 60.0 * frameRate;
        int second = (int) Math.floor(f / frameRate);
        int frame = (int) Math.floor(f - (double) second * frameRate);
        return new HMSF(hour, minute, second, frame);
    }

    public static HMSF zero() {
        return new HMSF(0, 0, 0, 0);
    }



    @Override
    public String toString() {
        if (stringRepresentation == null) {
            stringRepresentation =  FORMAT.format(hour) + ':' +
                    FORMAT.format(minute) + ':' +
                    FORMAT.format(second) + ':' +
                    FORMAT.format(frame);
        }
        return stringRepresentation;
    }

    public int getFrame() {
        return frame;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }
}
