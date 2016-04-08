package org.mbari.vcr4j.time;

import java.util.Optional;

/**
 * A container for a timecode string in the format of HH:MM:SS:FF (hour:minute:second:frame)
 * If a frameRate is provided the string will be decomposed into frames and framerate
 *
 * @author Brian Schlining
 * @since 2015-09-24T10:59:00
 */
public class Timecode {

    public static final String EMPTY_TIMECODE_STRING = "--:--:--:--";

    private final String stringRepresentation;
    private final double frameRate;
    private final double frames;
    private final Optional<HMSF> hmsf;

    public Timecode(double frames, double frameRate) {
        this.frames = frames;
        this.frameRate = frameRate;
        this.hmsf = Optional.of(HMSF.from(frames, frameRate));
        this.stringRepresentation = hmsf.map(HMSF::toString).orElse(EMPTY_TIMECODE_STRING);
    }

    public Timecode(String timecode, double frameRate) {
        this.stringRepresentation = timecode;
        this.hmsf = HMSF.from(timecode);
        this.frames = hmsf.map(v -> toFrames(v, frameRate)).orElse(Double.NaN);
        this.frameRate = frameRate;
    }

    public Timecode(String timecode) {
        this.stringRepresentation = timecode;
        this.hmsf = HMSF.from(timecode);
        this.frames = Double.NaN;
        this.frameRate = Double.NaN;
    }

    private double toFrames(HMSF hmsf, double frameRate) {
        final int h = hmsf.getHour();
        final int m = hmsf.getMinute();
        final int s = hmsf.getSecond();
        final int f = hmsf.getFrame();
        return ((h * 60 + m) * 60 + s) * frameRate + f;
    }

    /**
     *
     * @return <strong>true</strong> if the timecode is formatted as HH:MM:SS:FF and the
     * individual fields are possible field values. <strong>false</strong> if this is
     * not a valid timecode representation.
     */
    public boolean isValid() {
        return hmsf.isPresent();
    }

    /**
     *
     * @return <strong>true</strong> If the following are all available: A valid
     *  timecode string, a valid framerate and a valid frame value. <strong>false</strong>
     *  otherwise.
     */
    public boolean isComplete() {
        return hmsf.isPresent() && !Double.isNaN(frames) && !Double.isNaN(frameRate);
    }

    /**
     * If the timecode is in a valid format then this will be present. Use
     * this to access the hour, minute, second, frame fields.
     *
     * @return
     */
    public Optional<HMSF> getHMSF() {
        return hmsf;
    }

    /**
     * @return The frameRate or NaN if no framerate was provided
     */
    public double getFrameRate() {
        return frameRate;
    }

    /**
     * @return The frame count represented by the timecode string. If no
     *  frameRate was provided this can not be calculated so NaN is
     *  returned.
     */
    public double getFrames() {
        return frames;
    }

    /**
     * @return the timecode as elapsed seconds. NaN is returned if the frameRate was
     * not provided or if the timecode string is invalid
     */
    public double getSeconds() {
        return frames / frameRate;
    }

    /**
     * The string represented. This may NOT necessarily be a valid timecode string
     * if you did not provide one. You can use the <code>isValid()</code> method
     * to check if the string is a valid timecode representation.
     * @return
     */
    @Override
    public String toString() {
        return stringRepresentation;
    }


    public static Timecode zero() {
        return new Timecode(0, FrameRates.NTSC);
    }


}


