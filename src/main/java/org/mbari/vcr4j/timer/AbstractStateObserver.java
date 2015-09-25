/*
 * @(#)AbstractStateObserver.java   2009.02.24 at 09:44:55 PST
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



package org.mbari.vcr4j.timer;

import java.util.Timer;
import org.mbari.util.IObserver;
import org.mbari.vcr4j.IVCR;
import org.mbari.vcr4j.IVCRState;

/**
 * Base class for observers that respond to the state of the VCR. These
 * observers send commands to the VCR at regular intervals.
 */
public abstract class AbstractStateObserver implements IObserver {

    protected long intervalMin = 33;
    protected long interval = intervalMin;

    /**
     * This timer is used to read timecode at regular intervals
     */
    Timer timer;
    protected IVCR vcr;

    /**
     * Constructs ...
     */
    public AbstractStateObserver() {
        super();
    }

    public long getInterval() {
        return interval;
    }

    Timer getTimer() {
        return timer;
    }

    public void setInterval(long interval) {
        if (interval < intervalMin) {
            interval = intervalMin;
        }

        boolean reset = interval != this.interval;

        this.interval = interval;

        if (reset && (getTimer() != null)) {
            stopTimer();
            startTimer();
        }
    }

    void setTimer(Timer timer) {
        this.timer = timer;
    }

    /**
     * <p>Use this method to set the type of task you will use. We could have
     *  used reflection and just have a user set the Class of the TimerTask
     *  to instantiate, but reflection is slow and the VCR application is
     *  a time-sensitive process. The code implemented should look something like
     *  this:</p>
     *  <pre>
     *  void startTimer() {
     *      if (getTimer() == null) {
     *          setTimer(new Timer(true));
     *          // TimerTask is an impementation of java.util.TimerTask
     *          getTimer().schedule(new MyTimerTask(), 0, getInterval());
     *      }
     *  }
     *  </pre>
     */
    abstract void startTimer();

    void stopTimer() {
        if (getTimer() != null) {
            getTimer().cancel();
            setTimer(null);
        }
    }

    /**
     * @param obj An IVCRState object that will notify the observer of it's
     *        state change
     * @param changeCode A string change code. This is not currently used.
     */
    public void update(Object obj, Object changeCode) {
        IVCRState state = (IVCRState) obj;

        //this.vcr = state.
        if (state.isStopped() || state.isStill()) {
            stopTimer();
        }
        else {
            startTimer();
        }
    }
}
