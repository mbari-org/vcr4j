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
package org.mbari.vcr4j.remote.player;

import org.mbari.vcr4j.commands.RemoteCommands;
import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.commands.ConnectCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureDoneCmd;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.OpenDoneCmd;
import org.mbari.vcr4j.remote.control.commands.localization.LocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.RResponse;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * THis is an implementation for the video player. It is created by
 * {@link VideoControl}
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RxPlayerRequestHandler extends RxRequestHandler {

    private static final System.Logger log = System.getLogger(RxPlayerRequestHandler.class.getName());

    private final RVideoIOLifeCycle lifeCycle;

    // Videos are opened one at a time, in arrival order, off the UDP receive thread
    private final ExecutorService openExecutor = Executors.newSingleThreadExecutor(r -> {
        var t = new Thread(r, "vcr4j-open-video");
        t.setDaemon(true);
        return t;
    });

    // Pings after a connect, so that a slow remote never delays the UDP receive thread
    private final ExecutorService pingExecutor = Executors.newSingleThreadExecutor(r -> {
        var t = new Thread(r, "vcr4j-ping-remote");
        t.setDaemon(true);
        return t;
    });

    // Videos that have been accepted for opening but are not open yet, with the number of opens pending
    // for each. Localization commands for these are accepted and queued behind the open.
    private final Map<UUID, Integer> pendingOpens = new ConcurrentHashMap<>();

    // Localization commands waiting behind an open. While any are, later ones queue too, to keep their order.
    private final AtomicInteger deferredDispatches = new AtomicInteger();

    /**
     *
     */
    public RxPlayerRequestHandler(VideoController videoController, RVideoIOLifeCycle lifeCycle) {
        super(videoController);
        this.lifeCycle = lifeCycle;
    }

    public RVideoIOLifeCycle getLifeCycle() {
        return lifeCycle;
    }

    /**
     * THis method shouldn't actually be used on the video player side. It's needed
     * for the controlling applicaiton though.
     * @param request
     * @return
     */
    @Override
    public FrameCaptureDoneCmd.Response handleFrameCaptureDoneRequest(FrameCaptureDoneCmd.Request request) {
        //throw new UnsupportedOperationException("This method is not used in the video player");
        return new FrameCaptureDoneCmd.Response(RResponse.FAILED);
    }

    @Override
    public OpenCmd.Response handleOpen(OpenCmd.Request request) {
        if (request.getUrl() == null) {
            throw new IllegalArgumentException("A url is required to open a video");
        }
        var uuid = request.getUuid();
        var url = request.getUrl();
        // The request is valid, so respond immediately. The video is opened in the background
        // and 'open done' is sent to the connected remote app when that's finished.
        // VideoController.openVideo is required to block until the video is open and ready to
        // play (or has failed), so 'open done' is never sent before the video is usable.
        pendingOpens.merge(uuid, 1, Integer::sum);
        try {
            openExecutor.submit(() -> {
                VideoResult result;
                try {
                    result = getVideoController().openVideo(uuid, url);
                }
                catch (Exception e) {
                    log.log(System.Logger.Level.WARNING, "Failed to open " + url, e);
                    result = VideoResult.failed("Unable to open video");
                }
                finally {
                    pendingOpens.compute(uuid, (k, n) -> n == null || n <= 1 ? null : n - 1);
                }
                sendOpenDone(uuid, result);
            });
        }
        catch (RejectedExecutionException e) {
            pendingOpens.compute(uuid, (k, n) -> n == null || n <= 1 ? null : n - 1);
            return new OpenCmd.Response(RResponse.FAILED, "Unable to open video");
        }
        return new OpenCmd.Response(RResponse.OK);
    }

    private void sendOpenDone(UUID uuid, VideoResult result) {
        var status = result.ok() ? RResponse.OK : RResponse.FAILED;
        var done = new OpenDoneCmd(uuid, status, result.cause());
        var io = lifeCycle.get();
        if (io.isPresent()) {
            io.get().send(done);
        }
        else {
            log.log(System.Logger.Level.WARNING,
                    "No active connection to send 'open done' for uuid=" + uuid + " - dropping message");
        }
    }

    @Override
    protected boolean isVideoKnown(UUID videoUuid) {
        return pendingOpens.containsKey(videoUuid) || super.isVideoKnown(videoUuid);
    }

    /**
     * A video that is still opening can't take localizations yet, so those commands wait for the open
     * to finish. The open executor runs one task at a time in arrival order, so queueing them on it
     * delivers them after the open and in the order they were received.
     */
    @Override
    protected void dispatch(UUID videoUuid, LocalizationsCmd<?, ?> cmd) {
        if (pendingOpens.containsKey(videoUuid) || deferredDispatches.get() > 0) {
            deferredDispatches.incrementAndGet();
            try {
                openExecutor.submit(() -> {
                    try {
                        super.dispatch(videoUuid, cmd);
                    }
                    finally {
                        deferredDispatches.decrementAndGet();
                    }
                });
                return;
            }
            catch (RejectedExecutionException e) {
                deferredDispatches.decrementAndGet();
            }
        }
        super.dispatch(videoUuid, cmd);
    }

    /**
     * A connect without a host means the remote app is at the address the request came from.
     */
    @Override
    public RResponse composeResponse(SimpleRequest simpleRequest) {
        var sender = simpleRequest.getSender();
        if (sender != null && ConnectCmd.COMMAND.equals(simpleRequest.getCommand())) {
            return handle(simpleRequest, ConnectCmd.Request.class, request -> {
                var host = request.getHost() == null ? sender.getHostAddress() : request.getHost();
                return handleConnect(new ConnectCmd.Request(request.getPort(), host, request.getUuid()));
            });
        }
        return super.composeResponse(simpleRequest);
    }

    @Override
    public void close() {
        pingExecutor.shutdownNow();
        openExecutor.shutdown();
        super.close();
    }

    @Override
    public FrameCaptureCmd.Response handleFrameCaptureRequest(FrameCaptureCmd.Request request) {
        if (request.getImageLocation() == null || request.getImageReferenceUuid() == null) {
            throw new IllegalArgumentException("imageLocation and imageReferenceUuid are required");
        }
        if (!getVideoController().hasVideo(request.getUuid())) {
            return new FrameCaptureCmd.Response(RResponse.FAILED, VideoResult.NO_VIDEO_FOR_UUID);
        }

        Path path;
        try {
            // getParent() is null for a bare filename; toAbsolutePath() resolves against the JVM
            // working directory so the writability check has a real directory to test.
            path = Paths.get(request.getImageLocation()).toAbsolutePath();
        }
        catch (InvalidPathException e) {
            return new FrameCaptureCmd.Response(RResponse.FAILED, "Malformed image location");
        }

        if (Files.exists(path)) {
            return new FrameCaptureCmd.Response(RResponse.FAILED, VideoResult.IMAGE_EXISTS);
        }
        if (path.getParent() == null || !Files.isWritable(path.getParent())) {
            log.log(System.Logger.Level.WARNING, path.getParent() + " is not writable. Unable to write frame-grab to " + path);
            return new FrameCaptureCmd.Response(RResponse.FAILED, VideoResult.IMAGE_NOT_WRITABLE);
        }

        getVideoController()
                .framecapture(request.getUuid(), request.getImageReferenceUuid(), path)
                .handle((fc, ex) -> {
                    var resp = (fc == null || ex != null) ?
                            FrameCaptureDoneCmd.fail(request, failureMessage(ex)) :
                            FrameCaptureDoneCmd.success(fc);
                    if (log.isLoggable(System.Logger.Level.DEBUG)) {
                        var msg = RVideoIO.GSON.toJson(resp);
                        log.log(System.Logger.Level.DEBUG, "Framecapture is done. Sending: \n" + msg);
                    }
                    var io = lifeCycle.get();
                    if (io.isPresent()) {
                        io.get().send(resp);
                    }
                    else {
                        // No connection when the async capture finished — the controller
                        // that requested this frame will never receive the done cmd. Callers
                        // blocking on the done callback will hang; surface it in the log.
                        log.log(System.Logger.Level.WARNING,
                                "No active connection to send FrameCaptureDoneCmd for uuid="
                                        + request.getUuid() + ", imageReferenceUuid="
                                        + request.getImageReferenceUuid() + " — dropping response");
                    }
                    return null;
                });
        return new FrameCaptureCmd.Response(RResponse.OK);
    }

    private static String failureMessage(Throwable ex) {
        if (ex == null) {
            return "Unable to capture frame";
        }
        var cause = ex instanceof CompletionException && ex.getCause() != null ? ex.getCause() : ex;
        return cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage();
    }

    @Override
    public ConnectCmd.Response handleConnect(ConnectCmd.Request request) {
        var opt = lifeCycle.connect(request.getUuid(), request.getHost(), request.getPort());
        var status = opt.map(io -> RResponse.OK).orElse(RResponse.FAILED);
        // The protocol has the player check that the remote is reachable. There is no one to tell
        // about a failure other than the log, so the ping is only informational.
        opt.ifPresent(io -> {
            try {
                pingExecutor.submit(() -> io.send(RemoteCommands.PING));
            }
            catch (RejectedExecutionException e) {
                log.log(System.Logger.Level.DEBUG, "Not pinging the remote: the handler is closed");
            }
        });
        return new ConnectCmd.Response(status);
    }
}
