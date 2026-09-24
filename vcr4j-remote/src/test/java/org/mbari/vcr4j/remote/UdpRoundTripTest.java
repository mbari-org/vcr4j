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
package org.mbari.vcr4j.remote;

import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mbari.vcr4j.commands.RemoteCommands;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.remote.control.RState;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.FrameCapture;
import org.mbari.vcr4j.remote.control.commands.VideoInfo;
import org.mbari.vcr4j.remote.control.commands.VideoInfoBean;
import org.mbari.vcr4j.remote.player.NoopVideoController;
import org.mbari.vcr4j.remote.player.VideoControl;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

/**
 * End-to-end UDP request/response tests. A {@link VideoControl} runs on one ephemeral
 * port with a stub {@link VideoController} that returns known values; a
 * {@link RemoteControl} runs on another port and issues commands. The tests assert that
 * responses flow back through the state/index/videoInfo observables — exercising the full
 * loop: RxJava dispatch → JSON encode → UDP send → parse → handler dispatch → JSON encode
 * → UDP reply → parse → observable emit.
 */
public class UdpRoundTripTest {

    private static final double EXPECTED_RATE = 1.0;
    private static final long EXPECTED_ELAPSED_MILLIS = 42_000L;

    private VideoControl videoControl;
    private RemoteControl remoteControl;

    @Before
    public void setUp() throws Exception {
        int playerPort = freePort();
        int controllerPort = freePort();

        videoControl = new VideoControl.Builder()
                .port(playerPort)
                .videoController(new StubController())
                .build()
                .orElseThrow(() -> new IllegalStateException("Failed to build VideoControl"));

        remoteControl = new RemoteControl.Builder(UUID.randomUUID())
                .port(controllerPort)
                .remotePort(playerPort)
                .build()
                .orElseThrow(() -> new IllegalStateException("Failed to build RemoteControl"));

        // Give the ConnectCmd time to establish the reverse channel.
        Thread.sleep(200);
    }

    @After
    public void tearDown() {
        if (remoteControl != null) {
            remoteControl.close();
        }
        if (videoControl != null) {
            videoControl.close();
        }
    }

    @Test
    public void requestStatusRoundTripsToStateObservable() throws InterruptedException {
        TestObserver<RState> obs = remoteControl.getVideoIO().getStateObservable().test();

        remoteControl.getVideoIO().send(VideoCommands.REQUEST_STATUS);
        Thread.sleep(500);

        obs.assertValueCount(1);
        assertEquals(RState.State.PLAYING, obs.values().get(0).getState());
    }

    @Test
    public void requestElapsedTimeRoundTripsToIndexObservable() throws InterruptedException {
        var obs = remoteControl.getVideoIO().getIndexObservable().test();

        remoteControl.getVideoIO().send(VideoCommands.REQUEST_ELAPSED_TIME);
        Thread.sleep(500);

        obs.assertValueCount(1);
        var elapsed = obs.values().get(0).getElapsedTime().orElseThrow();
        assertEquals(EXPECTED_ELAPSED_MILLIS, elapsed.toMillis());
    }

    @Test
    public void requestAllVideoInfosRoundTripsToVideoInfoObservable() throws InterruptedException {
        var obs = remoteControl.getVideoIO().getVideoInfoObservable().test();

        remoteControl.getVideoIO().send(RemoteCommands.REQUEST_ALL_VIDEO_INFOS);
        Thread.sleep(500);

        obs.assertValueCount(1);
        var videos = obs.values().get(0);
        assertEquals(1, videos.size());
        assertEquals(StubController.KNOWN_UUID, videos.get(0).getUuid());
    }

    private static int freePort() {
        try (var s = new DatagramSocket(0)) {
            return s.getLocalPort();
        }
        catch (SocketException e) {
            throw new RuntimeException("Could not allocate a free UDP port", e);
        }
    }

    /** Returns known values so the tests can assert against fixed expectations. */
    private static class StubController extends NoopVideoController {

        static final UUID KNOWN_UUID = UUID.fromString("29f056c7-8d18-4880-bc26-62e9012f98b3");

        @Override
        public Optional<Double> requestRate(UUID videoUuid) {
            return Optional.of(EXPECTED_RATE);
        }

        @Override
        public Optional<Duration> requestElapsedTime(UUID videoUuid) {
            return Optional.of(Duration.ofMillis(EXPECTED_ELAPSED_MILLIS));
        }

        @Override
        public List<VideoInfo> requestAllVideoInfos() {
            try {
                var vi = new VideoInfoBean(KNOWN_UUID, new URL("http://example/x.mp4"),
                        1000L, 30.0, false);
                return List.of(vi);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public CompletableFuture<FrameCapture> framecapture(UUID videoUuid,
                                                            UUID imageReferenceUuid,
                                                            Path saveLocation) {
            return CompletableFuture.failedFuture(new UnsupportedOperationException());
        }
    }
}
