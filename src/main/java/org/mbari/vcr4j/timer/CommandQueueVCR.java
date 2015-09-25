package org.mbari.vcr4j.timer;

import org.mbari.movie.Timecode;
import org.mbari.vcr4j.IVCR;
import org.mbari.vcr4j.IVCRError;
import org.mbari.vcr4j.IVCRReply;
import org.mbari.vcr4j.IVCRState;
import org.mbari.vcr4j.IVCRTimecode;
import org.mbari.vcr4j.IVCRUserbits;
import org.mbari.vcr4j.VCRAdapter;
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
    private Timer timer;

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
        timer.cancel();
        timer = null;
        vcr.disconnect();
    }

    public void kill() {
        // Allow timer to clean up
        timer.cancel();
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
    }

    public void requestUserbits() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.requestUserbits();
            }
        };
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
    }

    public void seekTimecode(final Timecode timecode) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vcr.seekTimecode(timecode);
            }
        };
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
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
        timer.schedule(timerTask, 0);
    }


}
