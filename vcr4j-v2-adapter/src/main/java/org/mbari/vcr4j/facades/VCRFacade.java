package org.mbari.vcr4j.facades;

import org.mbari.vcr4j.IVCRError;
import org.mbari.vcr4j.IVCRReply;
import org.mbari.vcr4j.IVCRState;
import org.mbari.vcr4j.IVCRTimecode;
import org.mbari.vcr4j.IVCRUserbits;
import org.mbari.vcr4j.VCRAdapter;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.commands.SeekTimecodeCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.rs422.commands.PresetUserbitsCmd;
import org.mbari.vcr4j.rs422.commands.RS422VideoCommands;
import org.mbari.vcr4j.time.Timecode;

/**
 * @author Brian Schlining
 * @since 2016-03-09T15:20:00
 */
public class VCRFacade<S extends VideoState, E extends VideoError> extends VCRAdapter {

    private final VideoIO<S, E> videoIO;
    private final IVCRReply reply;

    public VCRFacade(VideoIO<S, E> videoIO) {
        this.videoIO = videoIO;
        this.reply = new VCRReplyFacade<>(videoIO);
    }

    @Override
    public void disconnect() {
        videoIO.close();
        super.disconnect();
    }

    @Override
    public void eject() {
        videoIO.send(RS422VideoCommands.EJECT);
    }

    @Override
    public void fastForward() {
        videoIO.send(VideoCommands.FAST_FORWARD);
    }

    @Override
    public String getConnectionName() {
        return videoIO.getConnectionID();
    }

    @Override
    public IVCRError getVcrError() {
        return reply.getVcrError();
    }

    @Override
    public IVCRReply getVcrReply() {
        return reply;
    }

    @Override
    public IVCRState getVcrState() {
        return reply.getVcrState();
    }

    @Override
    public IVCRTimecode getVcrTimecode() {
        return reply.getVcrTimecode();
    }

    @Override
    public IVCRUserbits getVcrUserbits() {
        return reply.getVcrUserbits();
    }

    @Override
    public void kill() {
        disconnect();
    }

    @Override
    public void pause() {
        videoIO.send(VideoCommands.PAUSE);
    }

    @Override
    public void play() {
        videoIO.send(VideoCommands.PLAY);
    }

    @Override
    public void presetTimecode(byte[] timecode) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void presetUserbits(byte[] userbits) {
        PresetUserbitsCmd cmd = new PresetUserbitsCmd(userbits);
        videoIO.send(cmd);
    }

    @Override
    public void record() {
        videoIO.send(RS422VideoCommands.RECORD);
    }

    @Override
    public void releaseTape() {
        videoIO.send(RS422VideoCommands.RELEASE_TAPE);
    }


    @Override
    public void requestDeviceType() {
        videoIO.send(VideoCommands.REQUEST_DEVICE_TYPE);
    }

    @Override
    public void requestLocalDisable() {
        videoIO.send(RS422VideoCommands.REQUEST_LOCAL_DISABLE);
    }

    @Override
    public void requestLocalEnable() {
        videoIO.send(RS422VideoCommands.REQUEST_LOCAL_ENABLE);
    }

    @Override
    public void requestLTimeCode() {
        videoIO.send(RS422VideoCommands.REQUEST_LTIMECODE);
    }

    @Override
    public void requestLUserbits() {
        videoIO.send(RS422VideoCommands.REQUEST_LUSERBITS);
    }

    @Override
    public void requestStatus() {
        videoIO.send(VideoCommands.REQUEST_STATUS);
    }

    @Override
    public void requestTimeCode() {
        videoIO.send(VideoCommands.REQUEST_TIMECODE);
    }

    @Override
    public void requestUserbits() {
        videoIO.send(RS422VideoCommands.REQUEST_USERBITS);
    }

    @Override
    public void requestVTimeCode() {
        videoIO.send(RS422VideoCommands.REQUEST_VTIMECODE);
    }

    @Override
    public void requestVUserbits() {
        videoIO.send(RS422VideoCommands.REQUEST_VUSERBITS);
    }

    @Override
    public void rewind() {
        videoIO.send(VideoCommands.REWIND);
    }

    @Override
    public void seekTimecode(byte[] timecode) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void seekTimecode(int timecode) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void seekTimecode(Timecode timecode) {
        videoIO.send(new SeekTimecodeCmd(timecode));
    }

    @Override
    public void shuttleForward(int speed) {
        videoIO.send(new ShuttleCmd(speed / 255D));
    }

    @Override
    public void shuttleReverse(int speed) {
        videoIO.send(new ShuttleCmd(speed / -255D));
    }

    @Override
    public void stop() {
        videoIO.send(VideoCommands.STOP);
    }

    public VideoIO<S, E> getVideoIO() {
        return videoIO;
    }
}
