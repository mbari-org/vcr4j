/*
 * Copyright © 2008 MBARI (brian@mbari.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mbari.vcr4j.sharktopoda;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoError;

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2016-08-25T16:40:00
 */
public class SharktopodaError implements VideoError {

    private final Optional<VideoCommand<?>> videoCommand;
    private final boolean connectionError;
    private final boolean parserError;
    private final boolean unknownError;

    public SharktopodaError(boolean connectionError,
            boolean parserError,
            boolean unknownError,
            Optional<VideoCommand<?>> videoCommand) {
        this.connectionError = connectionError;
        this.parserError = parserError;
        this.unknownError = unknownError;
        this.videoCommand = videoCommand;
    }

    @Override
    public Optional<VideoCommand<?>> getVideoCommand() {
        return videoCommand;
    }

    @Override
    public boolean hasError() {
        return connectionError || parserError ||unknownError;
    }

    public boolean isConnectionError() {
        return connectionError;
    }

    public boolean isParseError() {
        return  parserError;
    }

    public boolean isUnknownError() {
        return unknownError;
    }


}
