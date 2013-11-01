/*
 * @(#)IVCRState.java   2009.02.24 at 09:44:52 PST
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



package org.mbari.vcr;

import org.mbari.util.IObservable;

/**
 *
 * @author brian
 */
public interface IVCRState extends IObservable {

    /**
     * TODO 20031001 brian: What does this mean? Not defined in the 9-pin protocol
     *
     * @return
     */
    boolean isBadCommunication();

    /**
     * @return true if the VCR is connected
     */
    boolean isConnected();

    /**
     * @return True when a cue-up operation is completed. Reruns false as soon as the tape starts running.
     */
    boolean isCueingUp();

    /**
     * @return True if the VCR is fast-forwarding
     */
    boolean isFastForwarding();

    /**
     * @return True if a hardware error occurs in the Sony VCR. Errors may be due
     * to power supply overheating, motor drive circuitry overheating, tape path
     * system error, or communication error between servo and system control.
     */
    boolean isHardwareError();

    /**
     * @return True if the CONTROLLED DEVICE is in jog mode.
     */
    boolean isJogging();

    /**
     * @return True when the REMOTE/LOCAL switch on the front panel is set to
     * <b>LOCAL</B> or when the REMOTE/LOCAL switch is set to <B>REMOTE</B> and
     * the REMOTE switch S6 on the SY-70 board is set to REMOTE-3
     */
    boolean isLocal();

    /**
     * @return True if the VCR is playing.
     */
    boolean isPlaying();

    /**
     * @return True if the VCR is recording
     */
    boolean isRecording();

    /**
     * @return True if the tape in the vcr is moving backwards, i.e. during rewinds
     * shuttiling backwards, or reverse playing. False if the VCR is playing, fast-forwarding, or shuttiling forwards
     */
    boolean isReverseDirection();

    /**
     * @return True if the VCR is rewinding.
     */
    boolean isRewinding();

    /**
     * @return True when the VCR is in the following conditions:<br> <ol>
     * <li>The drum and capstan servos are locked in play mode</li>
     * <li>The drum servo is locked in a mode other than play mode</li> <li>During STANDBY OFF</li> </ol>
     */
    boolean isServoLock();

    /**
     * @return True when the signal selected by the SERVO REF selector on the
     * front panel is not inputted. (I don't know what this means either, thats a direct quote from the documentation.)
     */
    boolean isServoRef();

    /**
     * @return True if the VCR is in shuttiling mode (either forward or reverse).
     * Use the <i>isReverseDirection()</i> method in conjuncton with this
     * <i>isShuttling</i> to determine if the VCR is shuttiling forward or backwards.
     */
    boolean isShuttling();

    /**
     * @return True if the VCR is Standying by. (i.e When the CONTROLLED DEVICE
     * recieves the "20.05: standby on" command and goes into stand-by on mode.
     * Note: I've never seen the VCR return anything but true for this status
     * bit. I'm not sure what would cause the VCR to NOT standby
     */
    boolean isStandingBy();

    /**
     * @return True when the tape stops during stop or search mode and the
     * CONTROLLED DEVICE goes into the still mode. (Again, not sure exactly, when this occurs. Use isStopped() instead.
     */
    boolean isStill();

    /**
     * @return True if the VCR is in stop mode.
     */
    boolean isStopped();

    /**
     * @return True if the end of the tape is reached
     */
    boolean isTapeEnd();

    /**
     * @return True when a trouble such as he tape sticking to the drum occurs.
     */
    boolean isTapeTrouble();

    /**
     * @return True when the VCR is in auto edit mode.
     */
    boolean isTso();

    /**
     * @return True when there is no tape threading on the controlled device.
     */
    boolean isUnthreaded();

    /**
     * @return True when the VCR is in the following modes:<br> <ol> <li>VAR SLOW mode</li> <li>VAR MEMORY PLAY mode</li>
     * <li>CAPSTAN OVERRIDE mode</li> </ol>
     */
    boolean isVarSpeed();
}
