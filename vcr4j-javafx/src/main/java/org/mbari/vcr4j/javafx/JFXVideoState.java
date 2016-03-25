package org.mbari.vcr4j.javafx;

import org.mbari.vcr4j.VideoState;

/**
 * @author Brian Schlining
 * @since 2016-03-25T10:57:00
 */
public class JFXVideoState implements VideoState {

    private final boolean fastForward;
    private final boolean shuttling;
    private final boolean stopped;
    private final boolean playing;

    public JFXVideoState(boolean fastForward, boolean shuttling, boolean stopped, boolean playing) {
        this.fastForward = fastForward;
        this.shuttling = shuttling;
        this.stopped = stopped;
        this.playing = playing;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isCueingUp() {
        return false;
    }

    @Override
    public boolean isFastForwarding() {
        return fastForward;
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
        return shuttling;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }
}
