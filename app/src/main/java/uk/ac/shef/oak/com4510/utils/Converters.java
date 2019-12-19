package uk.ac.shef.oak.com4510.utils;

import androidx.room.TypeConverter;

import java.util.Date;


/**
 * Converters for the database
 */
public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
