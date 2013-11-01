package org.mbari.vcr.timer.priority;

import org.mbari.vcr.IVCR;
import org.mbari.vcr.IVCRReply;
import org.mbari.vcr.IVCRState;
import org.mbari.vcr.rs422.DeviceType;
import org.mbari.vcr.rs422.VCRReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Brian Schlining
 * @since 2013-01-22
 */
public class AnnotationPQVCR extends PriorityQueueVCR {

    private final DeviceType deviceType;
    private final Thread timecodeServiceThread;
    private final Function timecodeFn;
    private final Thread statusServiceThread;
    private final Function userbitsFn;
    private final Function statusFn;
    private volatile boolean isRunning = true;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public AnnotationPQVCR(final IVCR vcr) {
        super(vcr);
        super.requestDeviceType();

        IVCRReply vcrReply = getVcrReply();
        if (vcrReply instanceof VCRReply) {
            VCRReply reply = (VCRReply) vcrReply;
            deviceType = DeviceType.getDeviceType(reply.getData());
        }
        else {
            deviceType = DeviceType.UNKNOWN;
        }

        final IVCRState state = vcr.getVcrState();

        // -------------------------------

        timecodeFn = new Function() {
            public void apply() {
                if (state != null && state.isConnected()) {
                    vcr.requestTimeCode();
                }
            }
        };

        timecodeServiceThread = new Thread(new Runnable() {
            public void run() {
                while (isRunning) {
                    addLow(timecodeFn);
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        log.error("Timecode monitor was interrupted");
                    }
                }
            }
        });
        timecodeServiceThread.setDaemon(true);
        timecodeServiceThread.start();

        // -------------------------------

        statusFn = new Function() {
            public void apply() {
                if ((deviceType != null) && deviceType.isVTimecodeSupported() && state.isPlaying()) {
                    vcr.requestVTimeCode();
                }
                else {
                    vcr.requestLTimeCode();
                }
            }
        };

        userbitsFn = new Function() {
            public void apply() {
                vcr.requestUserbits();
            }
        };

        statusServiceThread = new Thread(new Runnable() {
            public void run() {
                while (isRunning) {
                    addLow(statusFn);
                    addHigh(userbitsFn);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    log.error("Timecode monitor was interrupted");
                }
            }
        });
        statusServiceThread.setDaemon(true);
        statusServiceThread.start();
    }

    @Override
    public void kill() {
        isRunning = false;
        super.kill();
    }
}
