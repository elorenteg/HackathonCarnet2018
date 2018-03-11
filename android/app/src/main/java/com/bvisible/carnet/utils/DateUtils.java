package com.bvisible.carnet.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static Date parseDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.UK);

        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            return new Date(0);
        }
    }
}
