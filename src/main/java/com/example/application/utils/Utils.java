package com.example.application.utils;

import java.text.Normalizer;
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
        else return "00:00";
        return formattedTime;
    }
    public static String setBreak(Integer stationaryTime) {
        String breakTime = "";
        if(stationaryTime != 0) {
            if (stationaryTime < 60) {
                breakTime = stationaryTime + " seconds";
            } else if (stationaryTime == 60) {
                breakTime = "1 minute";
            } else {
                breakTime = stationaryTime / 60 + " minutes";
            }
        }
        return breakTime;
    }
    public static String replaceSearch(String search){

        String convertedString =
                Normalizer
                        .normalize(search, Normalizer.Form.NFD)
                        .replaceAll("[^\\p{ASCII}]", "");
        return convertedString.toLowerCase();
    }
}
