package org.mbari.vcr4j.remote.control.commands.loc;

import org.mbari.vcr4j.remote.control.commands.RResponse;
import org.mbari.vcr4j.util.CollectionUtils;

import java.util.List;

public abstract class LocalizationsPayloadCmd<T, A extends LocalizationRequest<T>, B extends RResponse>
        extends LocalizationsCmd<A, B> {
    public LocalizationsPayloadCmd(A value) {
        super(value);
    }

    public List<List<T>> groupedPayload(int size) {
        return CollectionUtils.grouped(getValue().getLocalizations(), size);
    }

}
