package org.mbari.vcr4j.timer;

import org.mbari.vcr4j.IVCR;
import org.mbari.vcr4j.IVCRReply;
import org.mbari.vcr4j.IVCRState;
import org.mbari.vcr4j.rs422.DeviceType;
import org.mbari.vcr4j.rs422.VCRReply;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Brian Schlining
 * @since 2011-01-19
 */
public class AnnotationQueueVCR extends CommandQueueVCR {


    private final DeviceType deviceType;

    public AnnotationQueueVCR(IVCR vcr) {
        super(vcr);
        vcr.requestDeviceType();
        IVCRReply vcrReply = (IVCRReply) getVcrReply();
        if (vcrReply instanceof VCRReply) {
            VCRReply reply = (VCRReply) vcrReply;
            deviceType = DeviceType.getDeviceType(reply.getData());
        }
        else {
            deviceType = DeviceType.UNKNOWN;
        }
        initialize();
    }

    private void initialize() {
        Timer timer = getTimer();

        // Monitor status
        TimerTask statusMonitor = new TimerTask() {
            final IVCRState vcrState = getVcr().getVcrState();
            @Override
            public void run() {
                if (vcrState != null && vcrState.isConnected()) {
                    requestStatus();
                }
            }
        };
        timer.schedule(statusMonitor, 0, 1000);

        // Monitor timecode
        TimerTask timecodeMonitor = new TimerTask() {
            final IVCRState vcrState = getVcr().getVcrState();
            @Override
            public void run() {
                if (vcrState != null) {
                    if ((deviceType != null) && deviceType.isVTimecodeSupported() && vcrState.isPlaying()) {
                        requestVTimeCode();
                    }
                    else {
                        requestLTimeCode();
                    }
                }
            }
        };
        timer.schedule(timecodeMonitor, 0, 40);

        TimerTask userbitsMonitor = new TimerTask() {
            @Override
            public void run() {
                requestUserbits();
            }
        };
        timer.schedule(userbitsMonitor, 0, 500);

        // Write userbits
//        TimerTask writeTimeMonitor = new TimerTask() {
//            final IVCRState vcrState = getVcr().getVcrState();
//            @Override
//            public void run() {
//                if (vcrState.isRecording()) {
//                    int timeSeconds = (int) (System.currentTimeMillis() / 1000L);
//                    byte[] b = NumberUtilities.toByteArray(timeSeconds, true);
//                    presetUserbits(b);
//                }
//            }
//        };
//        timer.schedule(writeTimeMonitor, 0, 500);

    }
}
