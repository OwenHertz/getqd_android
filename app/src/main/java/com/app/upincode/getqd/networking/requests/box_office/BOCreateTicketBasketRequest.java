package com.app.upincode.getqd.networking.requests.box_office;

import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.box_office.BOTicketBasketParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.Map;

/**
 * Request for POST /api/events/box-office/baskets/
 */
public class BOCreateTicketBasketRequest extends GsonRequest<BOTicketBasketParser> {

    public BOCreateTicketBasketRequest(Map<String, String> headers, Response.Listener<BOTicketBasketParser> listener, Response.ErrorListener errorListener) {
        super(Method.POST,
                GQNetworkUtils.fullGQUrl("/api/events/box-office/baskets/"),
                BOTicketBasketParser.class,
                headers, listener, errorListener);
    }
}
