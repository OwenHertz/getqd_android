package com.app.upincode.getqd.networking.parsers.venue_based;

import com.app.upincode.getqd.networking.parsers.BaseParser;
import com.app.upincode.getqd.networking.parsers.generic.BasePaginationParser;

import org.joda.time.DateTime;

/**
 * Created by jpnauta on 15-09-18.
 */

//Parser for sending barcode to server
public class VBGetEventsParser extends BaseParser {
    public class PaginationParser extends BasePaginationParser {
        public  VBGetEventsParser[] results;
    }

    public class VenueGetEventsUserParser extends BaseParser {
        public Integer id;
        public String username;
    }

    public Integer id;
    public String name;
    public String venue;
    public DateTime starts_on;
    public VenueGetEventsUserParser user;

}
