package org.mbari.vcr4j.decorators;

import org.mbari.vcr4j.VideoCommand;

/**
 * @author Brian Schlining
 * @since 2016-01-29T14:22:00
 */
public class VideoCommandAsString {

    private final String string;

    public VideoCommandAsString(VideoCommand cmd) {
        StringBuilder sb = new StringBuilder("{name:'VideoCommand',class:'")
                .append(cmd.getClass().getName())
                .append("',name:'")
                .append(cmd.getName())
                .append("'");

        if (cmd.getValue() != null) {
            sb.append(",value:'")
                    .append(cmd.getValue())
                    .append("'");
        }
        sb.append("}");
        string = sb.toString();
    }

    @Override
    public String toString() {
        return string;
    }
}
