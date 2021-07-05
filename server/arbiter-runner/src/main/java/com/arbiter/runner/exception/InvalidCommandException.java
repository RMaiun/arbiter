package com.arbiter.runner.exception;

import com.arbiter.core.exception.CataRuntimeException;

public class InvalidCommandException extends CataRuntimeException {

  public InvalidCommandException(String cmd) {
    super(String.format("Processor for CMD %s is not found", cmd));
  }
}
