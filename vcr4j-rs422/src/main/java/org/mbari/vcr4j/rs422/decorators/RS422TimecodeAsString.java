package org.mbari.vcr4j.rs422.decorators;

import mbarix4j.util.NumberUtilities;
import org.mbari.vcr4j.rs422.RS422Timecode;

/**
 * @author Brian Schlining
 * @since 2016-02-03T12:13:00
 */
public class RS422TimecodeAsString {
    private final String string;

    public RS422TimecodeAsString(RS422Timecode timecode) {
        string = "{name:'RS422Timecode',class='" + timecode.getClass().getName() +
                "',timecode='" + timecode.getTimecode() + "',bytes=0x" +
                NumberUtilities.toHexString(timecode.getTimecodeBytes()) + "}";
    }

    @Override
    public String toString() {
        return string;
    }
}
