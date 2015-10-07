package com.app.upincode.getqd.networking.parsers.venue_based;

import com.app.upincode.getqd.networking.parsers.BaseParser;

import org.joda.time.DateTime;

/**
 * Created by jpnauta on 15-09-18.
 */
public class VBReservationParser extends BaseParser {
    public class VenueReservationUserParser extends BaseParser {
        public Integer id;
        public String username;
    }

    public Integer id;
    public Integer num_of_people;
    public Boolean is_approved;
    public String name;
    public String details;
    public DateTime arrival_date;
    public VenueReservationUserParser user;
}
