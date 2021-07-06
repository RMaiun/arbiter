package com.arbiter.core.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.apache.commons.lang3.tuple.Pair;

public class SeasonUtils {

  private SeasonUtils() {
  }

  public static boolean firstBeforeSecond(String s1, String s2) {
    LocalDate firstSeasonEndDate = seasonGate(s1).getRight();
    LocalDate secondSeasonEndDate = seasonGate(s2).getRight();
    return firstSeasonEndDate.isBefore(secondSeasonEndDate);
  }

  public static String seasonFromDate(ZonedDateTime dateTime) {
    int quarter = quarterForMonth(dateTime.getMonth().getValue());
    int year = dateTime.getYear();
    return formatSeason(quarter, year);
  }

  public static String previousSeason(String season) {
    Pair<LocalDate, LocalDate> gate = seasonGate(season);
    LocalDate localDate = gate.getLeft().minusDays(1);
    return seasonFromDate(ZonedDateTime.of(localDate, LocalTime.now(), ZoneOffset.UTC));
  }

  public static String previousSeason() {
    return previousSeason(currentSeason());
  }

  public static String currentSeason() {
    ZonedDateTime now = DateUtils.now();
    int month = now.getMonth().getValue();
    int year = now.getYear();
    int q = quarterForMonth(month);
    return formatSeason(q, year);
  }

  public static Pair<LocalDate, LocalDate> seasonGate(String seasonName) {
    String[] seasonData = seasonName.split("\\|");
    Pair<Integer, Integer> qGate = quarterGate(Integer.parseInt(seasonData[0].split("")[1]));
    int year = Integer.parseInt(seasonData[1]);
    LocalDate start = LocalDate.of(year, qGate.getKey(), 1);
    Month lastMonth = Month.of(qGate.getValue());
    LocalDate tmpDate = LocalDate.of(year, lastMonth, 1);
    LocalDate end = LocalDate.of(year, lastMonth, tmpDate.lengthOfMonth());
    return Pair.of(start, end);
  }

  private static String formatSeason(int quarter, int year) {
    return String.format("S%d|%d", quarter, year);
  }

  private static int quarterForMonth(int month) {
    return month <= 3 ? 1 : month <= 6 ? 2 : month <= 9 ? 3 : 4;
  }

  private static Pair<Integer, Integer> quarterGate(int quarter) {
    return switch (quarter) {
      case 1 -> Pair.of(1, 3);
      case 2 -> Pair.of(4, 6);
      case 3 -> Pair.of(7, 9);
      case 4 -> Pair.of(10, 12);
      default -> Pair.of(0, 0);
    };
  }
}
