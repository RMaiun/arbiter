package com.arbiter.core.config;

import java.time.ZonedDateTime;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class ZonedDateTimeWriteConverter implements Converter<ZonedDateTime, Date> {

  @Override
  public Date convert(ZonedDateTime zonedDateTime) {
    return Date.from(zonedDateTime.toInstant());
  }
}
