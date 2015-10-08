package com.app.upincode.getqd.networking.requests.root;

import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.generic.CurrentUserParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.Map;

/**
 * Request for GET /api/users/me/
 * <p/>
 * Results are cached, since the user properties rarely change
 * <p/>
 * Created by jpnauta on 15-09-18.
 */
public class RetrieveCurrentUserRequest extends GsonRequest<CurrentUserParser> {
    public RetrieveCurrentUserRequest(Map<String, String> headers, Response.Listener<CurrentUserParser> listener, Response.ErrorListener errorListener) {
        super(Method.GET, GQNetworkUtils.fullGQUrl("/api/users/me/"), CurrentUserParser.class,
                headers, listener, errorListener);
    }
}
