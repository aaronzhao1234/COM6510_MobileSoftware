package uk.ac.shef.oak.com4510.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Java Utilities
 */
public class Utilities {

    /**
     * it converts a number of mseconds since 1.1.1970 (epoch) to a current string date
     * @param actualTimeInMseconds a time in msecs for the UTC time zone
     * @return a time string of type HH:mm:ss such as 23:12:54.
     */
    public static String mSecsToString(long actualTimeInMseconds) {
        Date date = new Date(actualTimeInMseconds);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return (formatter.format(date));
    }

    /**
     * Convert a {@link Date} into time string
     * @param date date to be converted
     * @return a time string of type dd/MM/yyyy
     */
    public static String dateToStringSimple(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

}