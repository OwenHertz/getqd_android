package com.app.upincode.getqd.networking.requests.user_based;

import com.android.volley.Response;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.user_based.UBEventStatParser;
import com.app.upincode.getqd.networking.requests.GsonRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Request for GET /api/user/analytics/event-stats/
 */
public class UBPaginatedListEventStatsRequest extends GsonRequest<UBEventStatParser.PaginationParser> {

    public UBPaginatedListEventStatsRequest(Map<String, String> headers, Response.Listener<UBEventStatParser.PaginationParser> listener, Response.ErrorListener errorListener) {
        this(headers, new HashMap<String, String>(), listener, errorListener);
    }

    public UBPaginatedListEventStatsRequest(Map<String, String> headers, HashMap<String, String> queryParams, Response.Listener<UBEventStatParser.PaginationParser> listener, Response.ErrorListener errorListener) {
        super(Method.GET, GQNetworkUtils.fullGQUrl("/api/user/analytics/event-stats/", queryParams), UBEventStatParser.PaginationParser.class,
                headers, listener, errorListener);
    }
}
