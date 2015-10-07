package com.app.upincode.getqd.utils;

import com.app.upincode.getqd.config.GQConstants;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtils {
    /**
     * @param dt the datetime to convert
     * @return ISO8601 string represented by date
     */
    public static String toIsoString(DateTime dt) {
        dt = dt.toDateTime(GQConstants.UTC); // ISO dates should be serialized in UTC
        DateTimeFormatter isoFormatter = DateTimeFormat.forPattern(GQConstants.ISO_FORMAT).withZone(GQConstants.UTC);
        return isoFormatter.print(dt);
    }
}
