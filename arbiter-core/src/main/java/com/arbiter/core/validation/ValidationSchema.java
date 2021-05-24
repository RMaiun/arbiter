package com.arbiter.core.validation;

import com.arbiter.core.exception.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ValidationSchema {

  private final List<Supplier<Stream<String>>> prodsList;
  private Stream<String> msgs = Stream.empty();

  private ValidationSchema() {
    this.prodsList = new ArrayList<>();
  }

  public static ValidationSchema schema() {
    return new ValidationSchema();
  }

  public ValidationSchema withRule(Supplier<Stream<String>> prod) {
    this.prodsList.add(prod);
    return this;
  }

  ValidationSchema validate() {
    msgs = prodsList.stream().flatMap(Supplier::get);
    return this;
  }

  void check() {
    List<String> result = msgs.toList();
    if (!result.isEmpty()) {
      throw new ValidationException(String.join(".", result));
    }
  }

  Stream<String> getMsgs() {
    return msgs;
  }
}
