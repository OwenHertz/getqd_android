package com.app.upincode.getqd.networking.requests.venue_based;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.venue_based.VBReservationParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Request for GET /api/venue/{venue_id}/reservations/
 * <p/>
 * Created by jpnauta on 15-09-18.
 */
public class VBListReservationRequest extends GsonRequest<VBReservationParser[]> {

    public VBListReservationRequest(int venueID, Map<String, String> headers, Response.Listener<VBReservationParser[]> listener, Response.ErrorListener errorListener) {
        this(venueID, headers, new HashMap<String, String>(), listener, errorListener);
    }

    public VBListReservationRequest(int venueID, Map<String, String> headers, HashMap<String, String> queryParams,
                                    Response.Listener<VBReservationParser[]> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET,
                GQNetworkUtils.fullGQUrl("/api/venue/" + venueID + "/reservations/", queryParams),
                VBReservationParser[].class,
                headers, listener, errorListener);
    }
}
