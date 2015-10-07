package com.app.upincode.getqd.networking.requests.publik;


import com.android.volley.Request;
import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.publik.PublicVenueParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Request for GET /api/user/venues/
 */
public class PublicListVenueRequest extends GsonRequest<PublicVenueParser[]> {
    public PublicListVenueRequest(Map<String, String> headers, Response.Listener<PublicVenueParser[]> listener, Response.ErrorListener errorListener) {
        this(headers, new HashMap<String, String>(), listener, errorListener);
    }

    public PublicListVenueRequest(Map<String, String> headers, HashMap<String, String> queryParams, Response.Listener<PublicVenueParser[]> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET,
                GQNetworkUtils.fullGQUrl("/api/public/venues/", queryParams),
                PublicVenueParser[].class,
                headers, listener, errorListener);
    }
}
