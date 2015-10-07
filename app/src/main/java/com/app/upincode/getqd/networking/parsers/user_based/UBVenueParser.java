package com.app.upincode.getqd.networking.parsers.user_based;

import com.app.upincode.getqd.networking.parsers.BaseParser;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class UBVenueParser extends BaseParser {
    public Integer id;
    public String name;
    public String avatar;
    public DateTimeZone timezone;
    public DateTime created;
    public DateTime updated;
}
