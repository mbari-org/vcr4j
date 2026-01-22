package org.mbari.vcr4j.decorators;

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
