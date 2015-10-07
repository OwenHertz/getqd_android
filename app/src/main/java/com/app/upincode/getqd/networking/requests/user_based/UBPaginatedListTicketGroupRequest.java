package com.app.upincode.getqd.networking.requests.user_based;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.user_based.UBTicketGroupParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Request for GET /api/user/tickets/ticket-groups/
 */
public class UBPaginatedListTicketGroupRequest extends GsonRequest<UBTicketGroupParser.PaginationParser> {
    public UBPaginatedListTicketGroupRequest(Map<String, String> headers, Response.Listener<UBTicketGroupParser.PaginationParser> listener, Response.ErrorListener errorListener) {
        this(headers, new HashMap<String, String>(), listener, errorListener);
    }

    public UBPaginatedListTicketGroupRequest(Map<String, String> headers, HashMap<String, String> queryParams, Response.Listener<UBTicketGroupParser.PaginationParser> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET,
                GQNetworkUtils.fullGQUrl("/api/user/tickets/ticket-groups/", queryParams),
                UBTicketGroupParser.PaginationParser.class,
                headers,
                listener, errorListener);
    }
}
