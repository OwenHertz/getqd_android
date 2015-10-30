package com.app.upincode.getqd.networking.requests.venue_based;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.venue_based.VBEventsCheckInScanParser;
import com.app.upincode.getqd.networking.parsers.venue_based.VBEventsTicketHistoryParser;
import com.app.upincode.getqd.networking.parsers.venue_based.VBReservationReasonParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Request for GET /api/venue/{venue_id}/reservations/reasons/
 */
public class VBEventsCheckInScanRequest extends GsonRequest<VBEventsTicketHistoryParser> {
    public VBEventsCheckInScanRequest(int venueID, VBEventsCheckInScanParser serializer, Map<String, String> headers, Response.Listener<VBEventsTicketHistoryParser> listener, Response.ErrorListener errorListener) {
        super(
                Method.POST,  // Perform POST request
                GQNetworkUtils.fullGQUrl("/api/venue/" + venueID + "/events/check-in/actions/scan/"), //URL sent to server
                serializer.toString(),  // Add request JSON to body of request
                VBEventsTicketHistoryParser.class,  // Parser for JSON response
                headers, // Contains information about user, login information, etc.
                listener, //Listener called when successful request completed
                errorListener // Listener called if request fails
        );
    }
}