package com.app.upincode.getqd.networking.requests.auth;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.requests.GsonRequest;
import com.app.upincode.getqd.networking.parsers.generic.BlankParser;

import java.util.Map;

/**
 * Request for POST /api/auth/logout/
 */
public class CreateAuthLogoutRequest extends GsonRequest<BlankParser> {
    public CreateAuthLogoutRequest(Map<String, String> headers, Response.Listener<BlankParser> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, GQNetworkUtils.fullGQUrl("/api/auth/logout/"), BlankParser.class,
                headers, listener, errorListener);
    }
}
