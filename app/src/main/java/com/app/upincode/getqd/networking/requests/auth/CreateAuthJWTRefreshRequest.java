package com.app.upincode.getqd.networking.requests.auth;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkQueue;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.requests.GsonRequest;
import com.app.upincode.getqd.networking.parsers.auth.AuthJWTLoginTokenParser;
import com.app.upincode.getqd.usr.GQUserPropertiesUtils;

import java.util.Map;

/**
 * Request for POST /api/auth/jwt/refresh/
 */
public class CreateAuthJWTRefreshRequest extends GsonRequest<AuthJWTLoginTokenParser> {

    public CreateAuthJWTRefreshRequest(AuthJWTLoginTokenParser serializer, Map<String, String> headers, Response.Listener<AuthJWTLoginTokenParser> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, GQNetworkUtils.fullGQUrl("/api/auth/jwt/refresh/"), serializer.toString(),
                AuthJWTLoginTokenParser.class, headers, listener, errorListener);
    }

    /**
     * Refreshes the user's login token
     */
    public static void refresh(
            final Context context, final Response.Listener<AuthJWTLoginTokenParser> listener, Response.ErrorListener errorListener) {
        String token = GQUserPropertiesUtils.getLoginToken(context);

        if (token != null) {
            AuthJWTLoginTokenParser serializer = new AuthJWTLoginTokenParser(token);

            Request request = new CreateAuthJWTRefreshRequest(serializer, GQNetworkUtils.getRequestHeaders(context), listener, errorListener);
            GQNetworkQueue.getInstance(context).addToRequestQueue(request);
        }
    }
}
