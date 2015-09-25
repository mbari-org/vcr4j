/*
 * TestUtil.java
 * 
 * Created on Jul 25, 2007, 2:27:26 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.vcr4j.vcr;

import org.mbari.vcr4j.IVCR;

/**
 *
 * @author brian
 */
public class VCR4JUtil {

    public VCR4JUtil() {
    }
    
    public static void executeCommands(IVCR vcr) throws InterruptedException {
        vcr.requestStatus();
        vcr.requestTimeCode();
        vcr.play();
        vcr.requestStatus();
        vcr.requestTimeCode();
        Thread.sleep(3000);
        vcr.stop();
        vcr.requestLTimeCode();
        vcr.requestLUserbits();
        vcr.requestStatus();
        vcr.requestUserbits();
        vcr.requestVTimeCode();
        vcr.requestVUserbits();
        vcr.fastForward();
        vcr.requestTimeCode();
        Thread.sleep(2000);
        vcr.rewind();
        vcr.requestTimeCode();
        Thread.sleep(2000);
        vcr.stop();
    }

}
