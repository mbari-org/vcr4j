package org.mbari.vcr4j.udp;

import org.mbari.vcr4j.VideoState;

/**
 * @author Brian Schlining
 * @since 2016-02-04T13:27:00
 */
public class UDPState implements VideoState {

    private final boolean connected;
    private final boolean playing;
    private final boolean recording;

    public static final UDPState STOPPED = new UDPState(false, false, false);
    public static final UDPState RECORDING = new UDPState(true, false, true);


    public UDPState(boolean connected, boolean playing, boolean recording) {
        this.connected = connected;
        this.playing = playing;
        this.recording = recording;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean isCueingUp() {
        return false;
    }

    @Override
    public boolean isFastForwarding() {
        return false;
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public boolean isReverseDirection() {
        return false;
    }

    @Override
    public boolean isRewinding() {
        return false;
    }

    @Override
    public boolean isShuttling() {
        return false;
    }

    @Override
    public boolean isStopped() {
        return !(playing || recording);
    }

    public boolean isRecording() {
        return recording;
    }
}
