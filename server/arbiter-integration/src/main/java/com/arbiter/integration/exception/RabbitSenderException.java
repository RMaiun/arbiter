package com.arbiter.integration.exception;

import com.arbiter.core.exception.CataRuntimeException;

public class RabbitSenderException extends CataRuntimeException {

  public RabbitSenderException(Throwable cause) {
    super(cause);
  }
}
