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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.commands.FrameCapture;
import org.mbari.vcr4j.remote.control.commands.VideoInfo;
import org.mbari.vcr4j.remote.control.commands.VideoInfoBean;
import org.mbari.vcr4j.remote.control.commands.localization.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.Localization;
import org.mbari.vcr4j.remote.player.NoopVideoController;
import org.mbari.vcr4j.remote.player.VideoControl;
import org.mbari.vcr4j.remote.player.VideoResult;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * Sends raw JSON to a {@link VideoControl} and checks the replies against the
 * UDP Remote Protocol spec (Sharktopoda/Requirements/UDP_Remote_Protocol.md).
 */
public class SpecComplianceTest {

    private static final UUID KNOWN = UUID.fromString("29f056c7-8d18-4880-bc26-62e9012f98b3");
    private static final UUID SLOW_OPEN = UUID.fromString("22222222-8d18-4880-bc26-62e9012f98b3");
    private static final UUID LOADING_FAILS = UUID.fromString("11111111-8d18-4880-bc26-62e9012f98b3");

    private VideoControl videoControl;
    private DatagramSocket client;
    private int playerPort;
    private final AtomicBoolean lastAdvanceForward = new AtomicBoolean(true);
    private final CountDownLatch openMayFinish = new CountDownLatch(1);

    @Before
    public void setUp() throws Exception {
        playerPort = freePort();
        videoControl = new VideoControl.Builder()
                .port(playerPort)
                .videoController(new StubController())
                .build()
                .orElseThrow();
        client = new DatagramSocket();
        client.setSoTimeout(2000);
    }

    @After
    public void tearDown() {
        client.close();
        videoControl.close();
    }

    // ---- failure handling ------------------------------------------------------------

    @Test
    public void unparseableMessageIsInvalidMessage() throws Exception {
        var r = request("{not json");
        assertEquals("unknown", r.get("response").getAsString());
        assertEquals("failed", r.get("status").getAsString());
        assertEquals("Invalid message", r.get("cause").getAsString());
    }

    @Test
    public void invalidMessageReportsTheCommandWhenItCanBeDetermined() throws Exception {
        var r = request("{\"command\":\"play\",\"uuid\":\"not-a-uuid\"}");
        assertEquals("play", r.get("response").getAsString());
        assertEquals("failed", r.get("status").getAsString());
        assertEquals("Invalid message", r.get("cause").getAsString());
    }

    @Test
    public void missingUuidIsInvalidMessage() throws Exception {
        var r = request("{\"command\":\"pause\"}");
        assertEquals("pause", r.get("response").getAsString());
        assertEquals("Invalid message", r.get("cause").getAsString());
    }

    @Test
    public void missingFieldValueIsInvalidMessage() throws Exception {
        var r = request("{\"command\":\"seek elapsed time\",\"uuid\":\"" + KNOWN + "\"}");
        assertEquals("seek elapsed time", r.get("response").getAsString());
        assertEquals("Invalid message", r.get("cause").getAsString());
    }

    @Test
    public void messageWithoutCommandIsInvalidMessage() throws Exception {
        var r = request("{\"uuid\":\"" + KNOWN + "\"}");
        assertEquals("unknown", r.get("response").getAsString());
        assertEquals("Invalid message", r.get("cause").getAsString());
    }

    @Test
    public void unknownCommandIsReportedAsUnknown() throws Exception {
        var r = request("{\"command\":\"dance\"}");
        assertEquals("unknown", r.get("response").getAsString());
        assertEquals("failed", r.get("status").getAsString());
        assertEquals("unknown command: dance", r.get("cause").getAsString());
    }

    @Test
    public void largeMessageAfterSmallMessageIsNotTruncated() throws Exception {
        request("{\"command\":\"ping\"}");
        var concept = "x".repeat(Localization.MAX_CONCEPT_LENGTH);
        var locs = java.util.stream.IntStream.range(0, 10)
                .mapToObj(i -> new Localization(UUID.randomUUID(), concept, 1000L * i, null, 1, 2, 3, 4, null))
                .toList();
        var json = RVideoIO.GSON.toJson(new AddLocalizationsCmd.Request(KNOWN, locs));
        assertTrue(json.getBytes(StandardCharsets.UTF_8).length > 3000);
        var r = request(json);
        assertEquals("add localizations", r.get("response").getAsString());
        assertEquals("ok", r.get("status").getAsString());
    }

    // ---- commands --------------------------------------------------------------------

    @Test
    public void closeIsIdempotentAndAlwaysOk() throws Exception {
        var r = request("{\"command\":\"close\",\"uuid\":\"" + UUID.randomUUID() + "\"}");
        assertEquals("close", r.get("response").getAsString());
        assertEquals("ok", r.get("status").getAsString());
    }

    @Test
    public void showUnknownVideoFailsWithCause() throws Exception {
        var r = request("{\"command\":\"show\",\"uuid\":\"" + UUID.randomUUID() + "\"}");
        assertEquals("failed", r.get("status").getAsString());
        assertEquals("No video for uuid", r.get("cause").getAsString());
    }

    @Test
    public void playerStateUsesOkStatusAndStateField() throws Exception {
        var r = request("{\"command\":\"request player state\",\"uuid\":\"" + KNOWN + "\"}");
        assertEquals("request player state", r.get("response").getAsString());
        assertEquals("ok", r.get("status").getAsString());
        assertEquals("playing", r.get("state").getAsString());
        assertEquals(1.0, r.get("rate").getAsDouble(), 0.0001);
        assertEquals(42000L, r.get("elapsedTimeMillis").getAsLong());
    }

    @Test
    public void playerStateForUnknownVideoFails() throws Exception {
        var r = request("{\"command\":\"request player state\",\"uuid\":\"" + UUID.randomUUID() + "\"}");
        assertEquals("failed", r.get("status").getAsString());
        assertEquals("No video for uuid", r.get("cause").getAsString());
        assertFalse(r.has("state"));
    }

    @Test
    public void elapsedTimeForUnknownVideoFailsWithCause() throws Exception {
        var r = request("{\"command\":\"request elapsed time\",\"uuid\":\"" + UUID.randomUUID() + "\"}");
        assertEquals("failed", r.get("status").getAsString());
        assertEquals("No video for uuid", r.get("cause").getAsString());
    }

    @Test
    public void requestInformationIncludesIsKey() throws Exception {
        var r = request("{\"command\":\"request information\"}");
        assertEquals("ok", r.get("status").getAsString());
        assertTrue(r.get("isKey").getAsBoolean());
        assertEquals(KNOWN.toString(), r.get("uuid").getAsString());
    }

    @Test
    public void seekCausePassesThrough() throws Exception {
        var r = request("{\"command\":\"seek elapsed time\",\"uuid\":\"" + KNOWN + "\",\"elapsedTimeMillis\":-5}");
        assertEquals("failed", r.get("status").getAsString());
        assertEquals("elapsedTimeMillis before start", r.get("cause").getAsString());
    }

    @Test
    public void frameAdvanceHonorsDirection() throws Exception {
        var r = request("{\"command\":\"frame advance\",\"uuid\":\"" + KNOWN + "\",\"direction\":-1}");
        assertEquals("ok", r.get("status").getAsString());
        assertFalse(lastAdvanceForward.get());

        r = request("{\"command\":\"frame advance\",\"uuid\":\"" + KNOWN + "\",\"direction\":1}");
        assertEquals("ok", r.get("status").getAsString());
        assertTrue(lastAdvanceForward.get());
    }

    @Test
    public void frameAdvanceReportsCauseWhenDirectionNotSupported() throws Exception {
        var r = request("{\"command\":\"frame advance\",\"uuid\":\"" + LOADING_FAILS + "\",\"direction\":-1}");
        assertEquals("failed", r.get("status").getAsString());
        assertEquals("Cannot advance video in that direction", r.get("cause").getAsString());
    }

    // ---- frame capture ---------------------------------------------------------------

    @Test
    public void frameCaptureFailsIfImageExists() throws Exception {
        var file = Files.createTempFile("vcr4j-capture", ".png");
        try {
            var r = request(frameCapture(KNOWN, file.toString()));
            assertEquals("frame capture", r.get("response").getAsString());
            assertEquals("failed", r.get("status").getAsString());
            assertEquals("Image exists at location", r.get("cause").getAsString());
        }
        finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    public void frameCaptureFailsIfLocationNotWritable() throws Exception {
        var r = request(frameCapture(KNOWN, "/this/directory/does/not/exist/image.png"));
        assertEquals("failed", r.get("status").getAsString());
        assertEquals("Image location not writable", r.get("cause").getAsString());
    }

    @Test
    public void frameCaptureFailsForUnknownVideo() throws Exception {
        var r = request(frameCapture(UUID.randomUUID(), "/tmp/vcr4j-never-written.png"));
        assertEquals("failed", r.get("status").getAsString());
        assertEquals("No video for uuid", r.get("cause").getAsString());
    }

    @Test
    public void frameCaptureDoneFailureIncludesCause() throws Exception {
        var remote = connectRemote();
        var path = Path.of(System.getProperty("java.io.tmpdir"), "vcr4j-" + UUID.randomUUID() + ".png");
        var r = request(frameCapture(KNOWN, path.toString()));
        assertEquals("ok", r.get("status").getAsString());

        var done = receive(remote);
        assertEquals("frame capture done", done.get("command").getAsString());
        assertEquals("failed", done.get("status").getAsString());
        assertEquals("capture exploded", done.get("cause").getAsString());
        assertEquals(KNOWN.toString(), done.get("uuid").getAsString());
        remote.close();
    }

    // ---- open ------------------------------------------------------------------------

    @Test
    public void openWithMalformedUrlFailsSynchronously() throws Exception {
        var r = request("{\"command\":\"open\",\"uuid\":\"" + UUID.randomUUID() + "\",\"url\":\"not a url\"}");
        assertEquals("open", r.get("response").getAsString());
        assertEquals("failed", r.get("status").getAsString());
        assertEquals("Malformed URL", r.get("cause").getAsString());
    }

    @Test
    public void openRespondsOkThenSendsOpenDone() throws Exception {
        var remote = connectRemote();
        var uuid = UUID.randomUUID();
        var r = request("{\"command\":\"open\",\"uuid\":\"" + uuid + "\",\"url\":\"http://example.org/a.mp4\"}");
        assertEquals("ok", r.get("status").getAsString());

        var done = receive(remote);
        assertEquals("open done", done.get("command").getAsString());
        assertEquals("ok", done.get("status").getAsString());
        assertEquals(uuid.toString(), done.get("uuid").getAsString());
        remote.close();
    }

    @Test
    public void openDoneIsNotSentUntilTheVideoIsReady() throws Exception {
        var remote = connectRemote();
        try {
            var r = request("{\"command\":\"open\",\"uuid\":\"" + SLOW_OPEN + "\",\"url\":\"http://example.org/a.mp4\"}");
            assertEquals("ok", r.get("status").getAsString()); // acknowledged immediately

            // The video is still loading: open() hasn't returned, so there must be no 'open done'
            remote.setSoTimeout(700);
            try {
                var early = receive(remote);
                fail("'open done' was sent before the video was ready: " + early);
            }
            catch (AssertionError expected) {
                assertTrue(expected.getMessage(), expected.getMessage().contains("No UDP message"));
            }

            // Once the video is ready, 'open done' follows
            remote.setSoTimeout(3000);
            openMayFinish.countDown();
            var done = receive(remote);
            assertEquals("open done", done.get("command").getAsString());
            assertEquals("ok", done.get("status").getAsString());
            assertEquals(SLOW_OPEN.toString(), done.get("uuid").getAsString());
        }
        finally {
            openMayFinish.countDown();
            remote.close();
        }
    }

    @Test
    public void failedOpenIsReportedByOpenDoneNotByTheOpenResponse() throws Exception {
        var remote = connectRemote();
        var r = request("{\"command\":\"open\",\"uuid\":\"" + LOADING_FAILS + "\",\"url\":\"http://example.org/a.mp4\"}");
        assertEquals("ok", r.get("status").getAsString());

        var done = receive(remote);
        assertEquals("open done", done.get("command").getAsString());
        assertEquals("failed", done.get("status").getAsString());
        assertEquals("Unable to open video", done.get("cause").getAsString());
        remote.close();
    }

    // ---- outgoing messages -------------------------------------------------------------

    @Test
    public void outgoingMessagesAreMinified() {
        var loc = new Localization(UUID.randomUUID(), "concept", 1L, null, 1, 2, 3, 4, null);
        var json = RVideoIO.GSON.toJson(new AddLocalizationsCmd.Request(KNOWN, List.of(loc)));
        assertFalse(json, json.contains("\n"));
    }

    @Test
    public void conceptOfMaxLengthIsAccepted() {
        var loc = new Localization(UUID.randomUUID(), "x".repeat(256), 1L, null, 1, 2, 3, 4, null);
        assertEquals(256, loc.getConcept().length());
    }

    @Test(expected = IllegalArgumentException.class)
    public void conceptLongerThanMaxIsRejectedByConstructor() {
        new Localization(UUID.randomUUID(), "x".repeat(257), 1L, null, 1, 2, 3, 4, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void conceptLongerThanMaxIsRejectedBySetter() {
        new Localization().setConcept("x".repeat(257));
    }

    @Test
    public void openIsAcknowledgedWithinTheDefaultTimeout() throws Exception {
        // 'open' used to wait up to 20s for a reply. The protocol acknowledges it immediately,
        // so a player that never answers must fail fast like any other command.
        try (var silent = new DatagramSocket()) {
            var io = new RVideoIO(UUID.randomUUID(), "localhost", silent.getLocalPort());
            var errors = io.getErrorObservable().test();
            var start = System.nanoTime();
            io.send(new org.mbari.vcr4j.remote.control.commands.OpenCmd(UUID.randomUUID(),
                    new URL("http://example.org/a.mp4")));
            var elapsedMillis = (System.nanoTime() - start) / 1_000_000;
            errors.assertValueCount(1);
            assertTrue("open took " + elapsedMillis + "ms", elapsedMillis < RVideoIO.MAX_TIMEOUT_MILLIS / 2);
            io.close();
        }
    }

    @Test
    public void localizationColorIsOmittedWhenUnspecified() {
        var loc = new Localization(UUID.randomUUID(), "concept", 1L, null, 1, 2, 3, 4, null);
        assertFalse(RVideoIO.GSON.toJson(loc).contains("color"));
    }

    // ---- helpers -----------------------------------------------------------------------

    private static String frameCapture(UUID uuid, String location) {
        return "{\"command\":\"frame capture\",\"uuid\":\"" + uuid + "\",\"imageLocation\":\""
                + location + "\",\"imageReferenceUuid\":\"" + UUID.randomUUID() + "\"}";
    }

    /** Stands in for the remote app: tells the player to connect to a socket we can read from. */
    private DatagramSocket connectRemote() throws Exception {
        var remote = new DatagramSocket();
        remote.setSoTimeout(3000);
        var r = request("{\"command\":\"connect\",\"port\":" + remote.getLocalPort() + "}");
        assertEquals("ok", r.get("status").getAsString());
        return remote;
    }

    private JsonObject request(String json) throws IOException {
        var bytes = json.getBytes(StandardCharsets.UTF_8);
        client.send(new DatagramPacket(bytes, bytes.length, InetAddress.getLoopbackAddress(), playerPort));
        return receive(client);
    }

    private static JsonObject receive(DatagramSocket socket) throws IOException {
        var buf = new byte[8192];
        var packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        }
        catch (SocketTimeoutException e) {
            fail("No UDP message received before timeout");
        }
        var msg = new String(buf, 0, packet.getLength(), StandardCharsets.UTF_8);
        return JsonParser.parseString(msg).getAsJsonObject();
    }

    private static int freePort() throws IOException {
        try (var s = new DatagramSocket(0)) {
            return s.getLocalPort();
        }
    }

    private class StubController extends NoopVideoController {

        @Override
        public boolean hasVideo(UUID videoUuid) {
            return KNOWN.equals(videoUuid) || LOADING_FAILS.equals(videoUuid);
        }

        // Like a real implementation, blocks until the "video" is ready: see VideoController#open
        @Override
        public boolean open(UUID videoUuid, URL url) {
            if (SLOW_OPEN.equals(videoUuid)) {
                try {
                    return openMayFinish.await(10, TimeUnit.SECONDS);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return !LOADING_FAILS.equals(videoUuid);
        }

        @Override
        public Optional<Double> requestRate(UUID videoUuid) {
            return KNOWN.equals(videoUuid) ? Optional.of(1.0) : Optional.empty();
        }

        @Override
        public Optional<Duration> requestElapsedTime(UUID videoUuid) {
            return KNOWN.equals(videoUuid) ? Optional.of(Duration.ofMillis(42000)) : Optional.empty();
        }

        @Override
        public Optional<VideoInfo> requestVideoInfo() {
            try {
                return Optional.of(new VideoInfoBean(KNOWN, new URL("http://example/x.mp4"), 1000L, 30.0, true));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public VideoResult seekVideo(UUID videoUuid, Duration elapsedTime) {
            return elapsedTime.isNegative() ? VideoResult.failed(VideoResult.SEEK_BEFORE_START)
                    : VideoResult.success();
        }

        @Override
        public boolean frameAdvance(UUID videoUuid) {
            lastAdvanceForward.set(true);
            return true;
        }

        @Override
        public VideoResult advanceFrame(UUID videoUuid, boolean forward) {
            if (LOADING_FAILS.equals(videoUuid)) {
                return super.advanceFrame(videoUuid, forward); // default: reverse unsupported
            }
            lastAdvanceForward.set(forward);
            return VideoResult.success();
        }

        @Override
        public CompletableFuture<FrameCapture> framecapture(UUID videoUuid,
                                                            UUID imageReferenceUuid,
                                                            Path saveLocation) {
            return CompletableFuture.failedFuture(new IllegalStateException("capture exploded"));
        }
    }
}
