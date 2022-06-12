package org.mbari.vcr4j.remote.control.commands.loc;

import org.mbari.vcr4j.remote.control.commands.RCommand;
import org.mbari.vcr4j.remote.control.commands.RRequest;
import org.mbari.vcr4j.remote.control.commands.RResponse;

public abstract class LocalizationsCmd<A extends RRequest, B extends RResponse> extends RCommand<A, B> {

    public LocalizationsCmd(A value) {
        super(value);
    }
}

