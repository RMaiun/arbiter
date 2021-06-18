package com.arbiter.core.exception;

public class RoundNotFoundException extends RuntimeException {

  public RoundNotFoundException(String id) {
    super(String.format("Round with id %s is not found", id));
  }
}
