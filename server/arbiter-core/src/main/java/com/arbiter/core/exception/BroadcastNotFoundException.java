package com.arbiter.core.exception;

public class BroadcastNotFoundException extends CataRuntimeException {

  public BroadcastNotFoundException(String id) {
    super(String.format("Broadcast with %s is not found", id));
  }
}
