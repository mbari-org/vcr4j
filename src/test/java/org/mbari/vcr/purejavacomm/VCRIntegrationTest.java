package org.mbari.vcr.purejavacomm;

import purejavacomm.CommPortIdentifier;
import junit.framework.Assert;
import org.junit.Test;
import org.mbari.comm.BadPortException;
import org.mbari.util.IObserver;
import org.mbari.vcr.IVCRTimecode;
import org.mbari.vcr.VCR4JUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: brian
 * Date: 11/26/13
 * Time: 9:49 AM
 * To change this template use File | Settings | File Templates.
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
                //Assert.fail();
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
