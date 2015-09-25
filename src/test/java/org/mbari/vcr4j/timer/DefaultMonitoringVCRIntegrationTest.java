/*
 * DefaultMonitoringVCRTest.java
 * 
 * Created on Jul 25, 2007, 2:25:01 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.vcr4j.timer;

import org.junit.Assert;
import org.junit.Test;
import org.mbari.vcr4j.vcr.VCR4JUtil;

/**
 *
 * @author brian
 */
public class DefaultMonitoringVCRIntegrationTest {

    public DefaultMonitoringVCRIntegrationTest() {
    }
    
    @Test
    public void test1() {
        MonitoringVCR vcr = new DefaultMonitoringVCR();
        try {
            VCR4JUtil.executeCommands(vcr);
        } catch (Exception e) {
            Assert.fail();
        }
    }

}
