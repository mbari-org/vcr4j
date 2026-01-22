package org.mbari.vcr4j;

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

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2016-03-25T10:59:00
 */
public class SimpleVideoError implements VideoError {

    private final Optional<VideoCommand<?>> videoCommand;
    private final boolean error;

    public SimpleVideoError(boolean error, VideoCommand<?> videoCommand) {
        this.error = error;
        this.videoCommand = Optional.ofNullable(videoCommand);
    }

    public SimpleVideoError(boolean error) {
        this(error, null);
    }

    @Override
    public Optional<VideoCommand<?>> getVideoCommand() {
        return videoCommand;
    }

    @Override
    public boolean hasError() {
        return error;
    }
}
