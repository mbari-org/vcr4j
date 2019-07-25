package org.mbari.vcr4j.rs422;

import org.mbari.vcr4j.VideoState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class RS422State implements VideoState {

    /** Corrected LTC time data */
    public static final byte[] OTHER_STATUS_REPLY = { 0x71, 0x20 };

    public static final byte[] STATUS_REPLY = { 0x73, 0x20 };

    /**
     * bit representing bad communications. 1 = bad comuunications, 0 =
     * communications are OK.
     */
    public final static long STS_BAD_COMM = 0x0000080L;

    /** bit representing cueing status. 1 = cueing up, 0 = not cueing up */
    public final static long STS_CUE_UP = 0x0010000L;

    /**
     * Bit representing direction, 1 = reverse, 0= forward Sony documentation states
     * STS_DIRECTION = 0x0040000L. However we found it is STS_DIRECTION = 0x0000004L
     */
    public final static long STS_DIRECTION = 0x0000004L;

    // public static final long STS_DIRECTION = 0x0040000L;

    /**
     * Bit representing fast-forward mode. 1 if the VCR is fast-forwarding, 0
     * otherwise
     */
    public final static long STS_FAST_FWD = 0x0000400L;

    /**
     * bit representing hardware error, 1 = error, 0 = no error. A typical error is
     * due to overheating. NOTE: Documentation states: STS_HW_ERROR = 0x0000004L;
     * However we found that is used by the VCR to indicate direction
     */
    public final static long STS_HW_ERROR = 0x0000000L;

    // public static final long STS_HW_ERROR = 0x0000004L;

    /** bit representing jog mode, 1 = jog mode, 0 = not jog mode. */
    public final static long STS_JOG = 0x0100000L;

    // //////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // Sony Status Bit Definitions - One or more of following bits may
    // be set on return from sony_status()
    public final static long STS_LOCAL = 0x0000001L;

    /** Bit representing play mode. 1 if the VCR is playing, 0 otherwise */
    public final static long STS_PLAY = 0x0000100L;

    /** Bit representing record mode. 1 if the VCR is recording, 0 otherwise */
    public final static long STS_RECORD = 0x0000200L;

    /** Bit representing rewind mode. 1 if the VCR is rewinding, 0 otherwise */
    public final static long STS_REWIND = 0x0000800L;

    /**
     * Bit representing servo lock. 1 when the VCR is in the following
     * conditions:<br>
     * <ol>
     * <li>The drum and capstan servos are locked in play mode</li>
     * <li>The drum servo is locked in a mode other than play mode</li>
     * <li>During STANDBY OFF</li>
     * </ol>
     */
    public final static long STS_SERVO_LOCK = 0x0800000L;

    /**
     * 1 when the signal selected by the SERVO REF selector on the front panel is
     * not inputted. (I don't know what this means either, thats a direct quote from
     * the documentation.)
     */
    public final static long STS_SERVO_REF = 0x0000010L;

    /**
     * Bit representing shuttle mode, 1 = vcr is shuttiling, 0= is not shuttiling.
     * Use in combintation with STS_DIRECTION to determine if the VCR is shuttiling
     * forward or in reverse<br>
     * <br>
     * <b>Note: Sony documentation says: STS_SHUTTLE = 0x0200000L. However we found
     * it to be STS_SHUTTLE = 0x0000020</b>
     */
    public final static long STS_SHUTTLE = 0x0000020L;

    // public static final long STS_SHUTTLE = 0x0200000L;

    /**
     * Bit representing stand-by mode. 1 if the VCR is standing-by, 0 otherwise.
     * Note: The VCR always seems to be standing-by. I'm not sure what would cause
     * the VCR to NOT stand-by.
     */
    public final static long STS_STANDBY = 0x0008000L;

    /**
     * bit reprepresenting still status, 1 when the tape stops during stop or search
     * mode and the CONTROLLED DEVICE goes into the still mode. (Again, not sure
     * exactly, when this occurs. Use isStopped() instead.
     */
    public final static long STS_STILL = 0x0020000L;

    /** Bit representing stop mode. 1 if the VCR is stopped, 0 otherwise */
    public final static long STS_STOP = 0x0002000L;

    /**
     * Bit indicating if the end of the tape has been reached. 1 if it's the end of
     * the tape, 0 otherwise
     */
    public final static long STS_TAPE_END = 0x4000000L;

    /** bit representing tape trouble, 1 = trouble, 0 = no trouble. */
    public final static long STS_TAPE_TROUBLE = 0x0000008L;

    /**
     * Bit representing auto edit mode, 1 when the VCR is in auto edit mode, 0
     * otherwise.
     */
    public final static long STS_TSO = 0x0400000L;

    /**
     * WARNING: This bit maks is incorrectly defined in the sony documentation. It
     * is currently no implemented
     */
    public final static long STS_UNTHREAD = 0x0000000L;

    // public static final long STS_UNTHREAD = 0x0000020L;

    /**
     * bit representing var mode. 1 when the VCR is in the following modes:<br>
     * <ol>
     * <li>VAR SLOW mode</li>
     * <li>VAR MEMORY PLAY mode</li>
     * <li>CAPSTAN OVERRIDE mode</li>
     * </ol>
     */
    public final static long STS_VARSPEED = 0x0080000L;
    private static final Logger log = LoggerFactory.getLogger(RS422State.class);
    public static final RS422State STOPPED = new RS422State(STS_STOP);

    private final long status;

    public RS422State(long status) {
        this.status = status;
        logStatus();
    }

    /**
     * TODO 20031001 brian: What does this mean? Not defined in the 9-pin protocol
     *
     * @return not really sure what this is.
     */
    public boolean isBadCommunication() {
        return ((status & STS_BAD_COMM) > 0);
    }

    @Override
    public boolean isConnected() {
        return status > 0;
    }

    /**
     * @return True when a cue-up operation is completed. Reruns false as soon as
     *         the tape starts running.
     */
    @Override
    public boolean isCueingUp() {
        return ((status & STS_CUE_UP) > 0);
    }

    /** @return True if the VCR is fast-forwarding */
    @Override
    public boolean isFastForwarding() {
        return ((status & STS_FAST_FWD) > 0);
    }

    /**
     * @return True if a hardware error occurs in the Sony VCR. Errors may be due to
     *         power supply overheating, motor drive circuitry overheating, tape
     *         path system error, or communication error between servo and system
     *         control.
     */
    public boolean isHardwareError() {
        return ((status & STS_HW_ERROR) > 0);
    }

    /** @return True if the CONTROLLED DEVICE is in jog mode. */
    public boolean isJogging() {
        return ((status & STS_JOG) > 0);
    }

    /**
     * @return True when the REMOTE/LOCAL switch on the front panel is set to
     *         <b>LOCAL</B> or when the REMOTE/LOCAL switch is set to <B>REMOTE</B>
     *         and the REMOTE switch S6 on the SY-70 board is set to REMOTE-3
     */
    public boolean isLocal() {
        return ((status & STS_LOCAL) > 0);
    }

    /** @return True if the VCR is playing. */
    @Override
    public boolean isPlaying() {
        return ((status & STS_PLAY) > 0);
    }

    /** @return True if the VCR is recording */
    public boolean isRecording() {
        return ((status & STS_RECORD) > 0);
    }

    /**
     * @return True if the tape in the vcr is moving backwards, i.e. during rewinds
     *         shuttiling backwards, or reverse playing. False if the VCR is
     *         playing, fast-forwarding, or shuttiling forwards
     */
    @Override
    public boolean isReverseDirection() {
        return ((status & STS_DIRECTION) > 0);
    }

    /** @return True if the VCR is rewinding. */
    @Override
    public boolean isRewinding() {
        return ((status & STS_REWIND) > 0);
    }

    /**
     * @return True when the VCR is in the following conditions:<br>
     *         <ol>
     *         <li>The drum and capstan servos are locked in play mode</li>
     *         <li>The drum servo is locked in a mode other than play mode</li>
     *         <li>During STANDBY OFF</li>
     *         </ol>
     */
    public boolean isServoLock() {
        return ((status & STS_SERVO_LOCK) > 0);
    }

    /**
     * @return True when the signal selected by the SERVO REF selector on the front
     *         panel is not inputted. (I don't know what this means either, thats a
     *         direct quote from the documentation.)
     */
    public boolean isServoRef() {
        return ((status & STS_SERVO_REF) > 0);
    }

    /**
     * @return True if the VCR is in shuttiling mode (either forward or reverse).
     *         Use the <i>isReverseDirection()</i> method in conjuncton with this
     *         <i>isShuttling</i> to determine if the VCR is shuttiling forward or
     *         backwards.
     */
    @Override
    public boolean isShuttling() {
        return ((status & STS_SHUTTLE) > 0);
    }

    /**
     * @return True if the VCR is Standying by. (i.e When the CONTROLLED DEVICE
     *         recieves the "20.05: standby on" command and goes into stand-by on
     *         mode. Note: I've never seen the VCR return anything but true for this
     *         status bit. I'm not sure what would cause the VCR to NOT standby
     */
    public boolean isStandingBy() {
        return ((status & STS_STANDBY) > 0);
    }

    /**
     * @return True when the tape stops during stop or search mode and the
     *         CONTROLLED DEVICE goes into the still mode. (Again, not sure exactly,
     *         when this occurs. Use isStopped() instead.
     */
    public boolean isStill() {
        return ((status & STS_STILL) > 0);
    }

    /** @return True if the VCR is in stop mode. */
    @Override
    public boolean isStopped() {
        return ((status & STS_STOP) > 0);
    }

    /** @return True if the end of the tape is reached */
    public boolean isTapeEnd() {
        return ((status & STS_TAPE_END) > 0);
    }

    /** @return True when a trouble such as he tape sticking to the drum occurs. */
    public boolean isTapeTrouble() {
        return ((status & STS_TAPE_TROUBLE) > 0);
    }

    /** @return True when the VCR is in auto edit mode. */
    public boolean isTso() {
        return ((status & STS_TSO) > 0);
    }

    /** @return True when there is no tape threading on the controlled device. */
    public boolean isUnthreaded() {
        return ((status & STS_UNTHREAD) > 0);
    }

    /**
     * @return True when the VCR is in the following modes:<br>
     *         <ol>
     *         <li>VAR SLOW mode</li>
     *         <li>VAR MEMORY PLAY mode</li>
     *         <li>CAPSTAN OVERRIDE mode</li>
     *         </ol>
     */
    public boolean isVarSpeed() {
        return ((status & STS_VARSPEED) > 0);
    }

    private void logStatus() {

        if (log.isDebugEnabled()) {
            StringBuilder msg = new StringBuilder("VCR Status >> (");

            if (isBadCommunication()) {
                msg.append("BadCommunication ");
            }

            if (isCueingUp()) {
                msg.append("CueingUp ");
            }

            if (isFastForwarding()) {
                msg.append("FastForarding ");
            }

            if (isHardwareError()) {
                msg.append("HardwareError ");
            }

            if (isJogging()) {
                msg.append("Jogging ");
            }

            if (isLocal()) {
                msg.append("Local ");
            }

            if (isPlaying()) {
                msg.append("Playing ");
            }

            if (isReverseDirection()) {
                msg.append("ReverseDirection ");
            }

            if (isRewinding()) {
                msg.append("Rewinding ");
            }

            if (isServoLock()) {
                msg.append("ServoLock ");
            }

            if (isServoRef()) {
                msg.append("ServoRef ");
            }

            if (isShuttling()) {
                msg.append("Shuttiling ");
            }

            if (isStandingBy()) {
                msg.append("StandingBy ");
            }

            if (isStill()) {
                msg.append("Still ");
            }

            if (isStopped()) {
                msg.append("Stopped ");
            }

            if (isTapeEnd()) {
                msg.append("TapeEnd ");
            }

            if (isTapeTrouble()) {
                msg.append("TapeTrouble ");
            }

            if (isTso()) {
                msg.append("Tso ");
            }

            if (isUnthreaded()) {
                msg.append("Unthreaded ");
            }

            if (isVarSpeed()) {
                msg.append("VarSpeed ");
            }

            msg.delete(msg.length() - 1, msg.length());
            msg.append(")");
            log.debug(msg.toString());
        }
    }

    public static boolean isStatusReply(byte[] cmd) {
        return (Arrays.equals(cmd, STATUS_REPLY) || Arrays.equals(cmd, OTHER_STATUS_REPLY));
    }

}