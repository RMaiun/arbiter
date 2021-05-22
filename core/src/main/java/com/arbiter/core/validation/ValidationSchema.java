package com.arbiter.core.validation;

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

  static ValidationSchema schema() {
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

  Stream<String> getMsgs() {
    return msgs;
  }
}
