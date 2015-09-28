/*
 * @(#)VCRTimecode.java   2009.02.24 at 09:44:55 PST
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



package org.mbari.vcr4j.rs422;

import java.util.Arrays;
import org.mbari.vcr4j.VCRTimecodeAdapter;
import org.mbari.vcr4j.time.Converters;
import org.mbari.vcr4j.time.HMSF;

/**
 * <p>Class for monitoring the timecode of a sony VCR.</p>
 *
 * @author  Brian Schlining
 */
public class VCRTimecode extends VCRTimecodeAdapter {

    /** <!-- Field description --> */
    public static final byte[] ALT_LTC_TIMECODE = { 0x74, 0x14 };

    /** <!-- Field description --> */
    public static final byte[] LTC_TIMECODE = { 0x74, 0x04 };

    /** <!-- Field description --> */
    public static final byte[] TIMER1_TIMECODE = { 0x74, 0x00 };

    /** <!-- Field description --> */
    public static final byte[] TIMER2_TIMECODE = { 0x74, 0x01 };

    /** <!-- Field description --> */
    public static final byte[] VTC_TIMECODE = { 0x74, 0x06 };

    /**
     */
    private volatile byte[] timecodeBytes = { 0, 0, 0, 0 };

    VCRTimecode() {
        setTimecodeBytes(timecodeBytes);
    }

    /**
     * Convert a byte representing a timecode value to a number
     * @param b A byte of timecode
     * @return The decimal timecode value corresponding to the input byte.
     * @deprecated Don't use. In some cases it will return the wrong frame value. See setTimecodeBytes
     */
    public static int byteToTime(byte b) {
        int i10 = (int) ((b & 0x70) >>> 4) * 10;
        int i1 = (int) (b & 0x0F);

        return i10 + i1;
    }

    public static int byteToHourOrFrame(byte b) {
        int i10 = (int) ((b & 0x30) >>> 4) * 10;
        int i1 = (int) (b & 0x0F);

        return i10 + i1;
    }

    public static int byteToMinuteOrSecond(byte b) {
        int i10 = (int) ((b & 0x70) >>> 4) * 10;
        int i1 = (int) (b & 0x0F);

        return i10 + i1;
    }

    /**
     * @return  the timecodeBytes
     * @uml.property  name="timecodeBytes"
     */
    public byte[] getTimecodeBytes() {
        return timecodeBytes;
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param cmd
     *
     * @return
     */
    public static boolean isTimecodeReply(byte[] cmd) {
        return ((Arrays.equals(cmd, LTC_TIMECODE)) || (Arrays.equals(cmd, VTC_TIMECODE)) ||
                (Arrays.equals(cmd, ALT_LTC_TIMECODE)) || (Arrays.equals(cmd, TIMER1_TIMECODE)) ||
                (Arrays.equals(cmd, TIMER2_TIMECODE)));
    }

    /**
     * Sets the vlue stored in the timecode object. Meant to be called by VCRReply. Calling this method will notify all observers of a timecode change
     * @param timecodeBytes  The byte formatted timecode to store inside a VCRTimecode.
     * @uml.property  name="timecodeBytes"
     */
    protected synchronized void setTimecodeBytes(byte[] timecodeBytes) {
        this.timecodeBytes = timecodeBytes;

//        HMSF hmsf = new HMSF(byteToTime(timecodeBytes[3]), byteToTime(timecodeBytes[2]), byteToTime(timecodeBytes[1]),
//                byteToTime(timecodeBytes[0]));

        HMSF hmsf = new HMSF(byteToHourOrFrame(timecodeBytes[3]),
                byteToMinuteOrSecond(timecodeBytes[2]),
                byteToMinuteOrSecond(timecodeBytes[1]),
                byteToHourOrFrame(timecodeBytes[0]));
        timecodeProperty().set(Converters.toTimecode(hmsf));

        /*
         * This call is no longer needed since the Timecode object contained
         * in the VCRTimecodeAdpater will trigger updates as needed.
         */

        //notifyObservers();
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param i
     *
     * @return
     */
    public static byte timeToByte(int i) {
        int i10 = (int) Math.floor(i / 10);
        int i1 = i - i10 * 10;
        byte b10 = (byte) i10;
        byte b1 = (byte) i1;

        return (byte) ((b10 << 4) + b1);
    }

    /** @return Formatted timecode in HH:MM:SS:FF (i.e. hours:minutes:seconds:frame) */
    @Override
    public String toString() {
        return getTimecode().toString();
    }
}
