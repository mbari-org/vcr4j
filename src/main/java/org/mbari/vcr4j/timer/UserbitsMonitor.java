package org.mbari.vcr4j.timer;

import org.mbari.util.IObserver;
import org.mbari.vcr4j.IVCR;
import org.mbari.vcr4j.IVCRState;
import org.mbari.vcr4j.VCRAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: brian
 * Date: 2013-01-22
 * Time: 3:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserbitsMonitor extends Monitor {

    public static final String MONITOR_NAME = "UserbitsMonitor";
    private final IObserver stateObserver = new IObserver() {

        public void update(Object obj, Object changeCode) {
            IVCRState vcrState = (IVCRState) obj;

            if ((vcrState != null) && !vcrState.isPlaying()) {

                //start();
                setInterval(120);
            }
            else {
                setInterval(500);
            }
        }
    };

    /**
     * Constructs ...
     */
    public UserbitsMonitor() {
        this(new VCRAdapter());
        setInterval(120);
    }

    /**
     * Constructs ...
     *
     * @param vcr
     */
    public UserbitsMonitor(IVCR vcr) {
        super(ReadUserbitsTimerTask.class, MONITOR_NAME, vcr);
    }

    @Override
    public synchronized void setVcr(IVCR vcr) {
        IVCR oldVcr = getVcr();

        if (oldVcr != null) {
            oldVcr.getVcrState().removeObserver(stateObserver);
        }

        vcr.getVcrState().addObserver(stateObserver);
        super.setVcr(vcr);
        start();
    }
}
