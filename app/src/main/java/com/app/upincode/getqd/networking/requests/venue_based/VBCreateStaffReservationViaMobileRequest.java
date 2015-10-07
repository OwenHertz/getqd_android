package com.app.upincode.getqd.networking.requests.venue_based;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.venue_based.VBCreateStaffReservationViaMobileParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.Map;

/**
 * Request for POST /api/venue/reservations/new-reservation-via-mobile/{venue_id}/
 * <p/>
 * Created by jpnauta on 15-09-18.
 */
public class VBCreateStaffReservationViaMobileRequest extends GsonRequest<String> {
    public VBCreateStaffReservationViaMobileRequest(int venueID, VBCreateStaffReservationViaMobileParser serializer, Map<String, String> headers, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST,
                GQNetworkUtils.fullGQUrl("/api/venue/reservations/new-staff-reservation-via-mobile/" + venueID + "/"),
                serializer.toString(),
                String.class,
                headers, listener, errorListener);
    }
}
