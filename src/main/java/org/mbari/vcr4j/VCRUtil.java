/*
 * @(#)VCRUtil.java   2009.02.24 at 09:44:49 PST
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

import org.mbari.movie.Timecode;

/**
 * <p>Utility class providing static methods used for supporting VCR operations.</p>
 *
 * @author  : $Author: hohonuuli $
 * @version : $Revision: 246 $
 */
public class VCRUtil {

    /**
     * Constructs ...
     *
     */
    private VCRUtil() {

        // No instantiation
    }

    /**
     * Converts a byte array to anicely formatted string. Useful for debugging using System.out
     * @param b A byte array to view as a string
     * @return The byte array formatted as a string
     */
    public static final String byteArrayToString(byte[] b) {
        StringBuffer sb = new StringBuffer();

        if (b != null) {
            for (int i = 0; i < b.length; i++) {
                sb.append(b[i]);

                if (i < b.length - 1) {
                    sb.append(" ");
                }
            }
        }

        return sb.toString();
    }

    /**
     * Convert an <i>int</i> value to a timecode byte that can be used in sending a seekTimecode command.
     * @param time An integer value representing time (hour, minute, second, or frame)
     * @return The byte representiation of that time as a value recognized by a VCR.
     */
    public static byte intToTime(int time) {
        double timeD = (double) time;
        int tens = (int) Math.floor(timeD / 10D) * 10;
        int ones = time - tens;

        return (byte) (((tens / 10) << 4) + ones);
    }

    /**
     * Convert a Timecode object to a byte array format expected by the RS-422 protocol.
     * @param timecode
     * @return
     */
    public static byte[] timecodeToTime(Timecode timecode) {
        return new byte[] { VCRUtil.intToTime(timecode.getFrame()), VCRUtil.intToTime(timecode.getSecond()),
                            VCRUtil.intToTime(timecode.getMinute()), VCRUtil.intToTime(timecode.getHour()) };
    }
}
