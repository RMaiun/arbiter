package com.arbiter.flows.event;

public interface Event {

  String ROUND_ADDED = "round_added";
  String PLAYER_MODIFIED = "player_modified";

  String eventCode();
  String identifier();
}
