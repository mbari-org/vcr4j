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
