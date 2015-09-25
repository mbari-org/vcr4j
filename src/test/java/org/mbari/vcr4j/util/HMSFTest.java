package org.mbari.vcr4j.util;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2015-09-24T14:48:00
 */
public class HMSFTest {

    @Test
    public void testValidString() {
        String s = "01:23:45:56";
        Optional<HMSF> hmsf = HMSF.from(s);
        assertTrue(hmsf.isPresent());
    }

    @Test
    public void testInvalidStrings() {
        String s1 = "01:23:45-56";
        Optional<HMSF> hmsf1 = HMSF.from(s1);
        assertFalse(hmsf1.isPresent());

        String s2 = "01:23:45:567";
        Optional<HMSF> hmsf2 = HMSF.from(s2);
        assertFalse(hmsf2.isPresent());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorWithBigMinute() {
        HMSF h1 = new HMSF(0, 60, 0, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorWithNegativeMinute() {
        HMSF h2 = new HMSF(0, -1, 0, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorWithBigSecond() {
        HMSF h1 = new HMSF(0, 0, 60, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorWithNegativeSecond() {
        HMSF h2 = new HMSF(0, 0, -1, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorWithBigHour() {
        HMSF h1 = new HMSF(100, 0, 0, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorWithNegativeHour() {
        HMSF h1 = new HMSF(-100, 0, 0, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorWithNegativeFrame() {
        HMSF h1 = new HMSF(0, 0, 0, -10);
    }

    @Test
    public void testFromFrames() {
        HMSF h1 = HMSF.from(90, 30);
        assertTrue(h1.toString().equals("00:00:03:00"));
    }

    @Test
    public void testGetters() {
        HMSF h1 = new HMSF(1, 2, 3, 4);
        assertEquals(1, h1.getHour());
        assertEquals(2, h1.getMinute());
        assertEquals(3, h1.getSecond());
        assertEquals(4, h1.getFrame());
    }

}
