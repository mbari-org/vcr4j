package org.mbari.vcr4j.commands;

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

import org.mbari.vcr4j.VideoCommand;

public enum VideoCommands implements VideoCommand<Void> {
    FAST_FORWARD("fast forward"),
    PAUSE("pause"),
    PLAY("play"),
    REQUEST_DEVICE_TYPE("request device type"),
    REQUEST_ELAPSED_TIME("request elapsed time"),
    REQUEST_INDEX("request index"),  // This should always return an index for the video
    REQUEST_STATUS("request status"),
    REQUEST_TIMECODE("request timecode"),
    REQUEST_TIMESTAMP("request timestamp"),
    REWIND("rewind"),
    STOP("stop");

    private final String name;

    VideoCommands(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Void getValue() {
        return null;
    }
}
