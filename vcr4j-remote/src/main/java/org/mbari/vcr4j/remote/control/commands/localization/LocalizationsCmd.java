package org.mbari.vcr4j.remote.control.commands.localization;

import org.mbari.vcr4j.remote.control.commands.RCommand;
import org.mbari.vcr4j.remote.control.commands.RRequest;
import org.mbari.vcr4j.remote.control.commands.RResponse;

/**
 * Base class for all commands that deal with localizations
 * @author Brian Schlining
 * @since 2022-08-08
 */
public abstract class LocalizationsCmd<A extends RRequest, B extends RResponse> extends RCommand<A, B> {

    public LocalizationsCmd(A value) {
        super(value);
    }
}

