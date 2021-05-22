package com.arbiter.core.validation;

public interface ValidationType<T> {
  ValidationSchema applyDto(T dto);
}
