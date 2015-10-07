package com.app.upincode.getqd.networking.requests.venue_based;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.venue_based.VBReservationReasonParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Request for GET /api/venue/{venue_id}/reservations/reasons/
 */
public class VBListReservationReasonRequest extends GsonRequest<VBReservationReasonParser[]> {
    public VBListReservationReasonRequest(int venueID, Map<String, String> headers, Response.Listener<VBReservationReasonParser[]> listener, Response.ErrorListener errorListener) {
        this(venueID, headers, new HashMap<String, String>(), listener, errorListener);
    }

    public VBListReservationReasonRequest(int venueID, Map<String, String> headers, HashMap<String, String> queryParams,
                                          Response.Listener<VBReservationReasonParser[]> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET,
                GQNetworkUtils.fullGQUrl("/api/venue/" + venueID + "/reservations/reasons/", queryParams),
                VBReservationReasonParser[].class,
                headers, listener, errorListener);
    }
}
