package org.mbari.vcr4j.timer;

import org.mbari.vcr4j.IVCR;
import org.mbari.vcr4j.IVCRError;
import org.mbari.vcr4j.IVCRReply;
import org.mbari.vcr4j.IVCRState;
import org.mbari.vcr4j.IVCRTimecode;
import org.mbari.vcr4j.IVCRUserbits;
import org.mbari.vcr4j.VCRAdapter;
import org.mbari.vcr4j.time.Timecode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A VCR that contains an internal {@link Timer} for scheduling commands.
 * Useful for running background processes such as timecode and status monitoring.
 * Commands are placed on a queue so that they are run in the order that they are called
 * in
 * @author Brian Schlining
 * @since 2011-01-19
 */
public class CommandQueueVCR implements IVCR {

    public static final IVCR DUMMY_VCR = new VCRAdapter();
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final IVCR vcr;
    private volatile Timer timer;

    public CommandQueueVCR(IVCR vcr) {
        this.vcr = vcr;
    }

    public Timer getTimer() {
        if (timer == null) {
            timer = new Timer(getClass().getSimpleName() + "-" + System.currentTimeMillis(), true);
        }
        return timer;
    }

    /**
     * Method description
     *
     */
    public void disconnect() {
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
        vcr.disconnect();
    }

    public void kill() {
        // Allow timer to clean up
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
        vcr.kill();
    }

    /** Eject the tape from the VTR */
    public void eject() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.stop();
                vcr.releaseTape();
                vcr.eject();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     */
    public void fastForward() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.fastForward();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getConnectionName() {
        return vcr.getConnectionName();
    }


    /**
     *     @return  the vcr
     *     @uml.property  name="vcr"
     */
    public IVCR getVcr() {
        return vcr;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public IVCRError getVcrError() {
        return vcr.getVcrError();
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public IVCRReply getVcrReply() {
        return vcr.getVcrReply();
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public IVCRState getVcrState() {
        return vcr.getVcrState();
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public IVCRTimecode getVcrTimecode() {
        return vcr.getVcrTimecode();
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public IVCRUserbits getVcrUserbits() {
        return vcr.getVcrUserbits();
    }

    /**
     * Method description
     *
     */
    public void pause() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.pause();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     */
    public void play() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.play();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     *
     * @param timecode
     */
    public void presetTimecode(final byte[] timecode) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.presetTimecode(timecode);
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     *
     * @param userbits
     */
    public void presetUserbits(final byte[] userbits) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.presetUserbits(userbits);
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     */
    public void record() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.record();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     */
    public void releaseTape() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.releaseTape();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     */
    public void removeAllObservers() {
        vcr.removeAllObservers();
    }

    /**
     * Method description
     *
     */
    public void requestDeviceType() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.requestDeviceType();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     */
    public void requestLTimeCode() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.requestLTimeCode();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     */
    public void requestLUserbits() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.requestLUserbits();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     */
    public void requestLocalDisable() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.requestLocalDisable();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     */
    public void requestLocalEnable() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.requestLocalEnable();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     */
    public void requestStatus() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.requestStatus();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     */
    public void requestTimeCode() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.requestTimeCode();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    public void requestUserbits() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.requestUserbits();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     */
    public void requestVTimeCode() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.requestVTimeCode();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     */
    public void requestVUserbits() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.requestVUserbits();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     */
    public void rewind() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.rewind();
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     *
     * @param timecode
     */
    public void seekTimecode(final byte[] timecode) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.seekTimecode(timecode);
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     *
     * @param timecode
     */
    public void seekTimecode(final int timecode) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.seekTimecode(timecode);
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    public void seekTimecode(final Timecode timecode) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.seekTimecode(timecode);
            }
        };
        getTimer().schedule(timerTask, 0);
    }


    /**
     * Method description
     *
     *
     * @param speed
     */
    public void shuttleForward(final int speed) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.shuttleForward(speed);
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     *
     * @param speed
     */
    public void shuttleReverse(final int speed) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.shuttleReverse(speed);
            }
        };
        getTimer().schedule(timerTask, 0);
    }

    /**
     * Method description
     *
     */
    public void stop() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.stop();
            }
        };
        getTimer().schedule(timerTask, 0);
    }


}
