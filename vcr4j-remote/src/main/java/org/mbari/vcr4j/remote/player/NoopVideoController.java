package org.mbari.vcr4j.remote.player;

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
