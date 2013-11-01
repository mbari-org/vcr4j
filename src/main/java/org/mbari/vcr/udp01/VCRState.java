/*
 * @(#)VCRState.java   2009.02.24 at 09:44:55 PST
 *
 * Copyright 2007 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.mbari.vcr.udp01;

import org.mbari.vcr.VCRStateAdapter;

/**
 *
 * @author brian
 */
public class VCRState extends VCRStateAdapter {

    /**
         * @uml.property  name="playing"
         */
    private boolean playing = true;

    /**
         * @uml.property  name="connected"
         */
    private boolean connected = true;

    /**
         * @uml.property  name="recording"
         */
    private boolean recording = true;

    /** Creates a new instance of VCRState */
    public VCRState() {}

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public boolean isRecording() {
        return recording;
    }

    @Override
    public boolean isStill() {
        return false;
    }

    @Override
    public boolean isStopped() {
        return false;
    }

    @Override
    protected void notifyObservers() {
        super.notifyObservers();
    }

    /**
         * @param connected  the connected to set
         * @uml.property  name="connected"
         */
    protected void setConnected(boolean connected) {
        this.connected = connected;
        this.playing = connected;
        this.recording = connected;
        notifyObservers();
    }

    /**
         * @param playing  the playing to set
         * @uml.property  name="playing"
         */
    protected void setPlaying(boolean playing) {
        this.playing = playing;
        notifyObservers();
    }

    /**
         * @param recording  the recording to set
         * @uml.property  name="recording"
         */
    public void setRecording(boolean recording) {
        this.recording = recording;
        notifyObservers();
    }
}
