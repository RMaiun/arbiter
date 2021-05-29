package com.arbiter.core.exception;

public class SamePlayersInRoundException extends CataRuntimeException {

  public SamePlayersInRoundException() {
    super("All players in round must be different");
  }
}
