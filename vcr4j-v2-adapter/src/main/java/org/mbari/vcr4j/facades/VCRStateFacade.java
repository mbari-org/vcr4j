package org.mbari.vcr4j.facades;

import org.mbari.vcr4j.VCRStateAdapter;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoState;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * @author Brian Schlining
 * @since 2016-03-09T15:43:00
 */
public class VCRStateFacade<S extends VideoState, E extends VideoError> extends VCRStateAdapter {


    private final AtomicReference<S> videoState = new AtomicReference<>();

    public VCRStateFacade(VideoIO<S, E> videoIO) {
        videoIO.getStateObservable().forEach(s -> {
            videoState.set(s);
            os.notify(VCRStateFacade.this, null);
        });
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

    public boolean isPlaying() {
        return apply(() -> videoState.get().isPlaying());
    }

    public boolean isReverseDirection() {
        return apply(() -> videoState.get().isReverseDirection());
    }

    public boolean isRewinding() {
        return apply(() -> videoState.get().isRewinding());
    }

    public boolean isShuttling() {
        return apply(() -> videoState.get().isShuttling());
    }

    public boolean isStopped() {
        return apply(() -> videoState.get().isStopped());
    }

    private boolean apply(Supplier<Boolean> fn) {
        return Optional.ofNullable(fn.get()).orElse(false);
    }

}



