package org.mbari.vcr4j.facades;

import org.mbari.util.IObserver;
import org.mbari.util.ObservableSupport;
import org.mbari.vcr4j.IVCRError;
import org.mbari.vcr4j.IVCRReply;
import org.mbari.vcr4j.IVCRState;
import org.mbari.vcr4j.IVCRTimecode;
import org.mbari.vcr4j.IVCRUserbits;
import org.mbari.vcr4j.VCRTimecodeAdapter;
import org.mbari.vcr4j.VCRUserbitsAdapter;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.rs422.RS422VideoIO;

/**
 * @author Brian Schlining
 * @since 2016-03-11T11:03:00
 */
public class VCRReplyFacade<S extends VideoState, E extends VideoError> implements IVCRReply {

    private ObservableSupport os = new ObservableSupport();
    private final IVCRTimecode timecode = new VCRTimecodeAdapter();
    private final IVCRUserbits userbits = new VCRUserbitsAdapter();
    private final IVCRState state;
    private final IVCRError error;


    public VCRReplyFacade(VideoIO<S, E> videoIO) {

        // --- Timecode
        videoIO.getIndexObservable().forEach(vi ->
                vi.getTimecode().ifPresent(tc -> timecode.timecodeProperty().setValue(tc))
        );

        // --- Userbits
        if (videoIO instanceof RS422VideoIO) {
            RS422VideoIO rs422VideoIO = (RS422VideoIO) videoIO;
            rs422VideoIO.getUserbitsObservable()
                    .forEach(ub -> userbits.setUserbits(ub.getUserbits()));
        }

        // --- State
        if (videoIO instanceof RS422VideoIO) {
            RS422VideoIO rs422VideoIO = (RS422VideoIO) videoIO;
            state = new RS422StateFacade<>(rs422VideoIO);
        }
        else {
            state = new VCRStateFacade<>(videoIO);
        }

        // -- Error
        if (videoIO instanceof RS422VideoIO) {
            RS422VideoIO rs422VideoIO = (RS422VideoIO) videoIO;
            error = new RS422ErrorFacade<>(rs422VideoIO);
        }
        else {
            error = new VCRErrorFacade<>(videoIO);
        }

    }

    @Override
    public IVCRError getVcrError() {
        return error;
    }

    @Override
    public IVCRState getVcrState() {
        return state;
    }

    @Override
    public IVCRTimecode getVcrTimecode() {
        return timecode;
    }

    @Override
    public IVCRUserbits getVcrUserbits() {
        return userbits;
    }

    @Override
    public boolean isAck() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean isNack() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean isStatusReply() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean isTimecodeReply() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean isUserbitsReply() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void addObserver(IObserver observer) {
        os.add(observer);
    }

    @Override
    public void removeAllObservers() {
        os.clear();
    }

    @Override
    public void removeObserver(IObserver observer) {
        os.remove(observer);
    }
}
