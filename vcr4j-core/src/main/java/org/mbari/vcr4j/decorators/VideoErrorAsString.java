package org.mbari.vcr4j.decorators;

import org.mbari.vcr4j.VideoError;

/**
 * @author Brian Schlining
 * @since 2016-01-29T11:39:00
 */
public class VideoErrorAsString {

    private final String string;

    public VideoErrorAsString(VideoError error) {

        StringBuilder sb = new StringBuilder("{name:'VideoError',class:'")
                .append(error.getClass().getName())
                .append("',has_error:")
                .append(error.hasError());

        error.getVideoCommand().ifPresent(cmd -> {
            sb.append(new VideoCommandAsString(cmd).toString());
        });
        sb.append("}");
        string = sb.toString();

    }
}
