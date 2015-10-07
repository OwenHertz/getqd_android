package com.app.upincode.getqd.networking.requests.auth;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.requests.GsonRequest;
import com.app.upincode.getqd.networking.parsers.auth.AuthJWTLoginParser;
import com.app.upincode.getqd.networking.parsers.auth.AuthJWTLoginTokenParser;

import java.util.Map;

/**
 * Request for POST /api/auth/jwt/
 */
public class CreateAuthJWTLoginRequest extends GsonRequest<AuthJWTLoginTokenParser> {
    public CreateAuthJWTLoginRequest(AuthJWTLoginParser serializer, Map<String, String> headers, Response.Listener<AuthJWTLoginTokenParser> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, GQNetworkUtils.fullGQUrl("/api/auth/jwt/"), serializer.toString(),
                AuthJWTLoginTokenParser.class, headers, listener, errorListener);
    }
}
