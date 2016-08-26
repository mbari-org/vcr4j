package org.mbari.vcr4j.sharktopoda.commands;

import org.mbari.vcr4j.commands.SimpleVideoCommand;

import java.net.URL;

/**
 * @author Brian Schlining
 * @since 2016-08-25T17:28:00
 */
public class OpenCmd extends SimpleVideoCommand<URL> {

    public OpenCmd(URL value) {
        super("open", value);
    }
}
