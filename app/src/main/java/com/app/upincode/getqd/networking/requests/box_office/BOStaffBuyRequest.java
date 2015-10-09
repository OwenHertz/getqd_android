package com.app.upincode.getqd.networking.requests.box_office;

import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.box_office.BOStaffBuyParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * Request for POST /api/events/box-office/baskets/{id}/staff-buy/
 */
public class BOStaffBuyRequest extends GsonRequest<JsonObject> {

    public BOStaffBuyRequest(Integer basketID, BOStaffBuyParser request, Map<String, String> headers, Response.Listener<JsonObject> listener, Response.ErrorListener errorListener) {
        super(Method.POST,
                GQNetworkUtils.fullGQUrl("/api/events/box-office/baskets/" + basketID + "/staff-buy/"),
                request.toString(),
                JsonObject.class,
                headers, listener, errorListener);
    }
}
