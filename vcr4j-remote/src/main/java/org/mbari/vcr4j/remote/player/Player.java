package org.mbari.vcr4j.remote.player;

import org.mbari.vcr4j.remote.control.commands.FrameCaptureCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureDoneCmd;
import org.mbari.vcr4j.remote.control.commands.NoopCmd;
import org.mbari.vcr4j.remote.control.commands.loc.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.loc.ClearLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.loc.RemoveLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.loc.UpdateLocalizationsCmd;

public interface Player {

    /**
     * This method is meant to be impleneted by the controller app, not the video
     * player app.
     * @param request
     */
    FrameCaptureDoneCmd.Response handleFrameCaptureDoneRequest(FrameCaptureDoneCmd.Request request);

    FrameCaptureCmd.Response handleFrameCaptureRequest(FrameCaptureCmd.Request request);

    AddLocalizationsCmd.Response handleAddLocalizationsRequest(AddLocalizationsCmd.Request request);

    RemoveLocalizationsCmd.Response handleDeleteLocalizationsRequest(RemoveLocalizationsCmd.Request request);

    UpdateLocalizationsCmd.Response handleUpdateLocalizationsRequest(UpdateLocalizationsCmd.Request request);

    ClearLocalizationsCmd.Response handleClearLocalizationsRequest(ClearLocalizationsCmd.Request request);

    default NoopCmd.Response handleError(SimpleRequest request, Exception e) {
        return new NoopCmd.Response(request.getCommand(), "failed", e.getClass() + ": " + e.getMessage());
    }

    default NoopCmd.Response handleError(SimpleRequest request) {
        return new NoopCmd.Response(request.getCommand(), "failed");
    }
}
