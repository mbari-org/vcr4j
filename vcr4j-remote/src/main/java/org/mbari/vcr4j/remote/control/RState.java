package org.mbari.vcr4j.remote.control;

import org.mbari.vcr4j.VideoState;

import java.util.Arrays;

public class RState implements VideoState {

    public enum State {
        PAUSED("paused"),
        PLAYING("playing"),
        SHUTTLE_FORWARD("shuttling forward"),
        SHUTTLE_REVERSE("shuttling reverse"),
        NOT_FOUND("not found"),
        UNKNOWN_ERROR("error");

        private final String name;

        State(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private final State state;

    public RState(State state) {
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
        return state.equals(State.PAUSED);
    }

    public static RState parse(String name) {
        var state = Arrays.stream(State.values())
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(State.NOT_FOUND);
        return new RState(state);
    }
}
