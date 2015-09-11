package com.app.upincode.getqd.utils;

/**
 * Created by herbert on 8/27/2015.
 */
//2016-02-04T07:00:00Z

import android.util.Log;

import com.app.upincode.getqd.logging.GQLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Helper class for handling a most common subset of ISO 8601 strings
 * (in the following format: "2008-03-01T13:00:00+01:00"). It supports
 * parsing the "Z" timezone, but many other less-used features are
 * missing.
 */
public final class ISO8601 {
    /**
     * Transform Calendar to ISO 8601 string.
     */
    boolean debug = true;
    public String getMonths[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public String fromCalendar(final Calendar calendar) {
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    /**
     * Get current date and time formatted as ISO 8601 string.
     */
    public String now() {
        return fromCalendar(GregorianCalendar.getInstance());
    }

    /**
     * Transform ISO 8601 string to Calendar.
     */
    public Calendar toCalendar(final String iso8601string)
            throws ParseException {
        Calendar calendar = GregorianCalendar.getInstance();
        String s = iso8601string.replace("Z", "+00:00");
        try {
            //"2015-08-19T23:27:32.83+00:00"
            //  GQLog.dObj(this,"pres="+s  + "length=" + s.length());
            if (s.length() == 29) {  // this is a slightly funny format for ticket inventory.  We just take out the milli-seconds. and all is good.
                s = s.substring(0, 19) + s.substring(23, 29);
                //  GQLog.dObj(this,"changed pres="+s  + "length=" + s.length());
            }
            s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
            //  GQLog.dObj(this, "posts=" + s + "length=" + s.length());
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid length", 0);
        }
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
        calendar.setTime(date);
        return calendar;
    }

    public Date offsetTimeZone(Date date, String fromTZ, String toTZ) {

// Construct FROM and TO TimeZone instances
        TimeZone fromTimeZone = TimeZone.getTimeZone(fromTZ);
        TimeZone toTimeZone = TimeZone.getTimeZone(toTZ);

// Get a Calendar instance using the default time zone and locale.
        Calendar calendar = Calendar.getInstance();

// Set the calendar's time with the given date
        calendar.setTimeZone(fromTimeZone);
        calendar.setTime(date);

        GQLog.dObj(this, "Input: " + calendar.getTime() + " in " + fromTimeZone.getDisplayName());

// FROM TimeZone to UTC
        calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);

        if (fromTimeZone.inDaylightTime(calendar.getTime())) {
             GQLog.dObj(this, "============= Adjust for timezone differnce");
            calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
        }

// UTC to TO TimeZone
        calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());

        if (toTimeZone.inDaylightTime(calendar.getTime())) {
            calendar.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
        }

        return calendar.getTime();

    }

    public Calendar offsetTimeZoneCalendar(Date date, String fromTZ, String toTZ) {

// Construct FROM and TO TimeZone instances
        TimeZone fromTimeZone = TimeZone.getTimeZone(fromTZ);
        TimeZone toTimeZone = TimeZone.getTimeZone(toTZ);

// Get a Calendar instance using the default time zone and locale.
        Calendar calendar = Calendar.getInstance();

// Set the calendar's time with the given date
        calendar.setTimeZone(fromTimeZone);
        calendar.setTime(date);

        GQLog.dObj(this, "Input: " + calendar.getTime() + " in " + fromTimeZone.getDisplayName());

// FROM TimeZone to UTC
        calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);

        if (fromTimeZone.inDaylightTime(calendar.getTime())) {
             GQLog.dObj(this, "============= Adjust for timezone differnce");
            calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
        }

// UTC to TO TimeZone
        calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());

        if (toTimeZone.inDaylightTime(calendar.getTime())) {
            calendar.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
        }

        return calendar;

    }
}
