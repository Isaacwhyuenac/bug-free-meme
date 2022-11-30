package com.example.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DayUtilsTest {
  @Test
  public void testSaturdayMoveToNextMonday() {
    LocalDate date = LocalDate.of(2022, 11, 26);

    LocalDate localDate = DayUtils.shiftDate(date);
    DayOfWeek dayOfWeek = DayOfWeek.of(localDate.get(ChronoField.DAY_OF_WEEK));

    Assertions.assertEquals(DayOfWeek.MONDAY, dayOfWeek);
  }

}