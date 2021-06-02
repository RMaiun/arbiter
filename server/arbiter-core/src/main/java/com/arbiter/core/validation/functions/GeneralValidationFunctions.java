package com.arbiter.core.validation.functions;

import java.util.Collections;
import java.util.Objects;

public interface GeneralValidationFunctions {

  static <T> ValidationFunction<T> exists() {
    return vf -> Objects.nonNull(vf.getData())
        ? Collections.emptyList()
        : Collections.singletonList(String.format("Field %s must be present", vf.getField()));
  }
}
