/*
 * VCRTest.java
 * JUnit 4.x based test
 *
 * Created on 2 November 2007, 12:08
 */

package org.mbari.vcr.udp02;

import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mbari.util.IObserver;
import org.mbari.util.NumberUtilities;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.IVCRTimecode;
import org.mbari.vcr.IVCRUserbits;
import org.mbari.vcr.VCRAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * For testing CSIRO's VCR Proxy that broadcasts over UDP. This isn't a test
 * in that it never fails. However, it's good for running to verify if the 
 * the class is working by looking at the output. If there are no Exceptions
 * it's probably working
 * @author sch482
 */
public class VCRTest {
    
    private static final Logger log = LoggerFactory.getLogger(VCRTest.class);
    
    private IVCR vcr;
    
    public VCRTest() {
        
    }

    @Before
    public void setUp() throws Exception {
        try {
            vcr = new VCR(22228);
        }
        catch (Exception e) {
            log.error("Failed to open UDP VCR Proxy", e);
            vcr = new VCRAdapter();
        }
        final IVCRUserbits vcrUserbits = vcr.getVcrUserbits();
        vcrUserbits.addObserver(new IObserver() {

            public void update(Object obj, Object changeCode) {
                final byte[] userbits = vcrUserbits.getUserbits();
                log.info(">>> Received userbits = " + NumberUtilities.toHexString(userbits) +
                        " via UDP");
                int epoch = NumberUtilities.toInt(userbits, true);
                log.info("Userbits converted to epoch seconds = " + epoch);
                log.info("Userbits converted to date = " + new Date((long) epoch * 1000L));
            }
        });
        
        final IVCRTimecode vcrTimecode = vcr.getVcrTimecode();
        vcrTimecode.addObserver(new IObserver() {

            public void update(Object obj, Object changeCode) {
                log.info(">>> Received timecode of " + vcrTimecode.getTimecode() + " via UDP");
            } 
        });
    }

    @After
    public void tearDown() throws Exception {
        vcr.disconnect();
    }
    
    @Test
    public void testAtCSIRO() {
        log.info("Setting timecode");
        vcr.getVcrTimecode().getTimecode().setTimecode("00:00:00:00");
        log.info("Setting userbits");
        vcr.getVcrUserbits().setUserbits(new byte[4]);
        log.info("Running UDP tests");
        try {
            for (int i = 0; i < 10; i++) {
                vcr.requestTimeCode();
                vcr.requestUserbits();
                Thread.sleep(500);
            }
        }
        catch (Exception e) {
            log.error("An Exception occured", e);
        }
    }
    
}
