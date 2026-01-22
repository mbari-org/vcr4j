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
            sb.append(",video_command:")
                .append(new VideoCommandAsString(cmd).toString());
        });
        sb.append("}");
        string = sb.toString();

    }

    @Override
    public String toString() {
        return string;
    }
}
