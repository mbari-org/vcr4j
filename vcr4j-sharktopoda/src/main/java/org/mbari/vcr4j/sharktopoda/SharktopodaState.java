/*
 * Copyright © 2008 MBARI (brian@mbari.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mbari.vcr4j.sharktopoda;

import org.mbari.vcr4j.VideoState;

import java.util.Arrays;

/**
 * @author Brian Schlining
 * @since 2016-08-25T16:40:00
 */
public class SharktopodaState implements VideoState {

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
        return state.equals(State.PAUSED);
    }

    public static SharktopodaState parse(String name) {
        State state = Arrays.stream(State.values())
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(State.NOT_FOUND);
        return new SharktopodaState(state);
    }
}
