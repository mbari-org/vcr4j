package org.mbari.vcr4j.rs422.commands;

/*-
 * #%L
 * vcr4j-rs422
 * %%
 * Copyright (C) 2008 - 2026 Monterey Bay Aquarium Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.SeekTimecodeCmd;
import org.mbari.vcr4j.commands.SeekTimestampCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.rs422.util.NumberUtilities;
import org.mbari.vcr4j.time.HMSF;
import org.mbari.vcr4j.time.Timecode;



/**
 * This utility class converts VideoCommand objects to the appropriate byte commands
 * used by RS422.
 *
 * @author Brian Schlining
 * @since 2016-01-28T14:25:00
 */
public class  CommandToBytes {

    private static final System.Logger log = System.getLogger(CommandToBytes.class.getName());

    private static final HMSF HMSF_ZERO = new HMSF(0, 0, 0, 0);

    private CommandToBytes() {
        // no instantiation
    }


    public static byte[] apply(VideoCommand cmd) {
        byte[] bytes;
        if (cmd instanceof VideoCommands) {
            bytes = toBytes((VideoCommands) cmd);
        }
        else if (cmd instanceof RS422VideoCommands) {
            bytes = toBytes((RS422VideoCommands) cmd);
        }
        else if (cmd instanceof PresetTimecodeCmd) {
            bytes = toBytes((PresetTimecodeCmd) cmd);
        }
        else if (cmd instanceof PresetUserbitsCmd) {
            bytes = toBytes((PresetUserbitsCmd) cmd);
        }
        else if (cmd instanceof SeekElapsedTimeCmd) {
            bytes = toBytes((SeekElapsedTimeCmd) cmd);
        }
        else if (cmd instanceof SeekTimecodeCmd) {
            bytes = toBytes((SeekTimecodeCmd) cmd);
        }
        else if (cmd instanceof SeekTimestampCmd) {
            bytes = toBytes((SeekTimestampCmd) cmd);
        }
        else if (cmd instanceof ShuttleCmd) {
            bytes = toBytes((ShuttleCmd) cmd);
        }
        else {
            bytes = RS422ByteCommands.UNDEFINED.getBytes();
        }
        return bytes;
    }


    public static byte[] toBytes(VideoCommands cmd) {
        byte[] bytes = RS422ByteCommands.UNDEFINED.getBytes();
        switch (cmd) {
            case FAST_FORWARD:
                bytes = RS422ByteCommands.FAST_FWD.getBytes();
                break;
            case PAUSE:
                bytes = RS422ByteCommands.PAUSE.getBytes();
                break;
            case PLAY:
                bytes = RS422ByteCommands.PLAY_FWD.getBytes();
                break;
            case REQUEST_DEVICE_TYPE:
                bytes = RS422ByteCommands.DEVICE_TYPE_REQUEST.getBytes();
                break;
            case REQUEST_ELAPSED_TIME:
                // TODO not directly supported.
                log.log(System.Logger.Level.DEBUG, String.format("'%s' is not supported.", cmd.getName()));
                break;
            case REQUEST_INDEX:
                bytes = RS422ByteCommands.GET_TIMECODE.getBytes();
                break;
            case REQUEST_STATUS:
                bytes = RS422ByteCommands.GET_STATUS.getBytes();
                break;
            case REQUEST_TIMECODE:
                bytes = RS422ByteCommands.GET_TIMECODE.getBytes();
                break;
            case REQUEST_TIMESTAMP:
                // TODO not directly supported.
                break;
            case REWIND:
                bytes = RS422ByteCommands.REWIND.getBytes();
                break;
            case STOP:
                bytes = RS422ByteCommands.STOP.getBytes();
                break;
        }
        return bytes;
    }

    public static byte[] toBytes(RS422VideoCommands cmd) {
        return cmd.getValue().getBytes();
    }

    public static byte[] toBytes(PresetTimecodeCmd cmd) {
        byte[] bytes = RS422ByteCommands.PRESET_TIMECODE.getBytes();
        byte[] timecode = timecodeToBytes(cmd.getValue());
        System.arraycopy(timecode, 0, bytes, 2, timecode.length);
        return bytes;
    }

    public static byte[] toBytes(PresetUserbitsCmd cmd) {
        byte[] bytes = RS422ByteCommands.PRESET_USERBITS.getBytes();
        byte[] userbits = cmd.getValue();
        System.arraycopy(userbits, 0, bytes, 2, userbits.length);
        return bytes;
    }

    public static byte[] toBytes(SeekElapsedTimeCmd cmd) {
        // TODO not directly supported
        log.log(System.Logger.Level.DEBUG, String.format("'%s' is not supported.", cmd.getName()));
        return RS422ByteCommands.UNDEFINED.getBytes();
    }

    public static byte[] toBytes(SeekTimecodeCmd cmd) {
        byte[] bytes = RS422ByteCommands.SONY_SEEK_TIMECODE.getBytes();
        byte[] timecode = timecodeToBytes(cmd.getValue());
        System.arraycopy(timecode, 0, bytes, 2, timecode.length);
        return bytes;
    }

    public static byte[] toBytes(SeekTimestampCmd cmd) {
        // TODO not directly supported
        log.log(System.Logger.Level.DEBUG, String.format("'%s' is not supported.", cmd.getName()));
        return RS422ByteCommands.UNDEFINED.getBytes();
    }

    public static byte[] toBytes(ShuttleCmd cmd) {
        // speed value between 0 (slow) and 255 (fast)
        int speed = (int) Math.round(cmd.getValue() * 255);

        byte[] bytes;
        if (speed >= 0) {
            bytes = RS422ByteCommands.SHUTTLE_FWD.getBytes();
        }
        else {
            speed = -1 * speed; // Speed has to be positive direction
            bytes = RS422ByteCommands.SHUTTLE_REV.getBytes();
        }

        byte[] byteSpeed = NumberUtilities.toByteArray(speed);
        bytes[2] = byteSpeed[3];    // speed is between 0-255, ignore other bytes
        return bytes;

    }

    public static byte[] timecodeToBytes(Timecode timecode) {
        HMSF hmsf = timecode.getHMSF().orElse(HMSF_ZERO);

        return new byte[] { intToTime(hmsf.getFrame()), intToTime(hmsf.getSecond()),
                intToTime(hmsf.getMinute()), intToTime(hmsf.getHour()) };

    }

    /**
     * Converts a byte array to a nicely formatted string. Useful for debugging using System.out
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

}
