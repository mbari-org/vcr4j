package org.mbari.vcr4j.remote.control.commands.loc;

import org.mbari.vcr4j.remote.control.commands.RResponse;
import org.mbari.vcr4j.util.CollectionUtils;

import java.util.List;

/**
 * Base class for {{@link LocalizationsCmd}}s that have a payload (typically, {{@link Localization}}s
 * or {{@link java.util.UUID}}
 * @author Brian Schlining
 * @since 2022-08-08
 */
public abstract class LocalizationsPayloadCmd<T, A extends LocalizationRequest<T>, B extends RResponse>
        extends LocalizationsCmd<A, B> {
    public LocalizationsPayloadCmd(A value) {
        super(value);
    }

    public List<List<T>> groupedPayload(int size) {
        return CollectionUtils.grouped(getValue().getLocalizations(), size);
    }

}
