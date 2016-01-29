package org.mbari.vcr4j.commands;

public interface VideoCommand<A> {
  
  public String getName();
  
  public A getValue();
  
}