package org.mbari.vcr4j.udp;

/*-
 * #%L
 * vcr4j-udp
 * %%
 * Copyright (C) 2008 - 2026 Monterey Bay Aquarium Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
