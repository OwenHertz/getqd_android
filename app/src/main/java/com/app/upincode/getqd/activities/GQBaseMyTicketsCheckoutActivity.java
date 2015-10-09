package com.app.upincode.getqd.activities;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.upincode.getqd.errors.GQVolleyErrorHandler;
import com.app.upincode.getqd.networking.GQNetworkQueue;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.box_office.BOEventAccessParser;
import com.app.upincode.getqd.networking.parsers.box_office.BOStaffBuyParser;
import com.app.upincode.getqd.networking.parsers.box_office.BOTicketBasketParser;
import com.app.upincode.getqd.networking.requests.box_office.BOStaffBuyRequest;
import com.google.gson.JsonObject;

public class GQBaseMyTicketsCheckoutActivity extends GQBaseActivity {
    public static final String BASKET = "basket";
    public static final String EVENT_ACCESS = "event_access";

    protected BOTicketBasketParser basket;
    protected BOEventAccessParser eventAccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.activity_gqinteract);

        Intent intent = getIntent();
        basket = BOTicketBasketParser.fromString(BOTicketBasketParser.class, intent.getStringExtra(BASKET));
        eventAccess = BOEventAccessParser.fromString(BOEventAccessParser.class, intent.getStringExtra(EVENT_ACCESS));
    }

    public void performBasketPurchase(BOStaffBuyParser parser) {
        BOStaffBuyRequest request = new BOStaffBuyRequest(
                this.basket.id, parser, GQNetworkUtils.getRequestHeaders(this),
                new Response.Listener<JsonObject>() {
                    @Override
                    public void onResponse(JsonObject json) {
                        //Success!
                        GQActivityUtils.showToast(getApplicationContext(), "Sale completed successfully!");
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure!
                        new GQVolleyErrorHandler(error).handle(GQBaseMyTicketsCheckoutActivity.this);
                    }
                });
        // Add the request to the RequestQueue.
        GQNetworkQueue.getInstance(this).addToRequestQueue(request);
    }
}
