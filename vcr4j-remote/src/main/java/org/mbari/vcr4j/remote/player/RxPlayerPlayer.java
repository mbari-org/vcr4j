package org.mbari.vcr4j.remote.player;

import org.mbari.vcr4j.remote.control.commands.FrameCaptureCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureDoneCmd;

import org.mbari.vcr4j.remote.control.commands.RResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * THis is an implementation for the video player.
 */
public class RxPlayerPlayer extends RxPlayer {

    private static final Logger log = LoggerFactory.getLogger(RxControlPlayer.class);
    private final Consumer<FrameCaptureCmd> frameCaptureFn;

    /**
     *
     * @param frameCaptureFn This function handles the actual framecapture.
     *                       When it's completed it needs to send a
     *                       FrameCaptureDoneCmd to the controlling app.
     */
    public RxPlayerPlayer(Consumer<FrameCaptureCmd> frameCaptureFn) {
        super();
        this.frameCaptureFn = frameCaptureFn;
    }

    /**
     * THis method shouldn't actually be used on the video player side. It's needed
     * for the controlling applicaiton though.
     * @param request
     * @return
     */
    @Override
    public FrameCaptureDoneCmd.Response handleFrameCaptureDoneRequest(FrameCaptureDoneCmd.Request request) {
        throw new UnsupportedOperationException("This method is not used in the video player");
    }

    @Override
    public FrameCaptureCmd.Response handleFrameCaptureRequest(FrameCaptureCmd.Request request) {
        frameCaptureFn.accept(new FrameCaptureCmd(request));
        return new FrameCaptureCmd.Response(RResponse.OK);
    }

}
