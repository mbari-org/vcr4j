package org.mbari.vcr4j.commands;


import org.mbari.vcr4j.time.Timecode;

public class SeekTimecodeCmd extends SimpleVideoCommand<Timecode> {
  
  public SeekTimecodeCmd(Timecode timecode) {
    super("seek timecode", timecode);
  }
  
}