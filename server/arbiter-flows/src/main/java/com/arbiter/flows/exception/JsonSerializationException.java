package com.arbiter.flows.exception;

import com.arbiter.core.exception.CataRuntimeException;

public class JsonSerializationException extends CataRuntimeException {

  public JsonSerializationException(Throwable cause) {
    super(cause);
  }
}
