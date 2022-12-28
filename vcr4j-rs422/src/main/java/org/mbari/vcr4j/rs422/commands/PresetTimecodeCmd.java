package org.mbari.vcr4j.rs422.commands;

import org.mbari.vcr4j.commands.SimpleVideoCommand;
import org.mbari.vcr4j.time.Timecode;

public class PresetTimecodeCmd extends SimpleVideoCommand<Timecode> {

    public PresetTimecodeCmd(Timecode timecode) {
        super("preset timecode", timecode);
    }

}
