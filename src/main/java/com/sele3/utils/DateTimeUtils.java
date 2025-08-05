package com.sele3.utils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimeUtils {

    public static int getMonthDiff(String fromMonthYear, String toMonthYear, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale);
        YearMonth from = YearMonth.parse(fromMonthYear, formatter);
        YearMonth to = YearMonth.parse(toMonthYear, formatter);
        return (int) ChronoUnit.MONTHS.between(from, to);
    }
}
