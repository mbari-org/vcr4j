package org.mbari.vcr4j.rs422;

import org.mbari.util.NumberUtilities;
import org.mbari.vcr4j.util.Preconditions;

import java.util.Arrays;

public class RS422Userbits {
    
    public static final byte[] LTC_USERBITS = { 0x74, 0x05 };

    public static final byte[] LTC_USERBITS_HOLD = { 0x74, 0x15 };

    public static final byte[] VTC_USERBITS = { 0x74, 0x07 };

    public static final byte[] VTC_USERBITS_HOLD = { 0x74, 0x17 };
    
    private final byte[] userbits;
    
    public RS422Userbits(byte[] userbits) {
        Preconditions.checkArgument(userbits != null, "userbits arg can not be null");
        this.userbits = userbits;
    }
    
    /**
     * @param cmd
     *
     * @return
     */
    public static boolean isUserbitsReply(byte[] cmd) {
        return ((Arrays.equals(cmd, LTC_USERBITS)) || (Arrays.equals(cmd, VTC_USERBITS)));
    }

    /**
     * Checks to see if the reply for a users bits command is telling you that the value
     * has been held. For example, if your are shuttling at a speed where the VUB
     * can't be read correctly, then it will return this command where the value
     * will be the same as you last VUB request.
     * @param cmd
     * @return
     */
    public static boolean isUserbitsHoldReply(byte[] cmd) {
        return ((Arrays.equals(cmd, LTC_USERBITS_HOLD)) || (Arrays.equals(cmd, VTC_USERBITS_HOLD)));
    }

    public byte[] getUserbits() {
        return userbits;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(userbits);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RS422Userbits that = (RS422Userbits) o;

        return Arrays.equals(userbits, that.userbits);

    }
}