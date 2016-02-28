/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs.utilities;

import android.database.Cursor;
import android.support.v7.graphics.Palette;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Contains various utility functions
 */
public final class Utilities {
    /** Extracts a year from a literal date */
    public static String formatYear(String literalDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = formatter.parse(literalDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return String.valueOf(calendar.get(GregorianCalendar.YEAR));
        }
        catch (ParseException e) {
            return "-";
        }
    }

    /** Toggles string resource based on boolean condition */
    public static int toggleStringResource(boolean condition,
                                     int positiveStringResource, int negativeStringResource) {
        return condition ? positiveStringResource : negativeStringResource;
    }

    /** Toggles image resource based on boolean condition */
    public static int toggleImageResource(boolean condition,
                                       int positiveImageResource, int negativeImageResource) {
        return condition ? positiveImageResource : negativeImageResource;
    }

    /** Gets the string from the specified cursor's column */
    public static String getString(Cursor cursor, String columName) {
        return cursor.getString(cursor.getColumnIndex(columName));
    }

    /** Gets the integer from the specified cursor's column */
    public static int getInt(Cursor cursor, String columName) {
        return cursor.getInt(cursor.getColumnIndex(columName));
    }

    /** Gets the long from the specified cursor's column */
    public static long getLong(Cursor cursor, String columName) {
        return cursor.getLong(cursor.getColumnIndex(columName));
    }

    /** Gets the boolean from the specified cursor's column */
    public static boolean getBoolean(Cursor cursor, String columName) {
        return cursor.getInt(cursor.getColumnIndex(columName)) != 0;
    }

    /** Gets the float from the specified cursor's column */
    public static float getFloat(Cursor cursor, String columnName) {
        return cursor.getFloat(cursor.getColumnIndex(columnName));
    }

    /** Find the best swatch based on population */
    public static Palette.Swatch getDominantSwatch(Palette palette) {
        return Collections.max(palette.getSwatches(), new Comparator<Palette.Swatch>() {
            @Override
            public int compare(Palette.Swatch sw1, Palette.Swatch sw2) {
                if (sw1.getPopulation() == sw2.getPopulation()) {
                    return sw1.getPopulation();
                } else if (sw1.getPopulation() < sw2.getPopulation()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }
}