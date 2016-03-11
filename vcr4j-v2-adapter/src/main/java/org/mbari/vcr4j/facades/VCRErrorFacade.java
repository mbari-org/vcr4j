package org.mbari.vcr4j.facades;

import org.mbari.vcr4j.VCRErrorAdapter;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.rs422.RS422Error;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * @author Brian Schlining
 * @since 2016-03-10T17:01:00
 */
public class VCRErrorFacade<S extends VideoState, E extends VideoError> extends VCRErrorAdapter {

    private final AtomicReference<E> videoError = new AtomicReference<>();

    public VCRErrorFacade(VideoIO<S, E> videoIO) {
        videoIO.getErrorObservable().forEach(e -> {
            videoError.set(e);
            oc.notify(VCRErrorFacade.this, null);
        });
    }


    @Override
    public boolean isOK() {
        return apply(() -> !videoError.get().hasError());
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
