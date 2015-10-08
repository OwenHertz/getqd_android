package com.app.upincode.getqd.networking.parsers.publik;

import com.app.upincode.getqd.models.CoordinateSet;
import com.app.upincode.getqd.networking.parsers.BaseParser;

import org.joda.time.DateTime;

public class PublicEventParser extends BaseParser {
    public class VenueParser extends BaseParser {
        public String name;
        public String slug;
    }

    public class LocationParser extends BaseParser {
        public CoordinateSet position;
    }

    public Integer id;
    public String name;
    public String slug;
    public String subtitle;
    public String image;
    public String image_medium;
    public DateTime created;
    public DateTime updated;
    public DateTime starts_on;
    public DateTime ends_on;
    public DateTime opens_at;
    public VenueParser venue;
    public LocationParser location;
}
