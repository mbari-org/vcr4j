package org.mbari.vcr4j.rs422.commands;

import org.mbari.vcr4j.commands.SimpleVideoCommand;

/**
 * At MBARI, we write time to the userbits track. This command is a signal to request
 * the time from the tape. This requires use of the UserbitsAsTimeDecorator.
 *
 * @author Brian Schlining
 * @since 2016-01-29T16:27:00
 */
public class RequestUserbitsAsTimeCmd  extends SimpleVideoCommand<Void> {

    public static final RequestUserbitsAsTimeCmd COMMAND = new RequestUserbitsAsTimeCmd();

    private RequestUserbitsAsTimeCmd() {
        super("request userbits as time", null);
    }
}
