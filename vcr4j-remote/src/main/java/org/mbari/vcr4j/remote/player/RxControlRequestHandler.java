package org.mbari.vcr4j.remote.player;


import org.mbari.vcr4j.remote.control.commands.ConnectCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureDoneCmd;
import org.mbari.vcr4j.remote.control.commands.RResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * This implementation is for the app that controls the video player,
 * not the video player itself. It is created using {@link org.mbari.vcr4j.remote.control.RemoteControl.Builder}
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RxControlRequestHandler extends RxRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RxControlRequestHandler.class);

    private final Consumer<FrameCaptureDoneCmd> frameCaptureDoneFn;

    /**
     *
     * @param frameCaptureDoneFn A function to do any post processing needed when
     *                           are frame capture is completed. Ideally this
     *                           should handle things in a seperate thread so
     *                           as not to block the response.
     */
    public RxControlRequestHandler(Consumer<FrameCaptureDoneCmd> frameCaptureDoneFn) {
        super(new NoopVideoController());
        this.frameCaptureDoneFn = frameCaptureDoneFn;
    }


    @Override
    public ConnectCmd.Response handleConnect(ConnectCmd.Request request) {
        log.atWarn()
                .log("The handleConnect method is not supported in the controller app");
        return new ConnectCmd.Response(RResponse.FAILED);
    }

    @Override
    public FrameCaptureDoneCmd.Response handleFrameCaptureDoneRequest(FrameCaptureDoneCmd.Request request) {
        frameCaptureDoneFn.accept(new FrameCaptureDoneCmd(request));
        return new FrameCaptureDoneCmd.Response(RResponse.OK);
    }

    /**
     * The Controller doens't do framecapture, that's the players jom
     * @param request
     * @return
     */
    @Override
    public FrameCaptureCmd.Response handleFrameCaptureRequest(FrameCaptureCmd.Request request) {
        //throw new UnsupportedOperationException("This method is not implemented in the controller application");
        log.atWarn()
                .log("The handleFrameCaptureRequest method is not supported in the controller app");
        return new FrameCaptureCmd.Response(RResponse.FAILED);
    }

}
