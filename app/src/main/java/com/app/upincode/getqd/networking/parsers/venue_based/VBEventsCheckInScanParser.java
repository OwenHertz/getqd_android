package com.app.upincode.getqd.networking.parsers.venue_based;

import com.app.upincode.getqd.networking.parsers.BaseParser;
import com.app.upincode.getqd.networking.parsers.generic.BasePaginationParser;

import org.joda.time.DateTime;

/**
 * Created by jpnauta on 15-09-18.
 */

//Parser for sending barcode to server
public class VBEventsCheckInScanParser extends BaseParser { //VB = "Venue Based" (because it starts with '/api/venues/')
    public String code; //The scanned code
    public Integer[] events; // list of event IDs allowed for scan

    // You'll probably need a constructor to initialize the variables above ^
    public VBEventsCheckInScanParser(String theCode, Integer[] theEvents) {
        code = theCode;
        events = theEvents;
    }
}

