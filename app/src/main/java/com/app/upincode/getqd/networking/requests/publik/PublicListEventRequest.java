package com.app.upincode.getqd.networking.requests.publik;


import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.publik.PublicEventParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Request for GET /api/user/events/
 */
public class PublicListEventRequest extends GsonRequest<PublicEventParser[]> {
    public PublicListEventRequest(Map<String, String> headers, Response.Listener<PublicEventParser[]> listener, Response.ErrorListener errorListener) {
        this(headers, new HashMap<String, String>(), listener, errorListener);
    }

    public PublicListEventRequest(Map<String, String> headers, HashMap<String, String> queryParams, Response.Listener<PublicEventParser[]> listener, Response.ErrorListener errorListener) {
        super(Method.GET,
                GQNetworkUtils.fullGQUrl("/api/public/events/", queryParams),
                PublicEventParser[].class,
                headers, listener, errorListener);
    }
}
