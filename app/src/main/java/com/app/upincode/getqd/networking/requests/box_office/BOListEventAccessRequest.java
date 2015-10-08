package com.app.upincode.getqd.networking.requests.box_office;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.box_office.BOEventAccessParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Request for GET /api/events/box-office/event-accesses/
 */
public class BOListEventAccessRequest extends GsonRequest<BOEventAccessParser[]> {

    public BOListEventAccessRequest(Map<String, String> headers, Response.Listener<BOEventAccessParser[]> listener, Response.ErrorListener errorListener) {
        this(headers, new HashMap<String, String>(), listener, errorListener);
    }

    public BOListEventAccessRequest(Map<String, String> headers, HashMap<String, String> queryParams, Response.Listener<BOEventAccessParser[]> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, GQNetworkUtils.fullGQUrl("/api/events/box-office/event-accesses/", queryParams), BOEventAccessParser[].class,
                headers, listener, errorListener);
    }
}
