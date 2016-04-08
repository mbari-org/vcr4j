package org.mbari.vcr4j.decorators;

import org.mbari.vcr4j.VideoState;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Schlining
 * @since 2016-01-29T11:16:00
 */
public class VideoStateAsString {

    private String string;

    public VideoStateAsString(VideoState s) {
        StringBuilder sb = new StringBuilder("{name:'VideoState',class:'")
                .append(s.getClass().getName())
                .append("',status:[");

        List<String> states = new ArrayList<>();
        if (s.isConnected()) {
            states.add("'connected'");
        }

        if (s.isCueingUp()) {
            states.add("'cueing up'");
        }

        if (s.isPlaying()) {
            states.add("'playing'");
        }

        if (s.isReverseDirection()) {
            states.add("'reverse direction'");
        }

        if (s.isRewinding()) {
            states.add("'rewinding'");
        }

        if (s.isShuttling()) {
            states.add("'shuttling'");
        }

        if (s.isStopped()) {
            states.add("'stopped'");
        }

        String status = String.join(",", states);

        sb.append(status)
                .append("]}");

        string = sb.toString();

    }

    @Override
    public String toString() {
        return string;
    }
}
