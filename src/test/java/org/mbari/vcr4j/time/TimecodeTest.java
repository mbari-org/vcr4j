package org.mbari.vcr4j.time;

import org.junit.Test;

import java.sql.Time;

import static org.junit.Assert.*;
/**
 * @author Brian Schlining
 * @since 2015-09-24T15:37:00
 */
public class TimecodeTest {

    private static final double EPSILON = 0.000000001;

    @Test
    public void testConstructorUsingFrames() {
        Timecode tc = new Timecode(310, 30);
        assertEquals(310, tc.getFrames(), EPSILON);
        assertEquals(30, tc.getFrameRate(), EPSILON);
        assertTrue(tc.getHMSF().isPresent());
        HMSF hmsf = tc.getHMSF().get();
        assertTrue(hmsf.toString().equals("00:00:10:10"));
        assertTrue(tc.isValid());
        assertTrue(tc.isComplete());
    }

    @Test
    public void testConstructorUsingStringAndFrameRate() {
        Timecode tc = new Timecode("00:00:10:10", 30);
        assertEquals(310, tc.getFrames(), EPSILON);
        assertEquals(30, tc.getFrameRate(), EPSILON);
        assertTrue(tc.getHMSF().isPresent());
        HMSF hmsf = tc.getHMSF().get();
        assertTrue(hmsf.toString().equals("00:00:10:10"));
        assertTrue(tc.toString().equals("00:00:10:10"));
        assertTrue(tc.isValid());
        assertTrue(tc.isComplete());
    }

    @Test
    public void testConstructorUsingString() {
        Timecode tc = new Timecode("00:00:10:10");
        assertTrue(Double.isNaN(tc.getFrames()));
        assertTrue(Double.isNaN(tc.getFrameRate()));
        assertTrue(tc.toString().equals("00:00:10:10"));
        assertTrue(tc.getHMSF().isPresent());
        HMSF hmsf = tc.getHMSF().get();
        assertTrue(hmsf.toString().equals("00:00:10:10"));
        assertTrue(tc.isValid());
        assertFalse(tc.isComplete());
    }

    @Test
    public void testConstructorUsingInvalidStringAndFrameRate() {
        Timecode tc = new Timecode(Timecode.EMPTY_TIMECODE_STRING, 30);
        assertTrue(Double.isNaN(tc.getFrames()));
        assertEquals(30, tc.getFrameRate(), EPSILON);
        assertTrue(tc.toString().equals(Timecode.EMPTY_TIMECODE_STRING));
        assertFalse(tc.isValid());
        assertFalse(tc.isComplete());
    }

    @Test
    public void testGetSeconds() {
        Timecode tc0 = new Timecode("00:00:10:00", 29.97);
        assertEquals(10D, tc0.getSeconds(), EPSILON);

        Timecode tc1 = new Timecode(100, 10);
        assertEquals(10D, tc1.getSeconds(), EPSILON);
    }




}
