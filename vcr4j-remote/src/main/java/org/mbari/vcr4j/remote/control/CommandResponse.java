package org.mbari.vcr4j.remote.control;

import org.mbari.vcr4j.remote.control.commands.RCommand;
import org.mbari.vcr4j.remote.control.commands.RRequest;
import org.mbari.vcr4j.remote.control.commands.RResponse;


public record CommandResponse(RCommand<? extends RRequest, ? extends RResponse> command,
                              RResponse response) {

}
