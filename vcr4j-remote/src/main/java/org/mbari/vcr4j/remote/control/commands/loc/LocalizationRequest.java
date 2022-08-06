package org.mbari.vcr4j.remote.control.commands.loc;

import org.mbari.vcr4j.remote.control.commands.RRequest;

import java.util.List;
import java.util.UUID;

public abstract class LocalizationRequest<T> extends RRequest {

    private List<T> localizations;

    public LocalizationRequest(String command, UUID uuid, List<T> localizations) {
        super(command, uuid);
    }

    public List<T> getLocalizations() {
        return localizations;
    }

}
