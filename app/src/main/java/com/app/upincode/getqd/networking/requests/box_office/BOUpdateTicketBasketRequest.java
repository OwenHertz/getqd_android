package com.app.upincode.getqd.networking.requests.box_office;

import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.box_office.BOTicketBasketParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.Map;

/**
 * Request for PUT /api/events/box-office/baskets/{id}/
 */
public class BOUpdateTicketBasketRequest extends GsonRequest<BOTicketBasketParser> {

    public BOUpdateTicketBasketRequest(BOTicketBasketParser basket, Map<String, String> headers, Response.Listener<BOTicketBasketParser> listener, Response.ErrorListener errorListener) {
        super(Method.PUT,
                GQNetworkUtils.fullGQUrl("/api/events/box-office/baskets/" + basket.id + "/"),
                basket.toString(),
                BOTicketBasketParser.class,
                headers, listener, errorListener);
    }
}
