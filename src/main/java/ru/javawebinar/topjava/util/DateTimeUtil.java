package ru.javawebinar.topjava.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static boolean isBetweenHalfOpen(LocalTime lt, LocalTime startTime, LocalTime endTime) {
        return lt.compareTo(startTime) >= 0 && lt.compareTo(endTime) < 0;
    }

    public static <T extends Comparable>boolean isBetween (T date, T start, T end) {
        if(date instanceof LocalTime) {
            if (start != null)
                if (date.compareTo(start) < 0) return false;
            if (end != null)
                if (date.compareTo(end) >= 0) return false;
            return true;
        }
        if(date instanceof LocalDate) {
            if (start != null)
                if (date.compareTo(start) < 0) return false;
            if (end != null)
                if (date.compareTo(end) > 0) return false;
            return true;
        }
        return true;
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }
}
