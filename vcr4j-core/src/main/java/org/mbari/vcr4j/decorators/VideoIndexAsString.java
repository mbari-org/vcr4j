package org.mbari.vcr4j.decorators;

import org.mbari.vcr4j.VideoIndex;

/**
 * @author Brian Schlining
 * @since 2016-01-29T11:30:00
 */
public class VideoIndexAsString {

    private final String string;

    public VideoIndexAsString(VideoIndex index) {
        StringBuilder sb = new StringBuilder("{name:'VideoIndex',class:'")
                .append(index.getClass().getName())
                .append("'");

        index.getElapsedTime().ifPresent(duration -> {
            double minutes = duration.toMillis() /  1000D / 60D;
            sb.append(",elapsed_time_minutes:")
                    .append(minutes);
        });

        index.getTimecode().ifPresent(timecode -> {
            sb.append(",timecode:'")
                    .append(timecode.toString())
                    .append("'");
        });

        index.getTimestamp().ifPresent(instant -> {
            sb.append(",timestamp:'")
                    .append(instant)
                    .append("'");
        });

        sb.append("}");

        string = sb.toString();

    }

    @Override
    public String toString() {
        return string;
    }
}
