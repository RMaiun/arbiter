package com.arbiter.core.validation.functions;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public interface NumberValidationFunctions {

  static <T> ValidationFunction<Integer> intBetween(int start, int end) {
    return vf -> (vf.getData() >= start && vf.getData() <= end)
        ? emptyList()
        : singletonList(String.format("Integer value for prop: %s must be in range [%d,%d] ", vf.getField(), start, end));
  }

  static ValidationFunction<Long> longBetween(long start, long end) {
    return vf -> (vf.getData() >= start && vf.getData() <= end)
        ? emptyList()
        : singletonList(String.format("Long value for prop: %s must be in range [%d,%d] ", vf.getField(), start, end));
  }
}
