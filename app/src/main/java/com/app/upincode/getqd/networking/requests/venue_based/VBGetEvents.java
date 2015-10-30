package com.app.upincode.getqd.networking.requests.venue_based;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.venue_based.VBEventsCheckInScanParser;
import com.app.upincode.getqd.networking.parsers.venue_based.VBGetEventsParser;
import com.app.upincode.getqd.networking.parsers.venue_based.VBEventsTicketHistoryParser;
import com.app.upincode.getqd.networking.parsers.venue_based.VBReservationReasonParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.HashMap;
import java.util.Map;


/****
        * Request for GET /api/venue/{venue_id}/events/
        */
public class VBGetEvents extends GsonRequest<VBGetEventsParser[]> {
    public VBGetEvents(int venueID, Map<String, String> headers, Response.Listener<VBGetEventsParser[]> listener, Response.ErrorListener errorListener) {
        super(
                Method. GET,  // Perform GET request
                GQNetworkUtils.fullGQUrl("/api/venue/" + venueID + "/events/"), //URL sent to server
                null,  // No request body sent to server
                VBGetEventsParser[].class,  // Parser for JSON response
                headers, // Contains information about user, login information, etc.
                listener, //Listener called when successful request completed
                errorListener // Listener called if request fails
        );
    }
}

