package org.mbari.vcr4j.commands;


import java.time.Instant;

public class SeekTimestampCmd extends SimpleVideoCommand<Instant> {
  
  public SeekTimestampCmd(Instant timestamp) {
    super("seek timestamp", timestamp);
  }
  
}