package ru.practicum.mainmodule.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Method to convert date and time from ISO format to a string in the format "yyyy-MM-dd HH:mm:ss".
     */
    public String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }
}
