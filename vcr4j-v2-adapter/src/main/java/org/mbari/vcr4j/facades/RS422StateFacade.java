package org.mbari.vcr4j.facades;

import org.mbari.vcr4j.VCRStateAdapter;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.rs422.RS422Error;
import org.mbari.vcr4j.rs422.RS422State;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * @author Brian Schlining
 * @since 2016-03-10T16:10:00
 */
public class RS422StateFacade<S extends RS422State, E extends RS422Error> extends VCRStateAdapter {

    private final AtomicReference<S> videoState = new AtomicReference<>();

    public RS422StateFacade(VideoIO<S, E> videoIO) {
        videoIO.getStateObservable().forEach(s -> {
            videoState.set(s);
            os.notify(RS422StateFacade.this, null);
        });
    }

    public boolean isBadCommunication() {
        return apply(() -> videoState.get().isBadCommunication());
    }

    public boolean isConnected() {
        return apply(() -> videoState.get().isConnected());
    }

    public boolean isCueingUp() {
        return apply(() -> videoState.get().isCueingUp());
    }

    public boolean isFastForwarding() {
        return apply(() -> videoState.get().isFastForwarding());
    }

    public boolean isHardwareError() {
        return apply(() -> videoState.get().isHardwareError());
    }

    public boolean isJogging() {
        return apply(() -> videoState.get().isJogging());
    }

    public boolean isLocal() {
        return apply(() -> videoState.get().isLocal());
    }

    public boolean isPlaying() {
        return apply(() -> videoState.get().isPlaying());
    }

    public boolean isRecording() {
        return apply(() -> videoState.get().isRecording());
    }

    public boolean isReverseDirection() {
        return apply(() -> videoState.get().isReverseDirection());
    }

    public boolean isRewinding() {
        return apply(() -> videoState.get().isRewinding());
    }

    public boolean isServoLock() {
        return apply(() -> videoState.get().isServoLock());
    }

    public boolean isServoRef() {
        return apply(() -> videoState.get().isServoRef());
    }

    public boolean isShuttling() {
        return apply(() -> videoState.get().isShuttling());
    }

    public boolean isStandingBy() {
        return apply(() -> videoState.get().isStandingBy());
    }

    public boolean isStill() {
        return apply(() -> videoState.get().isStill());
    }

    public boolean isStopped() {
        return apply(() -> videoState.get().isStopped());
    }

    public boolean isTapeEnd() {
        return apply(() -> videoState.get().isTapeEnd());
    }

    public boolean isTapeTrouble() {
        return apply(() -> videoState.get().isTapeTrouble());
    }

    public boolean isTso() {
        return apply(() -> videoState.get().isTso());
    }

    public boolean isUnthreaded() {
        return apply(() -> videoState.get().isUnthreaded());
    }

    public boolean isVarSpeed() {
        return apply(() -> videoState.get().isVarSpeed());
    }

    private boolean apply(Supplier<Boolean> fn) {
        boolean v;
        try {
            v = Optional.ofNullable(fn.get()).orElse(false);
        }
        catch (Exception e) {
            v = false;
        }
        return v;
    }
}
