package com.app.upincode.getqd.networking.requests.box_office;

import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.box_office.BOEventAccessParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Request for GET /api/events/box-office/event-accesses/{slug}/
 */
public class BORetrieveEventAccessRequest extends GsonRequest<BOEventAccessParser> {

    public BORetrieveEventAccessRequest(String slug, Map<String, String> headers, Response.Listener<BOEventAccessParser> listener, Response.ErrorListener errorListener) {
        this(slug, headers, new HashMap<String, String>(), listener, errorListener);
    }

    public BORetrieveEventAccessRequest(String slug, Map<String, String> headers, HashMap<String, String> queryParams, Response.Listener<BOEventAccessParser> listener, Response.ErrorListener errorListener) {
        super(Method.GET,
                GQNetworkUtils.fullGQUrl("/api/events/box-office/event-accesses/" + slug + "/", queryParams),
                BOEventAccessParser.class,
                headers, listener, errorListener);
    }
}
