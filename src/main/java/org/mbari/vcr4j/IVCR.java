/*
 * @(#)IVCR.java   2009.02.24 at 09:44:52 PST
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



package org.mbari.vcr4j;


import org.mbari.vcr4j.util.Timecode;

/**
 * <p>Base interface of VCRs</p>
 * @author   Brian Schlining
 */
public interface IVCR {

    /** Disconnect from the VCR */
    void disconnect();

    /** Eject a tape */
    void eject();

    /** Fast forward the tape */
    void fastForward();

    /**
     *  @return  The name of the connection (e.g. a URL or a serial port)
     */
    String getConnectionName();

    /**
     *  @return
     */
    IVCRError getVcrError();

    /**
     *     @return
     */
    IVCRReply getVcrReply();

    /**
     *     @return
     */
    IVCRState getVcrState();

    /**
     *     @return
     */
    IVCRTimecode getVcrTimecode();

    /**
     *     @return
     */
    IVCRUserbits getVcrUserbits();

    /** The <i> Sony 9-pin protocol</i> does not provide a pause command. A clever programmer could implement this though. */
    void pause();

    /** Play the tape */
    void play();

    /**
     * @param timecode
     */
    void presetTimecode(byte[] timecode);

    /**
     * @param userbits
     */
    void presetUserbits(byte[] userbits);

    /** start recording */
    void record();

    /** Releases the tape */
    void releaseTape();

    /** Remove all observers from the reply, state, timecode, and error objects. */
    void removeAllObservers();

    /**
     * Query the VCR for its type. Return codes are
     * <pre>
     * MODEL           Data-1      Data-2
     *  BVH-2000(00)    00          11
     *  BVH-2000(02)    00          10
     * </pre>
     */
    void requestDeviceType();

    /**
     * Get longitudial timecode, which is stored on the audio track. Not as
     * accurate as getVTimeCode, but this method will get timecodes during fast-forwards, rewinds and shuttiling
     */
    void requestLTimeCode();

    /**
     *
     */
    void requestLUserbits();

    /**
     *
     */
    void requestLocalDisable();

    /**
     *
     */
    void requestLocalEnable();

    /** Send a "get status" command to the VCR */
    void requestStatus();

    /**
     *
     */
    void requestTimeCode();

    void requestUserbits();

    /**
     * Get vertical timecode, which is stored between video frames. These
     * timecodes can not be accessed in any mode accept play mode. So use
     * getLTimeCode when fast-forwarding, rewinding, or shuttling
     */
    void requestVTimeCode();

    /**
     *
     */
    void requestVUserbits();

    /** Rewind the tape */
    void rewind();

    /**
     * seek to a specified timecode
     * @param timecode The timecode to seek. Refer to Sony 9-pin protocol for the format of the timecode.
     */
    void seekTimecode(byte[] timecode);

    /**
     * seek to a specified timecode
     * @param timecode The timecode to seek. Refer to Sony 9-pin protocol for the format of the timecode.
     */
    void seekTimecode(int timecode);

    /**
     * Seek a specified timecode
     * @paream timecode The timecode to seek
     */
    void seekTimecode(Timecode timecode);

    /**
     * Shuttle forward
     * @param speed A value between 0 (VERY slow) and 255 (Fast)
     */
    void shuttleForward(int speed);

    /**
     * Shuttle backwards
     * @param speed A value between 0 (VERY slow) and 255 (Fast)
     */
    void shuttleReverse(int speed);

    /** Stop the tape */
    void stop();

    /** Stop the VCR. Best called during shutdown */
    void kill();
}
