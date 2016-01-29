package org.mbari.vcr4j.commands;

import java.time.Duration;

public class SeekElapsedTimeCmd extends SimpleVideoCommand<Duration> {
  
  public SeekElapsedTimeCmd(Duration elapsedTime) {
    super("seek elapsed time", elapsedTime);
  }
  
}