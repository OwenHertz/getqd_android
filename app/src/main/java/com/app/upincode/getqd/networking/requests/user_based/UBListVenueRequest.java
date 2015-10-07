package com.app.upincode.getqd.networking.requests.user_based;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.user_based.UBVenueParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Request for GET /api/user/venues/
 */
public class UBListVenueRequest extends GsonRequest<UBVenueParser[]> {

    public UBListVenueRequest(Map<String, String> headers, Response.Listener<UBVenueParser[]> listener, Response.ErrorListener errorListener) {
        this(headers, new HashMap<String, String>(), listener, errorListener);
    }

    public UBListVenueRequest(Map<String, String> headers, HashMap<String, String> queryParams, Response.Listener<UBVenueParser[]> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, GQNetworkUtils.fullGQUrl("/api/user/venues/", queryParams), UBVenueParser[].class,
                headers, listener, errorListener);
    }
}
