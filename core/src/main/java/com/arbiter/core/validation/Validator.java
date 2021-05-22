package com.arbiter.core.validation;


public class Validator {

  private Validator() {
  }

  public static <T> void validate(T dto, ValidationType<T> validationType) {
    validationType.applyDto(dto).validate();
  }
}
