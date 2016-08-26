package org.mbari.vcr4j.sharktopoda.commands;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.commands.SimpleVideoCommand;

/**
 * @author Brian Schlining
 * @since 2016-08-25T17:33:00
 */
public class RequestAllVideoInfosCmd extends SimpleVideoCommand<Void> {

    public RequestAllVideoInfosCmd() {
        super("request all video information", null);
    }
}
