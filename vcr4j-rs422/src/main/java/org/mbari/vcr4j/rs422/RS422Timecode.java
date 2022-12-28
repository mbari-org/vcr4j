package org.mbari.vcr4j.rs422;

import java.util.Arrays;

import org.mbari.vcr4j.rs422.commands.CommandToBytes;
import org.mbari.vcr4j.time.Converters;
import org.mbari.vcr4j.time.HMSF;
import org.mbari.vcr4j.time.Timecode;
import org.mbari.vcr4j.util.Preconditions;

public class RS422Timecode {

    public static final RS422Timecode ZERO = new RS422Timecode(CommandToBytes.timecodeToBytes(new Timecode("00:00:00:00")));
    public static final byte[] ALT_LTC_TIMECODE = { 0x74, 0x14 };
    public static final byte[] LTC_TIMECODE     = { 0x74, 0x04 };
    public static final byte[] TIMER1_TIMECODE  = { 0x74, 0x00 };
    public static final byte[] TIMER2_TIMECODE  = { 0x74, 0x01 };
    public static final byte[] VTC_TIMECODE     = { 0x74, 0x06 };
    private final byte[]       timecodeBytes;
    private final Timecode timecode;

    public RS422Timecode(byte[] timecodeBytes) {
        Preconditions.checkArgument(timecodeBytes != null, "timecodeBytes arg can not be null");
        this.timecodeBytes = timecodeBytes;

        HMSF hmsf = new HMSF(byteToHourOrFrame(timecodeBytes[3]),
                             byteToMinuteOrSecond(timecodeBytes[2]),
                             byteToMinuteOrSecond(timecodeBytes[1]),
                             byteToHourOrFrame(timecodeBytes[0]));

        timecode = Converters.toTimecode(hmsf);
    }

    public static int byteToHourOrFrame(byte b) {
        int i10 = ((b & 0x30) >>> 4) * 10;
        int i1  = (b & 0x0F);

        return i10 + i1;
    }

    public static int byteToMinuteOrSecond(byte b) {
        int i10 = ((b & 0x70) >>> 4) * 10;
        int i1  = (b & 0x0F);

        return i10 + i1;
    }

    /**
     * Convert a byte representing a timecode value to a number
     * @param b A byte of timecode
     * @return The decimal timecode value corresponding to the input byte.
     * @deprecated Don't use. In some cases it will return the wrong frame value. See setTimecodeBytes
     */
    public static int byteToTime(byte b) {
        int i10 = (int) ((b & 0x70) >>> 4) * 10;
        int i1  = (int) (b & 0x0F);

        return i10 + i1;
    }

    public static byte timeToByte(int i) {
        int  i10 = (int) Math.floor(i / 10);
        int  i1  = i - i10 * 10;
        byte b10 = (byte) i10;
        byte b1  = (byte) i1;

        return (byte) ((b10 << 4) + b1);
    }

    /** @return Formatted timecode in HH:MM:SS:FF (i.e. hours:minutes:seconds:frame) */
    @Override
    public String toString() {
        return getTimecode().toString();
    }

    public Timecode getTimecode() {
        return timecode;
    }

    public byte[] getTimecodeBytes() {
        return timecodeBytes;
    }

    public static boolean isTimecodeReply(byte[] cmd) {
        return ((Arrays.equals(cmd, LTC_TIMECODE))
                || (Arrays.equals(cmd, VTC_TIMECODE))
                || (Arrays.equals(cmd, ALT_LTC_TIMECODE))
                || (Arrays.equals(cmd, TIMER1_TIMECODE))
                || (Arrays.equals(cmd, TIMER2_TIMECODE)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RS422Timecode that = (RS422Timecode) o;

        return Arrays.equals(timecodeBytes, that.timecodeBytes);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(timecodeBytes);
    }
}
