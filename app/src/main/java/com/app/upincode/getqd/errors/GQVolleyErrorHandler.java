package com.app.upincode.getqd.errors;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import com.app.upincode.getqd.networking.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jpnauta on 15-09-17.
 */
public class GQVolleyErrorHandler extends GQBaseErrorHandler {
    protected VolleyError error;
    public List<String> errorMessageFields = Arrays.asList("detail", "error");

    public GQVolleyErrorHandler(VolleyError error) {
        super();
        this.error = error;
    }

    /**
     * Most errors sent from GetQd have a typical form. This function aims to handle
     * those common errors.
     *
     * @param json the JSON object
     * @return The message of the JSON body
     */
    public String parseGQJsonErrorMessage(JSONObject json) throws JSONException {
        for (String field : this.errorMessageFields) {
            if (json.has(field)) {
                try {
                    return json.getString(field);
                } catch (JSONException e) {
                    // If field is not a string, ignore
                }
            }
        }

        //Ideally the raw JSON shouldn't be displayed, but if no message can be
        //found, so be it
        return json.toString();
    }

    /**
     * Most errors sent from GetQd have a typical form. This function aims to handle
     * those common errors.
     *
     * @param jsonBody the un-parsed JSON
     * @return The message of the JSON body
     */
    public String parseGQJsonErrorMessage(String jsonBody) throws JSONException {
        return this.parseGQJsonErrorMessage(new JSONObject(new String(jsonBody)));
    }

    public String getMessage() {
        NetworkResponse response = this.error.networkResponse;

        if (this.error instanceof NoConnectionError || response == null) {
            // No response sent from server (404)
            return "Please check your internet connection and try again later. (No response given)";
        } else if (response.statusCode == HttpStatus.SC_BAD_REQUEST) {
            try {
                return this.parseGQJsonErrorMessage(new String(response.data));
            } catch (JSONException e) {
                e.printStackTrace();
                return "(could not parse error message)";
            }
        } else if (this.error instanceof ServerError) {
            // Internal server error (500)
            return "Sorry, something happened on our end! We will fix this as soon as possible.";
        }
        if (this.error instanceof TimeoutError) {
            return "Connection timed out with server";
        } else if (this.error instanceof AuthFailureError) {
            return "Please check your permission or try logging in again";
        }

        //TODO handle unknown errors
        return "(unknown error)";
    }

    @Override
    public void handle(Context context) {
        this.showAlert(context, "Error", this.getMessage());
    }
}
