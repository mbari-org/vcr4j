package org.mbari.vcr4j.commands;

public class SimpleVideoCommand<A> implements VideoCommand<A> {
  
  private final String name;
  private final A value;
  
  public SimpleVideoCommand(String name, A value) {
    this.name = name;
    this.value = value;
  }
  
  public String getName() {
    return name;
  }
  
  public A getValue() {
    return value;
  }
  
}