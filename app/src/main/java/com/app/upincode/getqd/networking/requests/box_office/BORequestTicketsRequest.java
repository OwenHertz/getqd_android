package com.app.upincode.getqd.networking.requests.box_office;

import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.box_office.BORequestTicketParser;
import com.app.upincode.getqd.networking.parsers.box_office.BOTicketBasketParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.Map;

/**
 * Request for POST /api/events/box-office/baskets/{id}/request/
 */
public class BORequestTicketsRequest extends GsonRequest<BOTicketBasketParser> {

    public BORequestTicketsRequest(Integer basketID, BORequestTicketParser request, Map<String, String> headers, Response.Listener<BOTicketBasketParser> listener, Response.ErrorListener errorListener) {
        super(Method.POST,
                GQNetworkUtils.fullGQUrl("/api/events/box-office/baskets/" + basketID + "/request/"),
                request.toString(),
                BOTicketBasketParser.class,
                headers, listener, errorListener);
    }
}
