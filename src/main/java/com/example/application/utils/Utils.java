package com.example.application.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utils {
    public static String formatTime(Integer epochSeconds) {
        String formattedTime = "";
        if(epochSeconds != 0){
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC);
            formattedTime = dateTime.format(DateTimeFormatter.ofPattern("HH:mm", new Locale("ro_RO")));

        }
        return formattedTime;
    }
}
