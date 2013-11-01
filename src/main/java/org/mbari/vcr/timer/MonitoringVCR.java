/*
 * @(#)MonitoringVCR.java   2009.02.24 at 09:44:55 PST
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.mbari.movie.Timecode;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.IVCRError;
import org.mbari.vcr.IVCRReply;
import org.mbari.vcr.IVCRState;
import org.mbari.vcr.IVCRTimecode;
import org.mbari.vcr.IVCRUserbits;
import org.mbari.vcr.VCRAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>IVCR</code> class that acts as a proxy over an instance of another VCR
 * object. <code>Monitors</code> can be attached to trigger commands at intervals.
 *
 * @author brian
 */
public class MonitoringVCR implements IVCR {

    public static final IVCR DUMMY_VCR = new VCRAdapter();
    private static final Logger log = LoggerFactory.getLogger(MonitoringVCR.class);
    private Set<Monitor> monitors = new HashSet<Monitor>();
    private IVCR vcr;

    /**
     * Constructs ...
     */
    public MonitoringVCR() {
        this(DUMMY_VCR);
    }

    /**
     * Constructs ...
     *
     *
     * @param vcr
     */
    public MonitoringVCR(final IVCR vcr) {
        if (vcr == null) {
            throw new IllegalArgumentException("VCR can not be null");
        }

        setVcr(vcr);
        log.debug("Monitoring VCR intialized");
    }

    public void addMonitor(Monitor monitor) {
        synchronized (monitors) {
            monitor.setVcr(vcr);
            monitors.add(monitor);
        }
    }

    /**
     * Method description
     *
     */
    public void disconnect() {
        if (log.isDebugEnabled()) {
            log.debug("Removing timers and observers");
        }

        // Allow monitors to clean up
        for (Monitor monitor : new ArrayList<Monitor>(monitors)) {
            monitor.stop();
            monitor.setVcr(DUMMY_VCR);
        }

        vcr.disconnect();
    }

    public void kill() {
        // Allow monitors to clean up
        for (Monitor monitor : new ArrayList<Monitor>(monitors)) {
            monitor.stop();
            monitor.setVcr(DUMMY_VCR);
        }
        vcr.kill();
    }

    /** Eject the tape from the VTR */
    public void eject() {
        vcr.stop();
        vcr.releaseTape();
        vcr.eject();
    }

    /**
     * Method description
     *
     */
    public void fastForward() {
        vcr.fastForward();
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getConnectionName() {
        return vcr.getConnectionName();
    }

    public Set<Monitor> getMonitors() {
        return new HashSet<Monitor>(monitors);
    }

    /**
     *     @return  the vcr
     *     @uml.property  name="vcr"
     */
    public IVCR getVcr() {
        return vcr;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public IVCRError getVcrError() {
        return vcr.getVcrError();
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public IVCRReply getVcrReply() {
        return vcr.getVcrReply();
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public IVCRState getVcrState() {
        return vcr.getVcrState();
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public IVCRTimecode getVcrTimecode() {
        return vcr.getVcrTimecode();
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public IVCRUserbits getVcrUserbits() {
        return vcr.getVcrUserbits();
    }

    /**
     * Method description
     *
     */
    public void pause() {
        vcr.pause();
    }

    /**
     * Method description
     *
     */
    public void play() {
        vcr.play();
    }

    /**
     * Method description
     *
     *
     * @param timecode
     */
    public void presetTimecode(byte[] timecode) {
        vcr.presetTimecode(timecode);
    }

    /**
     * Method description
     *
     *
     * @param userbits
     */
    public void presetUserbits(byte[] userbits) {
        vcr.presetUserbits(userbits);
    }

    /**
     * Method description
     *
     */
    public void record() {
        vcr.record();
    }

    /**
     * Method description
     *
     */
    public void releaseTape() {
        vcr.releaseTape();
    }

    /**
     * Method description
     *
     */
    public void removeAllObservers() {
        vcr.removeAllObservers();
    }

    public void removeMonitor(Monitor monitor) {
        synchronized (monitors) {
            monitor.stop();
            monitor.setVcr(DUMMY_VCR);
            monitors.remove(monitor);
        }
    }

    /**
     * Method description
     *
     */
    public void requestDeviceType() {
        vcr.requestDeviceType();
    }

    /**
     * Method description
     *
     */
    public void requestLTimeCode() {
        vcr.requestLTimeCode();
    }

    /**
     * Method description
     *
     */
    public void requestLUserbits() {
        vcr.requestLUserbits();
    }

    /**
     * Method description
     *
     */
    public void requestLocalDisable() {
        vcr.requestLocalDisable();
    }

    /**
     * Method description
     *
     */
    public void requestLocalEnable() {
        vcr.requestLocalEnable();
    }

    /**
     * Method description
     *
     */
    public void requestStatus() {
        vcr.requestStatus();
    }

    /**
     * Method description
     *
     */
    public void requestTimeCode() {
        vcr.requestTimeCode();
    }

    public void requestUserbits() {
        vcr.requestUserbits();
    }

    /**
     * Method description
     *
     */
    public void requestVTimeCode() {
        vcr.requestVTimeCode();
    }

    /**
     * Method description
     *
     */
    public void requestVUserbits() {
        vcr.requestVUserbits();
    }

    /**
     * Method description
     *
     */
    public void rewind() {
        vcr.rewind();
    }

    /**
     * Method description
     *
     *
     * @param timecode
     */
    public void seekTimecode(byte[] timecode) {
        vcr.seekTimecode(timecode);
    }

    /**
     * Method description
     *
     *
     * @param timecode
     */
    public void seekTimecode(int timecode) {
        vcr.seekTimecode(timecode);
    }

    public void seekTimecode(Timecode timecode) {
        vcr.seekTimecode(timecode);
    }

    /**
     *     @param vcr  the vcr to set
     *     @uml.property  name="vcr"
     */
    public void setVcr(IVCR vcr) {

        if (vcr == null) {
            throw new IllegalArgumentException("You attempted to set the VCR " +
                                               "Property to null. This is not allowed!");
        }

        synchronized (monitors) {
            for (Monitor monitor : monitors) {
                monitor.setVcr(vcr);
            }
        }

        this.vcr = vcr;
        requestStatus();

    }

    /**
     * Method description
     *
     *
     * @param speed
     */
    public void shuttleForward(int speed) {
        vcr.shuttleForward(speed);
    }

    /**
     * Method description
     *
     *
     * @param speed
     */
    public void shuttleReverse(int speed) {
        vcr.shuttleReverse(speed);
    }

    public void startMonitors() {
        log.debug("Starting monitors");

        synchronized (monitors) {
            for (Monitor monitor : monitors) {
                monitor.start();
            }
        }
    }

    /**
     * Method description
     *
     */
    public void stop() {
        vcr.stop();
    }

    public void stopMonitors() {
        synchronized (monitors) {
            for (Monitor monitor : monitors) {
                monitor.stop();
            }
        }
    }
}
