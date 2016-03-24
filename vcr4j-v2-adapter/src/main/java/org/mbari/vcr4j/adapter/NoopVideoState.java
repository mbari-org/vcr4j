package org.mbari.vcr4j.adapter;

import org.mbari.vcr4j.VideoState;

/**
 * No-op implentation. All queries return false except for isStopped, which returns true.
 * @author Brian Schlining
 * @since 2016-03-24T13:29:00
 */
public class NoopVideoState implements VideoState {

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
        return true;
    }
}