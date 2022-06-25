package org.mbari.vcr4j.remote.player;

import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.commands.ConnectCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureDoneCmd;

import org.mbari.vcr4j.remote.control.commands.RResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * THis is an implementation for the video player.
 */
public class RxPlayerRequestHandler extends RxRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RxControlRequestHandler.class);

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
//        frameCaptureFn.accept(new FrameCaptureCmd(request));
        var path = Paths.get(request.getImageLocation());
        var response = new FrameCaptureCmd.Response(RResponse.OK);
        if (!Files.isWritable(path)) {
            log.atWarn()
                    .log(path + " is not writable. Unable to write frame-grab to that location.");
            response = new FrameCaptureCmd.Response(RResponse.FAILED);
        }
        else if (Files.exists(path)) {
            log.atWarn()
                    .log(path + " already exist. Overwriting existing file");
        }
        getVideoController()
                .framecapture(request.getUuid(), path)
                .handle((fc, ex) -> {
                    var resp = (fc == null || ex != null) ?
                            FrameCaptureDoneCmd.fail(request) :
                            FrameCaptureDoneCmd.success(fc);
                    lifeCycle.get().ifPresent(io -> io.send(resp));
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
