package com.fintrack.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private DateUtil() {}

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static String formatDate(LocalDate date) {
        if (date == null) return null;
        return date.format(DISPLAY_FORMAT);
    }

    public static int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }

    public static int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    public static LocalDate getFirstDayOfMonth(int month, int year) {
        return LocalDate.of(year, month, 1);
    }

    public static LocalDate getLastDayOfMonth(int month, int year) {
        return LocalDate.of(year, month, 1).withDayOfMonth(
                LocalDate.of(year, month, 1).lengthOfMonth());
    }
}
