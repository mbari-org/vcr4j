package org.mbari.vcr4j;

public interface VideoCommand<A> {
  
  public String getName();
  
  public A getValue();
  
}