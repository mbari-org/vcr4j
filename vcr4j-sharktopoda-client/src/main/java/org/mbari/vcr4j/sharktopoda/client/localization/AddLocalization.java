package org.mbari.vcr4j.sharktopoda.client.localization;

import org.mbari.vcr4j.sharktopoda.client.localization.Localization;

/**
 * @author Brian Schlining
 * @since 2020-02-10T14:13:00
 */
public class AddLocalization {

    private final Localization localization;

    public AddLocalization(Localization localization) {
        this.localization = localization;
    }

    public Localization getLocalization() {
        return localization;
    }
}
