package org.mbari.vcr4j.sharktopoda.commands;

import org.mbari.vcr4j.commands.SimpleVideoCommand;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-31T14:18:00
 */
public class ShowCmd extends SimpleVideoCommand<UUID> {
    public ShowCmd(UUID value) {
        super("show", value);
    }
}
