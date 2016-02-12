package org.mbari.vcr4j.kipro.decorators;

import org.mbari.vcr4j.decorators.VideoCommandAsString;
import org.mbari.vcr4j.kipro.QuadError;

/**
 * @author Brian Schlining
 * @since 2016-02-11T13:25:00
 */
public class QuadErrorAsString {

    private final String string;

    public QuadErrorAsString(QuadError error) {
        StringBuilder sb = new StringBuilder("{name:'VideoError',class='")
                .append("',has_error:'")
                .append(error.hasError())
                .append("',has_connection_error:'")
                .append(error.hasConnectionError())
                .append("'");

        error.getVideoCommand().ifPresent(vc -> {
            sb.append(",video_command:'")
                    .append(new VideoCommandAsString(vc).toString())
                    .append("'");
        });

        error.getException().ifPresent(e -> {
            sb.append(",exception:{class:'")
                    .append(e.getClass().getName())
                    .append("',message:'")
                    .append(e.getLocalizedMessage())
                    .append("'}");
        });

        sb.append("}");
        string = sb.toString();
    }

    @Override
    public String toString() {
        return string;
    }
}
