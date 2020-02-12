package org.mbari.vcr4j.sharktopoda.client.localization;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2020-02-11T16:23:00
 */
public class RemoveLocalizationMsg {

    private UUID localizationUuid;

    public RemoveLocalizationMsg(UUID localizationUuid) {
        this.localizationUuid = localizationUuid;
    }

    public RemoveLocalizationMsg() {
    }

    public UUID getLocalizationUuid() {
        return localizationUuid;
    }
}
