package com.arbiter.core.validation.functions;

import com.arbiter.core.validation.ValueField;
import java.util.List;

public interface ValidationFunction<T> {

  List<String> validate(ValueField<T> data);
}
