package org.mbari.vcr4j.timer;

import org.mbari.vcr4j.IVCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: brian
 * Date: 2013-01-22
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReadUserbitsTimerTask  extends VCRTimerTask {

    private static final Logger log = LoggerFactory.getLogger(ReadTimecodeTimerTask.class);

    /**
     * Constructs ...
     */
    public ReadUserbitsTimerTask() {
        super();
    }

    public void run() {
        IVCR vcr = getVcr();

        if (vcr != null) {
            vcr.requestUserbits();
        }
    }

    @Override
    public void setVcr(IVCR vcr) {
        super.setVcr(vcr);
    }
}

