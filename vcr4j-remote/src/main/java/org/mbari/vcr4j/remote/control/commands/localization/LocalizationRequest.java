package org.mbari.vcr4j.remote.control.commands.localization;

import org.mbari.vcr4j.remote.control.commands.RRequest;

import java.util.List;
import java.util.UUID;

/**
 * A remote request that contains Localization info.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public abstract class LocalizationRequest<T> extends RRequest {

    private List<T> localizations;

    public LocalizationRequest(String command, UUID uuid, List<T> localizations) {
        super(command, uuid);
        this.localizations = localizations;
    }

    public List<T> getLocalizations() {
        return localizations;
    }

}
