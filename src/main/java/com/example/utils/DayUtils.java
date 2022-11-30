package com.example.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;

public class DayUtils {
  public static LocalDate shiftDate(LocalDate date) {
    LocalDate currentDate = date;
    while (DayOfWeek.of(currentDate.get(ChronoField.DAY_OF_WEEK)) == DayOfWeek.SUNDAY || DayOfWeek.of(currentDate.get(ChronoField.DAY_OF_WEEK)) == DayOfWeek.SATURDAY) {
      currentDate = currentDate.plusDays(1);
    }

    return currentDate;
  }
}
