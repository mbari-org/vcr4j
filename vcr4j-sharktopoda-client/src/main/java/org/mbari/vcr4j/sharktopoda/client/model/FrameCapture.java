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
package org.mbari.vcr4j.sharktopoda.client.model;

import java.nio.file.Path;
import java.time.Duration;

/**
 * @author Brian Schlining
 * @since 2019-12-05T13:52:00
 */
public class FrameCapture {
    Path saveLocation;
    Duration snapTime;

    public FrameCapture(Path saveLocation, Duration snapTime) {
        this.saveLocation = saveLocation;
        this.snapTime = snapTime;
    }

    public FrameCapture() {
    }

    public Path getSaveLocation() {
        return saveLocation;
    }

    public Duration getSnapTime() {
        return snapTime;
    }
}
