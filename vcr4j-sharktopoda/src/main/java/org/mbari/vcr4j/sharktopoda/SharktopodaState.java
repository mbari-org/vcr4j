package org.mbari.vcr4j.sharktopoda;

import org.mbari.vcr4j.VideoState;

/**
 * @author Brian Schlining
 * @since 2016-08-25T16:40:00
 */
public class SharktopodaState implements VideoState {

    public enum State {
        PAUSED,
        PLAYING,
        SHUTTLE_FORWARD,
        SHUTTLE_REVERSE,
        NOT_FOUND
    }

    private final State state;

    public SharktopodaState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    @Override
    public boolean isConnected() {
        return !state.equals(State.NOT_FOUND);
    }

    @Override
    public boolean isCueingUp() {
        return false;
    }

    @Override
    public boolean isFastForwarding() {
        return state.equals(State.SHUTTLE_FORWARD);
    }

    @Override
    public boolean isPlaying() {
        return state.equals(State.PLAYING);
    }

    @Override
    public boolean isReverseDirection() {
        return state.equals(State.SHUTTLE_REVERSE);
    }

    @Override
    public boolean isRewinding() {
        return isReverseDirection();
    }

    @Override
    public boolean isShuttling() {
        return state.equals(State.SHUTTLE_FORWARD) || state.equals(State.SHUTTLE_REVERSE);
    }

    @Override
    public boolean isStopped() {
        return state.equals(State.PLAYING);
    }
}
