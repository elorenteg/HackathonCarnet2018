package com.bvisible.carnet.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static Date parseDate(String date) {
        String inputFormat = "HH:mm";
        SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);

        try {
            return inputParser.parse(date);
        } catch (ParseException e) {
            return new Date(0);
        }
    }
}
