/*
 * @(#)Monitor.java   2009.02.24 at 09:44:55 PST
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



package org.mbari.vcr.timer;

import java.util.Timer;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.VCRAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Monitor is a custom wrapper around a Timer and a TimerTask. This allows
 * for sceduling of repeated periodic requests such as requests for staus or
 * timecode form the VCR.
 *
 * @author brian
 */
public class Monitor {

    private static final Logger log = LoggerFactory.getLogger(Monitor.class);
    private long intervalMin = 33;
    protected long interval = intervalMin;
    private final String name;
    private Timer timer;
    private VCRTimerTask timerTask;
    private final Class timerTaskClass;
    private IVCR vcr;

    /**
     * Constructs ...
     *
     * @param timerTaskClass
     * @param name
     */
    public Monitor(final Class timerTaskClass, final String name) {
        this(timerTaskClass, name, new VCRAdapter());
    }

    /**
     * Constructs ...
     *
     * @param timerTaskClass
     * @param name
     * @param vcr
     */
    public Monitor(final Class timerTaskClass, final String name, final IVCR vcr) {
        this.name = name;
        this.timerTaskClass = timerTaskClass;
        setVcr(vcr);
        log.debug("Initialized Monitor '" + name + "'");
    }

    public long getInterval() {
        return interval;
    }

    public long getIntervalMin() {
        return intervalMin;
    }

    public String getName() {
        return name;
    }

    public VCRTimerTask getTimerTask() {
        boolean isRunning = (timer != null);

        if (!isRunning) {
            try {
                timerTask = (VCRTimerTask) timerTaskClass.newInstance();
                timerTask.setVcr(vcr);
            }
            catch (Exception ex) {
                log.error("Unable to instantiate " + timerTaskClass, ex);
            }
        }

        return timerTask;
    }

    public IVCR getVcr() {
        return vcr;
    }

    public void setInterval(long interval) {
        if (interval < intervalMin) {
            interval = intervalMin;
        }

        boolean reset = interval != this.interval;

        this.interval = interval;

        if (reset && (timer != null)) {
            stop();
            start();
        }
    }

    public void setIntervalMin(long intervalMin) {
        this.intervalMin = intervalMin;

        if (interval < intervalMin) {
            setInterval(intervalMin);
        }
    }

    public synchronized void setVcr(IVCR vcr) {

        boolean isRunning = (timer != null);

        // Turn off the timer if it's running
        if (isRunning) {
            stop();
        }

        this.vcr = vcr;

        // Restart timer if it was running
        if (isRunning) {
            start();
        }
    }

    public synchronized void start() {
        if (timer == null) {
            final String threadName = (name == null)
                                      ? "Monitor-" + System.currentTimeMillis()
                                      : name + "-" + System.currentTimeMillis();

            log.debug("Starting " + threadName);

            // Must get the task BEFORE we intialize the timer here.
            VCRTimerTask task = getTimerTask();

            timer = new Timer(threadName, true);
            timer.schedule(task, 0, getInterval());
        }
    }

    public synchronized void stop() {
        if (timer != null) {
            log.debug("Stopping " + name);
            timer.cancel();
            timer = null;
        }
    }
}
