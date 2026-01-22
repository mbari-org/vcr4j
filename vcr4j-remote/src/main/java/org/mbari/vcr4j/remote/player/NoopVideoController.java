package org.mbari.vcr4j.remote.player;

/*-
 * #%L
 * vcr4j-remote
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

import org.mbari.vcr4j.remote.control.commands.FrameCapture;
import org.mbari.vcr4j.remote.control.commands.VideoInfo;

import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Video controller that does nothing.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class NoopVideoController implements VideoController {

    @Override
    public boolean open(UUID videoUuid, URL url) {
        return false;
    }

    @Override
    public boolean close(UUID videoUuid) {
        return false;
    }

    @Override
    public boolean show(UUID videoUuid) {
        return false;
    }

    @Override
    public Optional<VideoInfo> requestVideoInfo() {
        return Optional.empty();
    }

    @Override
    public List<VideoInfo> requestAllVideoInfos() {
        return Collections.emptyList();
    }

    @Override
    public boolean play(UUID videoUuid, double rate) {
        return false;
    }

    @Override
    public boolean pause(UUID videoUuid) {
        return false;
    }

    @Override
    public Optional<Double> requestRate(UUID videoUuid) {
        return Optional.empty();
    }

    @Override
    public Optional<Duration> requestElapsedTime(UUID videoUuid) {
        return Optional.empty();
    }

    @Override
    public boolean seekElapsedTime(UUID videoUuid, Duration elapsedTime) {
        return false;
    }

    @Override
    public boolean frameAdvance(UUID videoUuid) {
        return false;
    }

    @Override
    public CompletableFuture<FrameCapture> framecapture(UUID videoUuid, UUID imageReferenceUuid, Path saveLocation) {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("This is not implemented"));
    }
}
