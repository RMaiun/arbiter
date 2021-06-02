package com.arbiter.flows.exception;

import com.arbiter.core.exception.CataRuntimeException;

public class RabbitSenderException extends CataRuntimeException {

  public RabbitSenderException(Throwable cause) {
    super(cause);
  }
}
