/*
 * @(#)VCRStateAdapter.java   2009.02.24 at 09:44:50 PST
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



package org.mbari.vcr4j.adapter;


import mbarix4j.util.IObserver;
import mbarix4j.util.ObservableSupport;

/**
 *
 * @author brian
 */
public class VCRStateAdapter implements IVCRState {

    protected ObservableSupport os = new ObservableSupport();

    /** Creates a new instance of VCRStateAdapter */
    public VCRStateAdapter() {}

    public void addObserver(IObserver observer) {
        os.add(observer);
    }

    public boolean isBadCommunication() {
        return false;
    }

    public boolean isConnected() {
        return false;
    }

    public boolean isCueingUp() {
        return false;
    }

    public boolean isFastForwarding() {
        return false;
    }

    public boolean isHardwareError() {
        return false;
    }

    public boolean isJogging() {
        return false;
    }

    public boolean isLocal() {
        return false;
    }

    public boolean isPlaying() {
        return false;
    }

    public boolean isRecording() {
        return false;
    }

    public boolean isReverseDirection() {
        return false;
    }

    public boolean isRewinding() {
        return false;
    }

    public boolean isServoLock() {
        return false;
    }

    public boolean isServoRef() {
        return false;
    }

    public boolean isShuttling() {
        return false;
    }

    public boolean isStandingBy() {
        return false;
    }

    public boolean isStill() {
        return false;
    }

    public boolean isStopped() {
        return false;
    }

    public boolean isTapeEnd() {
        return false;
    }

    public boolean isTapeTrouble() {
        return false;
    }

    public boolean isTso() {
        return false;
    }

    public boolean isUnthreaded() {
        return false;
    }

    public boolean isVarSpeed() {
        return false;
    }

    /** Notifies all the observers of a chage of state */
    protected void notifyObservers() {
        os.notify(this, null);
    }

    public void removeAllObservers() {
        os.clear();
    }

    public void removeObserver(IObserver observer) {
        os.remove(observer);
    }
}
