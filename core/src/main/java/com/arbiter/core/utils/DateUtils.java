package com.arbiter.core.utils;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateUtils {

  private DateUtils() {
  }

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
  private static final ZoneId EET_ZONE = ZoneId.of("Europe/Kiev");

  public static boolean notLateToSend(ZonedDateTime dateTime) {
    return dateTime.getHour() >= 10 && dateTime.getHour() <= 23;
  }

  public static ZonedDateTime now() {
    return ZonedDateTime.now(ZoneOffset.UTC);
  }

  public static ZonedDateTime utcToEet(ZonedDateTime utcDateTime) {
    return utcDateTime.withZoneSameInstant(EET_ZONE);
  }

  public static String formatDateWithHour(ZonedDateTime utcDateTime) {
    ZonedDateTime date = utcToEet(utcDateTime);
    String month = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    int day = date.getDayOfMonth();
    String dateTime = date.format(formatter);
    int year = date.getYear();
    return String.format("%s %s %s %s", dateTime, day, month, year);
  }
}
