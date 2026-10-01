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
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.commands.RemoteCommands;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.remote.control.CommandResponse;
import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.CloseCmd;
import org.mbari.vcr4j.remote.control.commands.ConnectCmd;
import org.mbari.vcr4j.remote.control.commands.FrameAdvanceCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureDoneCmd;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.OpenDoneCmd;
import org.mbari.vcr4j.remote.control.commands.PlayCmd;
import org.mbari.vcr4j.remote.control.commands.RResponse;
import org.mbari.vcr4j.remote.control.commands.RequestAllVideoInfosCmd;
import org.mbari.vcr4j.remote.control.commands.RequestElapsedTimeCmd;
import org.mbari.vcr4j.remote.control.commands.RequestPlayerStateCmd;
import org.mbari.vcr4j.remote.control.commands.RequestVideoInfoCmd;
import org.mbari.vcr4j.remote.control.commands.localization.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.ClearLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.Localization;
import org.mbari.vcr4j.remote.control.commands.localization.RemoveLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.SelectLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.UpdateLocalizationsCmd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * Integration tests that drive a live, spec-compliant video player (Sharktopoda) through
 * {@link RemoteControl}/{@link RVideoIO}, to verify that vcr4j-remote speaks the
 * UDP Remote Protocol correctly. They are NOT run by a normal build (the class name does
 * not match surefire's defaults, and they open windows in the player). Run them with:
 *
 * <pre>
 *   mvn -pl vcr4j-remote test -Dtest=SharktopodaIT \
 *       -Dsharktopoda.video=/path/to/video.mp4
 * </pre>
 *
 * <ul>
 *   <li><code>sharktopoda.port</code> (or env SHARKTOPODA_PORT) - the player's UDP port. Default 8800.</li>
 *   <li><code>sharktopoda.host</code> (or env SHARKTOPODA_HOST) - the player's host. Default localhost.
 *       Frame capture writes to the local temp directory, so the player should be on this machine.</li>
 *   <li><code>sharktopoda.video</code> (or env SHARKTOPODA_VIDEO) - a URL or file path of a video the
 *       player can open. Tests that need a video are skipped when this is not set.</li>
 * </ul>
 *
 * All tests are skipped if the player doesn't answer a ping.
 */
public class SharktopodaIT {

    private static final String HOST = setting("sharktopoda.host", "SHARKTOPODA_HOST", "localhost");
    private static final int PORT = Integer.parseInt(setting("sharktopoda.port", "SHARKTOPODA_PORT", "8800"));
    private static final String VIDEO = setting("sharktopoda.video", "SHARKTOPODA_VIDEO", null);

    private static final String NO_VIDEO = "No video for uuid";

    private static final UUID VIDEO_UUID = UUID.randomUUID();
    private static final BlockingQueue<OpenDoneCmd.Request> openDones = new LinkedBlockingQueue<>();
    private static final BlockingQueue<FrameCaptureDoneCmd> captureDones = new LinkedBlockingQueue<>();

    private static RemoteControl remote;
    private static int controllerPort;
    private static RVideoIO io;                  // talks to the player about VIDEO_UUID
    private static RVideoIO noVideoIo;           // addresses a uuid that is never opened
    private static boolean videoOpened = false;
    private static URL videoUrl;
    private static long durationMillis;

    @BeforeClass
    public static void connect() throws Exception {
        Assume.assumeTrue("Sharktopoda is not responding at " + HOST + ":" + PORT, isPlayerUp());
        controllerPort = freePort();
        remote = new RemoteControl.Builder(VIDEO_UUID)
                .remoteHost(HOST)
                .remotePort(PORT)
                .port(controllerPort)
                .whenOpenIsDone(openDones::add)
                .whenFrameCaptureIsDone(captureDones::add)
                .build()
                .orElseThrow(() -> new IllegalStateException("Unable to build RemoteControl"));
        io = remote.getVideoIO();
        noVideoIo = new RVideoIO(UUID.randomUUID(), HOST, PORT);
        if (VIDEO != null) {
            videoUrl = VIDEO.contains(":/") ? new URI(VIDEO).toURL() : Path.of(VIDEO).toUri().toURL();
        }
    }

    @AfterClass
    public static void disconnect() {
        if (io != null && videoOpened) {
            io.send(new CloseCmd(VIDEO_UUID));
        }
        if (noVideoIo != null) {
            noVideoIo.close();
        }
        if (remote != null) {
            remote.close();
        }
    }

    // ---- no video required -----------------------------------------------------------

    @Test
    public void ping() {
        var r = only(send(io, RemoteCommands.PING));
        assertOk(r);
        assertEquals("ping", r.response().getResponse());
    }

    @Test
    public void connectIsAcknowledgedAndThePlayerPingsTheRemote() throws Exception {
        try (var listener = new DatagramSocket()) {
            listener.setSoTimeout(5000);
            try {
                var reply = raw("{\"command\":\"connect\",\"port\":" + listener.getLocalPort() + "}");
                assertEquals("connect", reply.get("response").getAsString());
                assertEquals("ok", reply.get("status").getAsString());

                var ping = receive(listener);
                assertEquals("ping", ping.json.get("command").getAsString());
                send(listener, "{\"response\":\"ping\",\"status\":\"ok\"}", ping);
            }
            finally {
                // Point the player back at our RemoteControl
                assertOk(only(send(io, new ConnectCmd(controllerPort, "localhost"))));
            }
        }
    }

    @Test
    public void unparseableMessageIsReportedAsInvalid() throws Exception {
        var r = raw("{this is not json");
        assertEquals("unknown", r.get("response").getAsString());
        assertEquals("failed", r.get("status").getAsString());
        assertEquals("Invalid message", r.get("cause").getAsString());
    }

    @Test
    public void missingUuidIsReportedAsInvalid() throws Exception {
        var r = raw("{\"command\":\"play\"}");
        assertEquals("play", r.get("response").getAsString());
        assertEquals("failed", r.get("status").getAsString());
        assertEquals("Invalid message", r.get("cause").getAsString());
    }

    @Test
    public void mistypedFieldIsReportedAsInvalid() throws Exception {
        var r = raw("{\"command\":\"seek elapsed time\",\"uuid\":\"" + UUID.randomUUID()
                + "\",\"elapsedTimeMillis\":\"soon\"}");
        assertEquals("seek elapsed time", r.get("response").getAsString());
        assertEquals("failed", r.get("status").getAsString());
        assertEquals("Invalid message", r.get("cause").getAsString());
    }

    @Test
    public void garbledUuidFails() throws Exception {
        // The spec allows either "Invalid message" or "No video for uuid" for this
        var r = raw("{\"command\":\"play\",\"uuid\":\"not-a-uuid\"}");
        assertEquals("play", r.get("response").getAsString());
        assertEquals("failed", r.get("status").getAsString());
    }

    @Test
    public void unknownCommandIsReported() throws Exception {
        var r = raw("{\"command\":\"dance\"}");
        assertEquals("unknown", r.get("response").getAsString());
        assertEquals("failed", r.get("status").getAsString());
        assertEquals("unknown command: dance", r.get("cause").getAsString());
    }

    @Test
    public void openWithMalformedUrlFails() throws Exception {
        var r = raw("{\"command\":\"open\",\"uuid\":\"" + UUID.randomUUID() + "\",\"url\":\"http://[bad\"}");
        assertEquals("open", r.get("response").getAsString());
        assertEquals("failed", r.get("status").getAsString());
        assertEquals("Malformed URL", r.get("cause").getAsString());
    }

    @Test
    public void requestAllInformationIsOk() {
        var r = only(send(io, RemoteCommands.REQUEST_ALL_VIDEO_INFOS));
        assertOk(r);
        assertNotNull(((RequestAllVideoInfosCmd.Response) r.response()).getVideos());
    }

    @Test
    public void requestInformationFailsWhenNothingIsOpen() {
        closeSharedVideo(); // other tests share it, and reopen it on demand
        var all = (RequestAllVideoInfosCmd.Response) only(send(io, RemoteCommands.REQUEST_ALL_VIDEO_INFOS)).response();
        Assume.assumeTrue("Other videos are open in the player", all.getVideos().isEmpty());
        var r = only(send(io, RemoteCommands.REQUEST_VIDEO_INFO));
        assertFailed(r, "No open videos");
    }

    @Test
    public void commandsForAVideoThatIsNotOpenFailWithCause() {
        var uuid = noVideoIo.getUuid();
        var commands = List.<VideoCommand<?>>of(
                RemoteCommands.SHOW,
                VideoCommands.PAUSE,
                VideoCommands.REQUEST_STATUS,
                VideoCommands.REQUEST_ELAPSED_TIME,
                new PlayCmd(uuid, 2.0),
                new SeekElapsedTimeCmd(Duration.ofSeconds(1)),
                new FrameAdvanceCmd(uuid, true),
                new FrameCaptureCmd(uuid, UUID.randomUUID(),
                        Path.of(System.getProperty("java.io.tmpdir"), "vcr4j-" + UUID.randomUUID() + ".png").toString()),
                new AddLocalizationsCmd(uuid, newLocalizations(1)),
                new UpdateLocalizationsCmd(uuid, newLocalizations(1)),
                new RemoveLocalizationsCmd(uuid, List.of(UUID.randomUUID())),
                new SelectLocalizationsCmd(uuid, List.of(UUID.randomUUID())),
                new ClearLocalizationsCmd(new ClearLocalizationsCmd.Request(uuid)));
        for (var cmd : commands) {
            var responses = send(noVideoIo, cmd);
            assertFalse(cmd + " got no response", responses.isEmpty());
            assertFailed(responses.get(0), NO_VIDEO);
        }
    }

    @Test
    public void closeIsIdempotent() {
        assertOk(only(send(noVideoIo, new CloseCmd(noVideoIo.getUuid()))));
    }

    // ---- video required --------------------------------------------------------------

    @Test
    public void openedVideoIsPausedAndReportsInformation() {
        openVideo();
        // 'request information' describes the focused window
        assertOk(only(send(io, RemoteCommands.SHOW)));
        var info = (RequestVideoInfoCmd.Response) eventually("focused video is ours", () -> {
            var r = (RequestVideoInfoCmd.Response) only(send(io, RemoteCommands.REQUEST_VIDEO_INFO)).response();
            return VIDEO_UUID.equals(r.getUuid()) ? r : null;
        });
        assertTrue(info.success());
        assertEquals(VIDEO_UUID, info.getUuid());
        assertNotNull(info.getUrl());
        assertTrue(info.getDurationMillis() > 0);
        assertTrue(info.getFrameRate() > 0);
        assertNotNull("isKey should be reported", info.isKey());
    }

    @Test
    public void requestAllInformationListsTheOpenVideo() {
        openVideo();
        var r = (RequestAllVideoInfosCmd.Response) only(send(io, RemoteCommands.REQUEST_ALL_VIDEO_INFOS)).response();
        assertTrue(r.success());
        var mine = r.getVideos().stream().filter(v -> VIDEO_UUID.equals(v.getUuid())).findFirst();
        assertTrue("Open video not listed", mine.isPresent());
        assertEquals(durationMillis, mine.get().getDurationMillis().longValue());
    }

    @Test
    public void openingAnOpenVideoAgainIsOkAndSendsOpenDone() throws Exception {
        openVideo();
        openDones.clear();
        assertOk(only(send(io, new OpenCmd(VIDEO_UUID, videoUrl))));
        var done = awaitOpenDone(VIDEO_UUID, 15);
        assertTrue(done.isOk());
    }

    @Test
    public void playerStateTracksPlayAndPause() throws Exception {
        openVideo();
        resetVideo();

        var state = state();
        assertEquals("paused", state.getState());
        assertEquals(0.0, state.getRate(), 0.0001);

        assertOk(only(send(io, new PlayCmd(VIDEO_UUID))));
        state = eventuallyState("playing");
        assertEquals(1.0, state.getRate(), 0.0001);

        assertOk(only(send(io, new PlayCmd(VIDEO_UUID, 2.0))));
        state = eventuallyState("shuttling forward");
        assertEquals(2.0, state.getRate(), 0.0001);

        assertOk(only(send(io, VideoCommands.PAUSE)));
        eventuallyState("paused");
    }

    @Test
    public void playingAdvancesElapsedTime() throws Exception {
        openVideo();
        resetVideo();
        var start = elapsed();
        assertOk(only(send(io, new PlayCmd(VIDEO_UUID))));
        Thread.sleep(1500);
        assertOk(only(send(io, VideoCommands.PAUSE)));
        Thread.sleep(300); // allow the last frame to settle
        var end = elapsed();
        assertTrue("start=" + start + " end=" + end, end - start > 500);

        Thread.sleep(300);
        assertEquals("Elapsed time should be stable while paused", end, elapsed());
    }

    @Test
    public void reversePlaybackIsReportedAsShuttlingReverse() throws Exception {
        openVideo();
        resetVideo();
        seek(Math.min(10_000, durationMillis / 2));
        assertOk(only(send(io, new PlayCmd(VIDEO_UUID, -1.0))));
        var state = eventuallyState("shuttling reverse");
        assertTrue(state.getRate() < 0);
        assertOk(only(send(io, VideoCommands.PAUSE)));
    }

    @Test
    public void seekMovesToTheRequestedTime() {
        openVideo();
        resetVideo();
        var target = Math.min(5_000, durationMillis / 2);
        seek(target); // waits for the player to arrive within 250ms of the target
    }

    @Test
    public void seekOutsideTheVideoFailsWithCause() {
        openVideo();
        resetVideo();
        assertFailed(only(send(io, new SeekElapsedTimeCmd(Duration.ofMillis(-1)))), "elapsedTimeMillis before start");
        assertFailed(only(send(io, new SeekElapsedTimeCmd(Duration.ofMillis(durationMillis + 600_000)))),
                "elapsedTimeMillis past end");
    }

    @Test
    public void frameAdvanceMovesOneFrameInEitherDirection() {
        openVideo();
        resetVideo();
        seek(Math.min(5_000, durationMillis / 2));
        var start = elapsed();

        assertOk(only(send(io, new FrameAdvanceCmd(VIDEO_UUID, true))));
        var forward = eventually("advance one frame", () -> elapsed() > start ? elapsed() : null);
        assertTrue("moved more than a few frames: start=" + start + " forward=" + forward,
                forward - start < 500);

        assertOk(only(send(io, new FrameAdvanceCmd(VIDEO_UUID, false))));
        eventually("go back one frame", () -> elapsed() < forward ? Boolean.TRUE : null);
    }

    @Test
    public void showIsOk() {
        openVideo();
        assertOk(only(send(io, RemoteCommands.SHOW)));
    }

    @Test
    public void frameCaptureWritesAPngAndSendsDone() throws Exception {
        openVideo();
        resetVideo();
        seek(Math.min(5_000, durationMillis / 2));
        var at = elapsed();
        var image = Path.of(System.getProperty("java.io.tmpdir"), "vcr4j-it-" + UUID.randomUUID() + ".png");
        var imageRef = UUID.randomUUID();
        captureDones.clear();
        try {
            assertOk(only(send(io, new FrameCaptureCmd(VIDEO_UUID, imageRef, image.toString()))));
            var done = captureDones.poll(30, TimeUnit.SECONDS);
            assertNotNull("No 'frame capture done'", done);
            var request = done.getValue();
            assertEquals("frame capture done failed: " + request.getCause(), RResponse.OK, request.getStatus());
            assertEquals(VIDEO_UUID, request.getUuid());
            assertEquals(imageRef, request.getImageReferenceUuid());
            assertEquals(image.toString(), request.getImageLocation());
            assertNotNull(request.getElapsedTimeMillis());
            assertTrue("capture at " + request.getElapsedTimeMillis() + ", seeked to " + at,
                    Math.abs(request.getElapsedTimeMillis() - at) <= 250);

            assertTrue("Image not written", Files.exists(image));
            var magic = Files.readAllBytes(image);
            assertTrue(magic.length > 8);
            assertEquals((byte) 0x89, magic[0]);
            assertEquals("PNG", new String(magic, 1, 3, StandardCharsets.US_ASCII));

            // Never overwrite an existing image
            var again = only(send(io, new FrameCaptureCmd(VIDEO_UUID, UUID.randomUUID(), image.toString())));
            assertFailed(again, "Image exists at location");
        }
        finally {
            Files.deleteIfExists(image);
        }
    }

    @Test
    public void frameCaptureToAnUnwritableLocationFails() {
        openVideo();
        var r = only(send(io, new FrameCaptureCmd(VIDEO_UUID, UUID.randomUUID(),
                "/vcr4j/no/such/directory/image.png")));
        assertFailed(r, "Image location not writable");
    }

    @Test
    public void localizationLifecycle() {
        openVideo();
        resetVideo();
        var locs = newLocalizations(3);

        assertAllOk(send(io, new AddLocalizationsCmd(VIDEO_UUID, locs)));

        var moved = locs.get(0);
        moved.setConcept("Updated concept");
        moved.setX(10);
        assertAllOk(send(io, new UpdateLocalizationsCmd(VIDEO_UUID, List.of(moved))));

        // Updating a localization the player doesn't have is ignored, not an error
        var stranger = newLocalizations(1);
        assertAllOk(send(io, new UpdateLocalizationsCmd(VIDEO_UUID, stranger)));

        assertAllOk(send(io, SelectLocalizationsCmd.fromLocalizations(VIDEO_UUID, locs.subList(0, 2))));
        // Unknown uuids in a select are ignored
        assertAllOk(send(io, new SelectLocalizationsCmd(VIDEO_UUID, List.of(UUID.randomUUID()))));

        assertAllOk(send(io, RemoveLocalizationsCmd.fromLocalizations(VIDEO_UUID, locs.subList(0, 1))));
        assertAllOk(send(io, new ClearLocalizationsCmd(new ClearLocalizationsCmd.Request(VIDEO_UUID))));
    }

    @Test
    public void manyLocalizationsAreSentInMessagesThatFitTheLimit() {
        openVideo();
        resetVideo();
        var locs = IntStream.range(0, 55)
                .mapToObj(i -> new Localization(UUID.randomUUID(),
                        "Concept number " + i + " " + "x".repeat(200),
                        1000L * i, 0L, 10, 20, 300, 400, "#FFDDDD"))
                .toList();
        var responses = send(io, new AddLocalizationsCmd(VIDEO_UUID, locs));
        assertEquals("55 localizations should go out 10 per message", 6, responses.size());
        assertAllOk(responses);

        var asMessage = RVideoIO.GSON.toJson(new AddLocalizationsCmd.Request(VIDEO_UUID, locs.subList(0, 10)));
        assertTrue(asMessage.getBytes(StandardCharsets.UTF_8).length <= RVideoIO.MAX_MESSAGE_BYTES);

        assertAllOk(send(io, new ClearLocalizationsCmd(new ClearLocalizationsCmd.Request(VIDEO_UUID))));
    }

    @Test
    public void localizationCommandsSentWhileAVideoLoadsAreAccepted() throws Exception {
        requireVideo();
        var uuid = UUID.randomUUID();
        var loading = new RVideoIO(uuid, HOST, PORT);
        try {
            assertOk(only(send(loading, new OpenCmd(uuid, videoUrl))));
            // No waiting for 'open done': the player queues commands for a loading video
            assertAllOk(send(loading, new AddLocalizationsCmd(uuid, newLocalizations(2))));
        }
        finally {
            try {
                awaitOpenDone(uuid, 30); // let it finish loading so close() finds the window
            }
            catch (AssertionError e) {
                // close anyway
            }
            send(loading, new CloseCmd(uuid));
            loading.close();
        }
    }

    @Test
    public void closedVideoIsNoLongerKnown() throws Exception {
        requireVideo();
        var uuid = UUID.randomUUID();
        var other = new RVideoIO(uuid, HOST, PORT);
        try {
            openDones.clear();
            assertOk(only(send(other, new OpenCmd(uuid, videoUrl))));
            // Our RemoteControl receives 'open done' for every video the player opens
            var done = awaitOpenDone(uuid, 30);
            assertTrue(done.isOk());

            assertOk(only(send(other, new CloseCmd(uuid))));
            eventually("video is closed", () -> {
                var r = only(send(other, VideoCommands.REQUEST_STATUS)).response();
                return RResponse.FAILED.equals(r.getStatus()) ? r : null;
            });
            assertFailed(only(send(other, VideoCommands.REQUEST_STATUS)), NO_VIDEO);
            assertOk(only(send(other, new CloseCmd(uuid)))); // idempotent
        }
        finally {
            send(other, new CloseCmd(uuid));
            other.close();
        }
    }

    // ---- helpers -----------------------------------------------------------------------

    /** Opens the shared test video, once, and waits for 'open done'. Skips the test if no video is configured. */
    private static synchronized void openVideo() {
        requireVideo();
        if (videoOpened) {
            return;
        }
        openDones.clear();
        assertOk(only(send(io, new OpenCmd(VIDEO_UUID, videoUrl))));
        var done = awaitOpenDone(VIDEO_UUID, 60);
        assertTrue("Video failed to open: " + done.getCause(), done.isOk());
        videoOpened = true;
        var info = (RequestVideoInfoCmd.Response) only(send(io, RemoteCommands.REQUEST_VIDEO_INFO)).response();
        durationMillis = info.getDurationMillis();
    }

    /** Closes the shared test video, if open. The next test that needs it reopens it. */
    private static synchronized void closeSharedVideo() {
        if (videoOpened) {
            assertOk(only(send(io, new CloseCmd(VIDEO_UUID))));
            videoOpened = false;
            eventually("video is closed", () -> {
                var r = only(send(io, VideoCommands.REQUEST_STATUS)).response();
                return RResponse.FAILED.equals(r.getStatus()) ? r : null;
            });
        }
    }

    private static List<Localization> newLocalizations(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> new Localization(UUID.randomUUID(), "Concept " + i, 1000L * i, 0L,
                        10 + i, 20 + i, 300, 200, "#AAAAAA"))
                .toList();
    }

    private static void requireVideo() {
        Assume.assumeNotNull(videoUrl);
    }

    /** Waits for the 'open done' for this video, skipping notifications about other videos. */
    private static OpenDoneCmd.Request awaitOpenDone(UUID uuid, int timeoutSeconds) {
        var deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(timeoutSeconds);
        try {
            while (true) {
                var remaining = deadline - System.nanoTime();
                var done = remaining <= 0 ? null : openDones.poll(remaining, TimeUnit.NANOSECONDS);
                if (done == null) {
                    fail("No 'open done' for " + uuid + " within " + timeoutSeconds + "s");
                }
                if (uuid.equals(done.getUuid())) {
                    return done;
                }
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted waiting for 'open done'", e);
        }
    }

    /**
     * Seek, play, frame advance etc. are acknowledged before the player has finished acting on them.
     * Polls until the supplier returns non-null, for up to 5 seconds.
     */
    private static <T> T eventually(String what, java.util.function.Supplier<T> check) {
        var deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (true) {
            var result = check.get();
            if (result != null) {
                return result;
            }
            if (System.nanoTime() > deadline) {
                fail("Timed out waiting for: " + what);
            }
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AssertionError("Interrupted waiting for: " + what, e);
            }
        }
    }

    private RequestPlayerStateCmd.Response eventuallyState(String expected) {
        var last = new String[1];
        return eventually("player state '" + expected + "' (last: " + last[0] + ")", () -> {
            var s = state();
            last[0] = s.getState();
            return expected.equals(s.getState()) ? s : null;
        });
    }

    private void resetVideo() {
        send(io, VideoCommands.PAUSE);
        seek(0);
        send(io, new ClearLocalizationsCmd(new ClearLocalizationsCmd.Request(VIDEO_UUID)));
    }

    /** Seeks and waits for the player to land there: the seek is acknowledged before it completes. */
    private void seek(long millis) {
        assertOk(only(send(io, new SeekElapsedTimeCmd(Duration.ofMillis(millis)))));
        eventually("seek to " + millis + "ms", () -> Math.abs(elapsed() - millis) <= 250 ? Boolean.TRUE : null);
    }

    private long elapsed() {
        var r = (RequestElapsedTimeCmd.Response) only(send(io, VideoCommands.REQUEST_ELAPSED_TIME)).response();
        assertTrue("request elapsed time failed: " + r.getCause(), r.success());
        return r.getElapsedTimeMillis();
    }

    private RequestPlayerStateCmd.Response state() {
        var r = (RequestPlayerStateCmd.Response) only(send(io, VideoCommands.REQUEST_STATUS)).response();
        assertTrue("request player state failed: " + r.getCause(), r.success());
        assertNotNull("elapsedTimeMillis should be reported", r.getElapsedTimeMillis());
        return r;
    }

    /** Sends a command (which blocks until the player replies) and returns every response it produced. */
    private static List<CommandResponse> send(RVideoIO videoIO, VideoCommand<?> cmd) {
        var seen = new CopyOnWriteArrayList<CommandResponse>();
        var subscription = videoIO.getResponseObservable().subscribe(seen::add);
        try {
            videoIO.send(cmd);
        }
        finally {
            subscription.dispose();
        }
        return seen;
    }

    private static CommandResponse only(List<CommandResponse> responses) {
        assertEquals("Expected exactly one response but got " + responses.size(), 1, responses.size());
        return responses.get(0);
    }

    private static void assertOk(CommandResponse r) {
        assertEquals(r.response().getResponse() + " failed: " + r.response().getCause(),
                RResponse.OK, r.response().getStatus());
    }

    private static void assertAllOk(List<CommandResponse> responses) {
        assertFalse("No responses", responses.isEmpty());
        responses.forEach(SharktopodaIT::assertOk);
    }

    private static void assertFailed(CommandResponse r, String cause) {
        assertEquals("Expected " + r.command().getName() + " to fail but got " + r.response().getStatus(),
                RResponse.FAILED, r.response().getStatus());
        assertEquals(cause, r.response().getCause());
    }

    // ---- raw UDP -----------------------------------------------------------------------

    private record Received(JsonObject json, InetAddress address, int port) {}

    private static JsonObject raw(String json) throws IOException {
        try (var socket = new DatagramSocket()) {
            socket.setSoTimeout(3000);
            var bytes = json.getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(bytes, bytes.length, InetAddress.getByName(HOST), PORT));
            return receive(socket).json;
        }
    }

    private static Received receive(DatagramSocket socket) throws IOException {
        var buf = new byte[8192];
        var packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        }
        catch (SocketTimeoutException e) {
            fail("No UDP message received before timeout");
        }
        var msg = new String(buf, 0, packet.getLength(), StandardCharsets.UTF_8);
        return new Received(JsonParser.parseString(msg).getAsJsonObject(), packet.getAddress(), packet.getPort());
    }

    private static void send(DatagramSocket socket, String json, Received to) throws IOException {
        var bytes = json.getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(bytes, bytes.length, to.address, to.port));
    }

    private static boolean isPlayerUp() {
        try {
            return "ok".equals(raw("{\"command\":\"ping\"}").get("status").getAsString());
        }
        catch (Throwable t) {
            return false;
        }
    }

    private static int freePort() throws IOException {
        try (var s = new DatagramSocket(0)) {
            return s.getLocalPort();
        }
    }

    private static String setting(String property, String env, String defaultValue) {
        var v = System.getProperty(property);
        if (v == null || v.isBlank()) {
            v = System.getenv(env);
        }
        return v == null || v.isBlank() ? defaultValue : v;
    }
}
