package com.app.upincode.getqd.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.errors.GQVolleyErrorHandler;
import com.app.upincode.getqd.networking.GQNetworkQueue;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.generic.BlankParser;
import com.app.upincode.getqd.networking.requests.auth.CreateAuthLogoutRequest;
import com.app.upincode.getqd.usr.GQUserPropertiesUtils;

public class GQConfirmLogoutActivity extends GQBaseActivity {
    private Button btnYes;
    private Button btnNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_confirm_logout);

        //Perform logout when 'yes' is pressed
        btnYes = (Button) this.findViewById(R.id.onClickFragment_logout_buttonYes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        //Exit intent when 'no' is clicked
        btnNo = (Button) this.findViewById(R.id.onClickFragment_logout_buttonNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void logout() {
        // Perform logout request
        CreateAuthLogoutRequest request = new CreateAuthLogoutRequest(
                GQNetworkUtils.getRequestHeaders(this),
                new Response.Listener<BlankParser>() {
                    @Override
                    public void onResponse(BlankParser result) {
                        // Delete auth token
                        GQUserPropertiesUtils.deleteLoginToken(GQConfirmLogoutActivity.this);

                        //Indicate logout
                        GQActivityUtils.showToast(GQConfirmLogoutActivity.this, "Logout complete!");

                        // Go to start activity
                        Intent intent = new Intent(GQConfirmLogoutActivity.this, GQStartActivity.class);
                        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure!
                        new GQVolleyErrorHandler(error).handle(GQConfirmLogoutActivity.this);
                    }
                });

        // Add the request to the RequestQueue.
        GQNetworkQueue.getInstance(this).addToRequestQueue(request);
    }
}
