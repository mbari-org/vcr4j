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

/**
 *
 * @author Brian Schlining
 * @since 2022-08-08
 */
public enum RemoteCommands implements VideoCommand<Void> {

    FRAMEADVANCE("frame advance"),

    PING("ping"),

    REQUEST_ALL_VIDEO_INFOS("request all video information"),

    REQUEST_VIDEO_INFO("request video information"),

    SHOW("show"),

    CLOSE("close");


    private final String name;

    RemoteCommands(String name) {
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
