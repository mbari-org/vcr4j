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

import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.commands.ConnectCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureDoneCmd;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.OpenDoneCmd;
import org.mbari.vcr4j.remote.control.commands.RResponse;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
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
                sendOpenDone(uuid, result);
            });
        }
        catch (RejectedExecutionException e) {
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
    public void close() {
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
        return new ConnectCmd.Response(status);
    }
}
