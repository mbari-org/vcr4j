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

import org.mbari.vcr4j.remote.control.commands.RResponse;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * THis is an implementation for the video player. It is created by
 * {@link VideoControl}
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RxPlayerRequestHandler extends RxRequestHandler {

    private static final System.Logger log = System.getLogger(RxPlayerRequestHandler.class.getName());

    private final RVideoIOLifeCycle lifeCycle;

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
    public FrameCaptureCmd.Response handleFrameCaptureRequest(FrameCaptureCmd.Request request) {
        // getParent() is null for a bare filename; toAbsolutePath() resolves against the JVM
        // working directory so the writability check has a real directory to test.
        var path = Paths.get(request.getImageLocation()).toAbsolutePath();
        var response = new FrameCaptureCmd.Response(RResponse.OK);
        if (!Files.isWritable(path.getParent())) {
            log.log(System.Logger.Level.WARNING, path.getParent() + " is not writable. Unable to write frame-grab to " + path);
            response = new FrameCaptureCmd.Response(RResponse.FAILED);
        }
        else if (Files.exists(path)) {
            log.log(System.Logger.Level.WARNING, path + " already exist. Overwriting existing file");
        }
        getVideoController()
                .framecapture(request.getUuid(), request.getImageReferenceUuid(), path)
                .handle((fc, ex) -> {
                    var resp = (fc == null || ex != null) ?
                            FrameCaptureDoneCmd.fail(request) :
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

        return response;
    }

    @Override
    public ConnectCmd.Response handleConnect(ConnectCmd.Request request) {
        var opt = lifeCycle.connect(request.getUuid(), request.getHost(), request.getPort());
        var status = opt.map(io -> RResponse.OK).orElse(RResponse.FAILED);
        return new ConnectCmd.Response(status);
    }
}
