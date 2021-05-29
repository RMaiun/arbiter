package com.arbiter.core.exception;

public class InvalidUserRightsException extends CataRuntimeException {

  public InvalidUserRightsException() {
    super("Not enough rights to use current command");
  }
}
