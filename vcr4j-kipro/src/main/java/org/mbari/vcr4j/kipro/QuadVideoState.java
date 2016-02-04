package org.mbari.vcr4j.kipro;

import org.mbari.vcr4j.VideoState;

/**
 * @author Brian Schlining
 * @since 2016-02-04T11:17:00
 */
public class QuadVideoState implements VideoState {

    @Override
    public boolean isConnected() {
        return false;
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
        return false;
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
        return false;
    }
}
