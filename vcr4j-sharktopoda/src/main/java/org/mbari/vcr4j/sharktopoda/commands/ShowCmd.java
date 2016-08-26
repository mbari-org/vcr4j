package org.mbari.vcr4j.sharktopoda.commands;

import org.mbari.vcr4j.commands.SimpleVideoCommand;

/**
 * @author Brian Schlining
 * @since 2016-08-25T17:28:00
 */
public class ShowCmd extends SimpleVideoCommand<Void> {

    public ShowCmd() {
        super("show", null);
    }
}
