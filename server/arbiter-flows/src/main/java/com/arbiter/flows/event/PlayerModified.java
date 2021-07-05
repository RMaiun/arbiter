package com.arbiter.flows.event;

public record PlayerModified(String id) implements Event {

  @Override
  public String eventCode() {
    return PLAYER_MODIFIED;
  }

  @Override
  public String identifier() {
    return id;
  }
}
