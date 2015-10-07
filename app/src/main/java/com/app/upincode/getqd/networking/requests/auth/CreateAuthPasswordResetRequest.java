package com.app.upincode.getqd.networking.requests.auth;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.requests.GsonRequest;
import com.app.upincode.getqd.networking.parsers.auth.AuthPasswordResetParser;

import java.util.Map;

/**
 * Request for POST /api/auth/password/reset/
 */
public class CreateAuthPasswordResetRequest extends GsonRequest<AuthPasswordResetParser> {
    public CreateAuthPasswordResetRequest(AuthPasswordResetParser serializer, Map<String, String> headers, Response.Listener<AuthPasswordResetParser> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, GQNetworkUtils.fullGQUrl("/api/auth/password/reset/"), serializer.toString(),
                AuthPasswordResetParser.class, headers, listener, errorListener);
    }
}
