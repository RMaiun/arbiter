package com.arbiter.core.config;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class ZonedDateTimeReadConverter implements Converter<Date, ZonedDateTime> {
  @Override
  public ZonedDateTime convert(Date date) {
    return date.toInstant().atZone(ZoneOffset.UTC);
  }
}
