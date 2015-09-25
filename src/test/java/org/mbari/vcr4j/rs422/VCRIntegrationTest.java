/*
 * VCRIntegrationTest.java
 * JUnit 4.x based test
 *
 * Created on May 14, 2007, 11:31 AM
 */

package org.mbari.vcr4j.rs422;

import gnu.io.CommPortIdentifier;
import java.io.IOException;
import java.util.Set;
import junit.framework.Assert;
import org.junit.Test;
import org.mbari.comm.BadPortException;
import org.mbari.util.IObserver;
import org.mbari.vcr4j.IVCRTimecode;
import org.mbari.vcr4j.vcr.VCR4JUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brian
 */
public class VCRIntegrationTest {
    
    private static final Logger log = LoggerFactory.getLogger(VCRIntegrationTest.class);
    
    public VCRIntegrationTest() {
    }

    @Test
    public void test01() {
        Set<CommPortIdentifier> ports = CommUtil.getAvailableSerialPorts();
        for (CommPortIdentifier port: ports) {
            try {
                log.debug("Testing port " + port.getName());
                run(port.getName());
            } catch (Exception ex) {
                log.error("An exception occured", ex);
                Assert.fail();
            } 
        }
    }
    
    public void run(String serialPortName) throws BadPortException, IOException, InterruptedException {
        VCR vcr = new VCR(serialPortName);
        vcr.getVcrTimecode().addObserver(new IObserver() {

            public void update(Object obj, Object changeCode) {
                IVCRTimecode vcrTimecode = (IVCRTimecode) obj;
                log.debug("VCR Timecode >> " + vcrTimecode.getTimecode());
            }
            
        });
        VCR4JUtil.executeCommands(vcr);
    }
    
}
