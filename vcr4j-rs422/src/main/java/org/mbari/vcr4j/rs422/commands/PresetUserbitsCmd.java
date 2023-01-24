package org.mbari.vcr4j.rs422.commands;

import org.mbari.vcr4j.commands.SimpleVideoCommand;

/**
 * @author Brian Schlining
 * @since 2016-01-28T13:05:00
 */
public class PresetUserbitsCmd extends SimpleVideoCommand<byte[]> {

    public PresetUserbitsCmd(byte[] value) {
        super("preset userbits", value);
    }
}
