package org.mbari.vcr4j.sharktopoda.client.udp;

import org.mbari.vcr4j.sharktopoda.client.ClientController;
import org.mbari.vcr4j.sharktopoda.client.model.FrameCapture;
import org.mbari.vcr4j.sharktopoda.client.model.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Brian Schlining
 * @since 2019-12-05T14:39:00
 */
public class MockClientController implements ClientController {

    final List<Video> videoUuids = new ArrayList<>();
    private volatile Video focusedVideo;
    private final Logger log = LoggerFactory.getLogger(getClass());

    private boolean contains(UUID uuid) {
        return videoUuids.stream()
                .anyMatch(v -> v.getUuid().equals(uuid));
    }

    private void remove(UUID uuid) {
        log.info("Removing " + uuid);
        Optional<Video> first = videoUuids.stream()
                .filter(v -> v.getUuid().equals(uuid))
                .findFirst();
        first.ifPresent(videoUuids::remove);
        if (focusedVideo.equals(uuid)) {
            focusedVideo = null;
        }
    }

    @Override
    public boolean open(UUID videoUuid, URL url) {

        log.info("Opening " + videoUuid);
        if (contains(videoUuid)) {
            return show(videoUuid);
        }
        else {
            Video video = new Video(videoUuid, url);
            videoUuids.add(video);
            focusedVideo = video;
            return true;
        }
    }

    @Override
    public boolean close(UUID videoUuid) {
        log.info("Closing " + videoUuid);
        if (contains(videoUuid)) {
            remove(videoUuid);
            if (focusedVideo.getUuid().equals(videoUuid)) {
                focusedVideo = null;
            }
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean show(UUID videoUuid) {
        log.info("Showing " + videoUuid);
        Optional<Video> first = videoUuids.stream()
                .filter(v -> v.getUuid().equals(videoUuid))
                .findFirst();
        if (first.isPresent()) {
            focusedVideo = first.get();
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public Optional<Video> requestVideoInfo() {
        return Optional.ofNullable(focusedVideo);
    }

    @Override
    public List<Video> requestAllVideoInfos() {
        return new ArrayList<>(videoUuids);
    }

    @Override
    public boolean play(UUID videoUuid, double rate) {
        log.info("Playing " + videoUuid + " at " + rate);
        return false;
    }

    @Override
    public boolean pause(UUID videoUuid) {
        log.info("Pausing " + videoUuid);
        return false;
    }

    @Override
    public Optional<Double> requestRate(UUID videoUuid) {
        log.info("Requesting rate from " + videoUuid);
        return Optional.of(0.0);
    }

    @Override
    public Optional<Duration> requestElapsedTime(UUID videoUuid) {
        log.info("Requesting elapsed time from " + videoUuid);
        return Optional.of(Duration.ZERO);
    }

    @Override
    public boolean seekElapsedTime(UUID videoUuid, Duration elapsedTime) {
        log.info("Seeking to " + elapsedTime + " in " + videoUuid);
        return false;
    }

    @Override
    public boolean frameAdvance(UUID videoUuid) {
        log.info("Frameadvance for " + videoUuid);
        return false;
    }

    @Override
    public CompletableFuture<FrameCapture> framecapture(UUID videoUuid, Path saveLocation) {
        log.info("Framecapture on " + videoUuid);
        CompletableFuture<FrameCapture> f = new CompletableFuture<>();
        f.completeExceptionally(new RuntimeException("Not implemented"));
        return f;
    }
}
