package org.mbari.vcr4j.udp;

/*-
 * #%L
 * vcr4j-udp
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
import org.mbari.vcr4j.VideoError;

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2016-02-04T13:27:00
 */
public class UDPError implements VideoError {

    private final Optional<VideoCommand<?>> videoCommand;
    private final boolean connectionError;
    private final boolean parserError;

    public UDPError(boolean connectionError, boolean parserError, Optional<VideoCommand<?>> videoCommand) {
        this.connectionError = connectionError;
        this.parserError = parserError;
        this.videoCommand = videoCommand;
    }

    @Override
    public Optional<VideoCommand<?>> getVideoCommand() {
        return videoCommand;
    }

    @Override
    public boolean hasError() {
        return connectionError || parserError;
    }

    public boolean isConnectionError() {
        return connectionError;
    }

    public boolean isParseError() {
        return  parserError;
    }


}
