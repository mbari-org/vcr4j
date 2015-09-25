package org.mbari.vcr4j.timer.priority;

import org.mbari.movie.Timecode;
import org.mbari.vcr4j.IVCR;
import org.mbari.vcr4j.IVCRError;
import org.mbari.vcr4j.IVCRReply;
import org.mbari.vcr4j.IVCRState;
import org.mbari.vcr4j.IVCRTimecode;
import org.mbari.vcr4j.IVCRUserbits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Brian Schlining
 * @since 2013-01-22
 */
public class PriorityQueueVCR implements IVCR {

    private final IVCR vcr;

    private Logger log = LoggerFactory.getLogger(getClass());

    public interface Function {
        void apply();
    }

    private Function fastForwardFn = new Function() {
        public void apply() {
            vcr.fastForward();
        }
    };

    private Function playFn = new Function() {
        public void apply() {
            vcr.play();
        }
    };

    private Function recordFn = new Function() {
        public void apply() {
            vcr.record();
        }
    };

    private Function requestDeviceTypeFn = new Function() {
        public void apply() {
            vcr.requestDeviceType();
        }
    };

    private Function requestLTimeCodeFn = new Function() {
        public void apply() {
            vcr.requestLTimeCode();
        }
    };

    private Function requestLUserbitsFn = new Function() {
        public void apply() {
            vcr.requestLUserbits();
        }
    };

    private Function requestLocalDisableFn = new Function() {
        public void apply() {
            vcr.requestLocalDisable();
        }
    };

    private Function requestLocalEnableFn = new Function() {
        public void apply() {
            vcr.requestLocalEnable();
        }
    };

    private Function requestStatusFn = new Function() {
        public void apply() {
            vcr.requestStatus();
        }
    };

    private Function requestTimeCodeFn = new Function() {
        public void apply() {
            vcr.requestTimeCode();
        }
    };

    private Function requestUserbitsFn = new Function() {
        public void apply() {
            vcr.requestUserbits();
        }
    };

    private Function requestVTimecodeFn = new Function() {
        public void apply() {
            vcr.requestVTimeCode();
        }
    };

    private Function requestVUserbitsFn = new Function() {
        public void apply() {
            vcr.requestVUserbits();
        }
    };

    private Function rewindFn = new Function() {
        public void apply() {
            vcr.rewind();
        }
    };

    private Function stopFn = new Function() {
        public void apply() {
            vcr.stop();
        }
    };


    private final LinkedBlockingDeque<Function> highPriorityQueue = new LinkedBlockingDeque<Function>(10);
    protected final LinkedBlockingDeque<Function> lowPriorityQueue = new LinkedBlockingDeque<Function>(100);
    private final Thread serviceThread;
    private volatile boolean isRunning = true;


    public PriorityQueueVCR(IVCR vcr) {
        if (vcr == null) {
            throw new IllegalArgumentException("VCR argument can not be null");
        }
        this.vcr = vcr;

        serviceThread = new Thread(new Runnable() {
            public void run() {
                while (isRunning) {
                    Function fn = highPriorityQueue.poll();
                    if (fn == null) {
                        fn = lowPriorityQueue.poll();
                    }
                    if (fn != null) {
                        fn.apply();
                    }
                }
            }
        });
        serviceThread.setDaemon(true);
        serviceThread.start();
    }

    public IVCR getVcr() {
        return vcr;
    }

    void addHigh(Function fn) {
        boolean ok = highPriorityQueue.offer(fn);
        if (!ok) {
            log.warn("Failed to executed a high priorite function because the queue was full");
        }
    }

    void addLow(Function fn) {
        boolean ok = lowPriorityQueue.offer(fn);
        if (!ok) {
            log.warn("Failed to executed a low priority function because the queue was full");
        }
    }

    public void disconnect() {
        vcr.disconnect();
    }

    public void eject() {
        vcr.eject();
    }

    public void fastForward() {
        addHigh(fastForwardFn);
    }

    public String getConnectionName() {
        return vcr.getConnectionName();
    }

    public IVCRError getVcrError() {
        return vcr.getVcrError();
    }

    public IVCRReply getVcrReply() {
        return vcr.getVcrReply();
    }

    public IVCRState getVcrState() {
        return vcr.getVcrState();
    }

    public IVCRTimecode getVcrTimecode() {
        return vcr.getVcrTimecode();
    }

    public IVCRUserbits getVcrUserbits() {
        return vcr.getVcrUserbits();
    }

    public void pause() {
        // do nothing
    }

    public void play() {
        addHigh(playFn);
    }

    public void presetTimecode(final byte[] timecode) {
        Function presetTimecodeFn = new Function() {
            public void apply() {
                vcr.presetTimecode(timecode);
            }
        };
        addHigh(presetTimecodeFn);
    }

    public void presetUserbits(final byte[] userbits) {
        Function presetUserbitsFn = new Function() {
            public void apply() {
                vcr.presetUserbits(userbits);
            }
        };
        addHigh(presetUserbitsFn);
    }

    public void record() {
        addHigh(recordFn);
    }

    public void releaseTape() {
        vcr.releaseTape();
    }

    public void removeAllObservers() {
        vcr.removeAllObservers();
    }

    public void requestDeviceType() {
        addHigh(requestDeviceTypeFn);
    }

    public void requestLTimeCode() {
        addHigh(requestLTimeCodeFn);
    }

    public void requestLUserbits() {
        addHigh(requestLUserbitsFn);
    }

    public void requestLocalDisable() {
        addHigh(requestLocalDisableFn);
    }

    public void requestLocalEnable() {
        addHigh(requestLocalEnableFn);
    }

    public void requestStatus() {
        addHigh(requestStatusFn);
    }

    public void requestTimeCode() {
        addHigh(requestTimeCodeFn);
    }

    public void requestUserbits() {
        addHigh(requestUserbitsFn);
    }

    public void requestVTimeCode() {
        addHigh(requestVTimecodeFn);
    }

    public void requestVUserbits() {
        addHigh(requestVUserbitsFn);
    }

    public void rewind() {
        addHigh(rewindFn);
    }

    public void seekTimecode(final byte[] timecode) {
        Function fn = new Function() {
            public void apply() {
                vcr.seekTimecode(timecode);
            }
        };
        addHigh(fn);
    }

    public void seekTimecode(final int timecode) {
        Function fn = new Function() {
            public void apply() {
                vcr.seekTimecode(timecode);
            }
        };
        addHigh(fn);
    }

    public void seekTimecode(final Timecode timecode) {
        Function fn = new Function() {
            public void apply() {
                vcr.seekTimecode(timecode);
            }
        };
        addHigh(fn);

    }

    public void shuttleForward(final int speed) {
        Function fn = new Function() {
            public void apply() {
                vcr.shuttleForward(speed);
            }
        };
        addHigh(fn);
    }

    public void shuttleReverse(final int speed) {
        Function fn = new Function() {
            public void apply() {
                vcr.shuttleReverse(speed);
            }
        };
        addHigh(fn);
    }

    public void stop() {
        addHigh(stopFn);
    }

    public void kill() {
        isRunning = false;
        vcr.kill();
    }
}
