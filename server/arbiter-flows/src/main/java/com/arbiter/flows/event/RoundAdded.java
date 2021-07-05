package com.arbiter.flows.event;

public record RoundAdded(String id) implements Event {

  @Override
  public String eventCode() {
    return ROUND_ADDED;
  }

  @Override
  public String identifier() {
    return id;
  }
}
