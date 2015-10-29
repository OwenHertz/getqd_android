package com.app.upincode.getqd.networking.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.app.upincode.getqd.networking.parsers.BaseParser;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Base Request class for entire project. Uses GSON to parse/serialize the data transferred
 * according to the given serializer class
 *
 * @param <T> the serializer class. Can use list (e.g. MySerializer[]) to specify an array of objects
 */
public class GsonRequest<T> extends BaseGsonRequest<T> {
    protected final Class<T> clazz;

    /**
     * Constructor typically used for POST/PUT/DELETE requests.
     *
     * @param method        select request method (POST, PUT, etc.)
     * @param url           the URL to request from
     * @param requestBody   the body of the request
     * @param clazz         the class to use to parse result
     * @param headers       request headers to use
     * @param listener      called when request is complete
     * @param errorListener called if an error occurs
     */
    public GsonRequest(int method, String url, String requestBody, Class<T> clazz, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, headers, listener, errorListener);

        this.clazz = clazz;
    }

    /**
     * Constructor typically used for GET requests. Does not require a request body.
     *
     * @param method        select request method (POST, PUT, etc.)
     * @param url           the URL to request from
     * @param clazz         the class to use to parse result
     * @param headers       request headers to use
     * @param listener      called when request is complete
     * @param errorListener called if an error occurs
     */
    public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, "", headers, listener, errorListener);

        this.clazz = clazz;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));

            // Parse JSON object from server response
            T obj = getGsonBuilder().create().fromJson(json, clazz);

            if (obj instanceof BaseParser) {
                ((BaseParser) obj).networkResponse = response; //Set network response for later use
            }

            return Response.success(obj, this.getCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}