package com.app.upincode.getqd.networking.requests;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.app.upincode.getqd.networking.parsers.BaseParser;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Base Request class for entire project. Uses GSON to parse/serialize the data transferred
 * according to the given serializer class
 *
 * @param <T> the serializer class. Can use list (e.g. MySerializer[]) to specify an array of objects
 */
public abstract class BaseGsonRequest<T> extends JsonRequest<T> {
    protected final Map<String, String> headers;
    protected final Response.Listener<T> listener;

    /**
     * @param method        select request method (POST, PUT, etc.)
     * @param url           the URL to request from
     * @param requestBody   the body of the request
     * @param headers       request headers to use
     * @param listener      called when request is complete
     * @param errorListener called if an error occurs
     */
    public BaseGsonRequest(int method, String url, String requestBody, Map<String, String> headers,
                           Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);

        this.headers = headers;
        this.listener = listener;
    }

    /**
     * Initializes the GsonBuilder for this class. Determines how to parse various
     * elements.
     */
    public GsonBuilder getGsonBuilder() {
        return BaseParser.getGsonBuilder();
    }

    /**
     * Determines how any responses should be cached. Can be overridden to specify
     * custom caching techniques
     *
     * @param response the response sent from server
     * @return cache entry specifying how to cache
     */
    public Cache.Entry getCacheHeaders(NetworkResponse response) {
        return HttpHeaderParser.parseCacheHeaders(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }
}