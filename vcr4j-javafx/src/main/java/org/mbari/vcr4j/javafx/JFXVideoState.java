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
