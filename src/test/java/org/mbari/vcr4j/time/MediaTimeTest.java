package org.mbari.vcr4j.time;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Brian Schlining
 * @since 2015-09-24T13:51:00
 */
public class MediaTimeTest {

    @Test
    public void testConstructor2Arg() {
        MediaTime mt1 = new MediaTime(300, 10);
        assertTrue(mt1.getValue() == 300);
        assertTrue(mt1.getTimescale() == 10);
        assertTrue(mt1.getSeconds() == 30D);
        assertFalse(mt1.isRounded());
    }

    @Test
    public void testConstructor3Arg() {
        MediaTime mt1 = new MediaTime(300, 10, true);
        assertTrue(mt1.getValue() == 300);
        assertTrue(mt1.getTimescale() == 10);
        assertTrue(mt1.getSeconds() == 30D);
        assertTrue(mt1.isRounded());
    }

    @Test
    public void testFromSecondsNoRounding() {
        MediaTime mt = MediaTime.fromSeconds(30, 10);
        assertTrue(mt.getValue() == 300);
        assertTrue(mt.getTimescale() == 10);
        assertTrue(mt.getSeconds() == 30D);
        assertFalse(mt.isRounded());
    }

    @Test
    public void testFromSecondsWithRounding() {
        MediaTime mt = MediaTime.fromSeconds(30.00001, 10);
        assertTrue(mt.getValue() == 300);
        assertTrue(mt.getTimescale() == 10);
        assertTrue(mt.getSeconds() == 30D);
        assertTrue(mt.isRounded());
    }

}
