package com.app.upincode.getqd.networking.requests.root;

import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.generic.CurrentUserParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.Map;

/**
 * Request for PUT /api/users/me/
 */
public class UpdateCurrentUserRequest extends GsonRequest<CurrentUserParser> {
    public UpdateCurrentUserRequest(CurrentUserParser user, Map<String, String> headers, Response.Listener<CurrentUserParser> listener, Response.ErrorListener errorListener) {
        super(Method.PUT, GQNetworkUtils.fullGQUrl("/api/users/me/"), user.toString(), CurrentUserParser.class,
                headers, listener, errorListener);
    }
}
