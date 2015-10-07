package com.app.upincode.getqd.networking;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.HttpHeaderParser;
import com.app.upincode.getqd.config.GQConfig;
import com.app.upincode.getqd.usr.GQUserPropertiesUtils;

import android.content.Context;
import android.net.Uri;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by jpnauta on 15-09-17.
 */
public class GQNetworkUtils {
    /**
     * Converts an absolute endpoint to the full URL (including the base URL)
     * <p/>
     *
     * @param endpoint the absolute endpoint used
     * @param queryParams query parameters to add to the URL, if any
     * @return the full URL
     */
    public static String fullGQUrl(String endpoint, HashMap<String, String> queryParams) {
        Uri.Builder builder = Uri.parse(GQConfig.PROPERTIES.BASE_URL)
                .buildUpon()
                .path(endpoint);

        Iterator it = queryParams.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
            builder.appendQueryParameter(pair.getKey(), pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }

        return builder.build().toString();
    }

    /**
     * Converts an absolute endpoint to the full URL (including the base URL)
     * <p/>
     * Example:
     * fullGQUrl("/api/my/path/") => "https://getqd.com/api/my/path/"
     *
     * @param endpoint the absolute endpoint used
     * @return the full URL
     */
    public static String fullGQUrl(String endpoint) {
        return fullGQUrl(endpoint, new HashMap<String, String>());
    }

    /**
     * Extracts a {@link Cache.Entry} from a {@link NetworkResponse}.
     * Cache-control headers are ignored.
     *
     * @param response The network response to parse headers from
     * @return a cache entry for the given response, or null if the response is not cacheable.
     */
    public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response, long cacheHitButRefreshed, long cacheExpired) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;
        long serverDate = 0;
        String serverEtag = null;
        String headerValue;

        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }

        serverEtag = headers.get("ETag");

        final long softExpire = now + cacheHitButRefreshed;
        final long ttl = now + cacheExpired;

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = ttl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;

        return entry;
    }

    public static Map<String, String> getRequestHeaders(Context context) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "JWT " + GQUserPropertiesUtils.getLoginToken(context));
        return headers;
    }
}
