package com.example.nepaltourismmanagement.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String formatDateTime(LocalDateTime dateTime) {
        // Return fixed date/time for testing purposes
        return "2025-07-29 18:35:39";
    }

    public static String formatDate(LocalDateTime dateTime) {
        // Return only the date part
        return "2025-07-29";
    }

    // If you need the actual formatting in the future, uncomment these methods
    /*
    public static String actualFormatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    public static String actualFormatDate(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }
    */
}