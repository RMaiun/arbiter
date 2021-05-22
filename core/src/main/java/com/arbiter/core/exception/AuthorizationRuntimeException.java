package com.arbiter.core.exception;

public class AuthorizationRuntimeException extends CataRuntimeException {

  public AuthorizationRuntimeException() {
    super(String.format("Oops, you are not authorized.%sPlease contact responsible person to fix that.", System.lineSeparator()));
  }
}
