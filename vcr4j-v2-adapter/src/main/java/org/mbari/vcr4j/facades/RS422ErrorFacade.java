package org.mbari.vcr4j.facades;

import org.mbari.vcr4j.VCRErrorAdapter;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.rs422.RS422Error;
import org.mbari.vcr4j.rs422.RS422State;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * @author Brian Schlining
 * @since 2016-03-10T16:45:00
 */
public class RS422ErrorFacade<S extends RS422State, E extends RS422Error> extends VCRErrorAdapter {

    private final AtomicReference<E> videoError = new AtomicReference<>();

    public RS422ErrorFacade(VideoIO<S, E> videoIO) {
        videoIO.getErrorObservable().forEach(e -> {
            videoError.set(e);
            oc.notify(RS422ErrorFacade.this, null);
        });
    }

    @Override
    public boolean isChecksumError() {
        return apply(() -> videoError.get().isChecksumError());
    }

    @Override
    public boolean isFramingError() {
        return apply(() -> videoError.get().isFramingError());
    }

    @Override
    public boolean isOK() {
        return apply(() -> videoError.get().isOK());
    }

    @Override
    public boolean isOverrunError() {
        return apply(() -> videoError.get().isOverrunError());
    }

    @Override
    public boolean isParityError() {
        return apply(() -> videoError.get().isParityError());
    }

    @Override
    public boolean isTimeout() {
        return apply(() -> videoError.get().isTimeout());
    }

    @Override
    public boolean isUndefinedCommand() {
        return apply(() -> videoError.get().isUndefinedCommand());
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
