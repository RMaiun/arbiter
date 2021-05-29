package com.arbiter.core.validation;

public class ValueField<T> {

  private final T data;
  private final String field;

  public ValueField(T data, String field) {
    this.data = data;
    this.field = field;
  }

  public T getData() {
    return data;
  }

  public String getField() {
    return field;
  }

}
