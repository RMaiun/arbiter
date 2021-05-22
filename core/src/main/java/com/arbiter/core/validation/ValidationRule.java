package com.arbiter.core.validation;

import static java.util.Objects.isNull;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ValidationRule {

  private static <T> Stream<String> executeRule(T data, String field, List<ValidationFunction<T>> validators) {
    ValueField<T> tValueField = new ValueField<>(data, field);
    if (isNull(data)) {
      return Stream.of(String.format("Field %s must be present", field));
    } else {
      return validators.stream().flatMap(v -> v.validate(tValueField).stream());
    }
  }

  private static <T> Stream<String> executeSubSchemaRule(T obj, String field, ValidationType<T> validationType) {
    if (isNull(obj)) {
      return Stream.of(String.format("Field %s must be present", field));
    }
    return validationType.applyDto(obj).validate().getMsgs();
  }

  @SafeVarargs
  public static <T> Supplier<Stream<String>> rule(T data, String field, ValidationFunction<T>... validators) {
    return () -> isNull(data) ? Stream.empty() : executeRule(data, field, List.of(validators));
  }

  @SafeVarargs
  public static <T> Supplier<Stream<String>> requiredRule(T data, String field, ValidationFunction<T>... validators) {
    return () -> executeRule(data, field, List.of(validators));
  }

  public static <T> Supplier<Stream<String>> requiredRule(T data, String field, ValidationType<T> validationType) {
    return () -> executeSubSchemaRule(data, field, validationType);
  }

  public static <T> Supplier<Stream<String>> rule(T data, String field, ValidationType<T> validationType) {
    return () -> executeSubSchemaRule(data, field, validationType);
  }
}
