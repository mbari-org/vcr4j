package org.mbari.vcr4j.time;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2015-09-24T16:23:00
 */
public class ConvertersTest {

    private static final double EPSILON = 0.000000001;

    @Test
    public void toValidMediaTimeTest() {
        Timecode tc = new Timecode("00:00:30:00", 30);
        Optional<MediaTime> mtOpt = Converters.toMediaTime(tc);
        assertTrue(mtOpt.isPresent());
        MediaTime mt = mtOpt.get();
        assertFalse(mt.isRounded());
        assertEquals(30D, mt.getSeconds(), EPSILON);
        assertEquals(30D, mt.getTimescale(), EPSILON);
        assertEquals(900D, mt.getValue(), EPSILON);
    }

    @Test
    public void toInvalidMediaTimeTest() {
        Timecode tc = new Timecode("00:00:30:00");
        Optional<MediaTime> mtOpt = Converters.toMediaTime(tc);
        assertFalse(mtOpt.isPresent());
    }

    @Test
    public void toTimecodeTest() {
        MediaTime mt = new MediaTime(900, 30);
        Timecode tc = Converters.toTimecode(mt);
        assertEquals("00:00:30:00", tc.toString());
    }
}
