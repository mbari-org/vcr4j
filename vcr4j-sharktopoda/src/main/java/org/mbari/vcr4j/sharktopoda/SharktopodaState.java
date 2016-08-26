package org.mbari.vcr4j.sharktopoda;

import org.mbari.vcr4j.VideoState;

/**
 * @author Brian Schlining
 * @since 2016-08-25T16:40:00
 */
public class SharktopodaState implements VideoState {

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
