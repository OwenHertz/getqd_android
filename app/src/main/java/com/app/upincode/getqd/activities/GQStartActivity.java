package com.app.upincode.getqd.activities;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.networking.GQNetworkQueue;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.user_based.UBVenueParser;
import com.app.upincode.getqd.networking.parsers.generic.CurrentUserParser;
import com.app.upincode.getqd.networking.requests.RequestGroup;
import com.app.upincode.getqd.networking.requests.auth.CreateAuthJWTRefreshRequest;
import com.app.upincode.getqd.networking.parsers.auth.AuthJWTLoginTokenParser;
import com.app.upincode.getqd.networking.requests.user_based.UBListVenueRequest;
import com.app.upincode.getqd.networking.requests.root.RetrieveCurrentUserRequest;
import com.app.upincode.getqd.usr.GQUserPropertiesUtils;


public class GQStartActivity extends GQBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_loader);


        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        //Attempt to auto-login
        // - if successful,
        String loginToken = GQUserPropertiesUtils.getLoginToken(globalVariable);
        if (loginToken != null && !loginToken.isEmpty()) {
            CreateAuthJWTRefreshRequest.refresh(this,
                    new Response.Listener<AuthJWTLoginTokenParser>() {
                        @Override
                        public void onResponse(AuthJWTLoginTokenParser result) {
                            GQUserPropertiesUtils.setLoginToken(GQStartActivity.this, result.token);

                            loadUserData();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            beginLoginActivity();
                        }
                    });

        }
        else {
            beginLoginActivity();
        }
    }

    public void loadUserData () {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        final RequestGroup requestGroup = new RequestGroup();

        //Retrieve user's venues
        UBListVenueRequest userVenueRequest = new UBListVenueRequest(
                GQNetworkUtils.getRequestHeaders(this),
                new Response.Listener<UBVenueParser[]>() {
                    @Override
                    public void onResponse(UBVenueParser[] result) {
                        globalVariable.setUserVenues(result);
                        requestGroup.success();
                    }
                }, requestGroup);

        //Retrieve current user data
        RetrieveCurrentUserRequest currentUserRequest = new RetrieveCurrentUserRequest(
                GQNetworkUtils.getRequestHeaders(this),
                new Response.Listener<CurrentUserParser>() {
                    @Override
                    public void onResponse(CurrentUserParser result) {
                        globalVariable.setCurrentUser(result);
                        requestGroup.success();
                    }
                }, requestGroup);

        //After all user data is loaded, begin main activity
        requestGroup.register(userVenueRequest);
        requestGroup.register(currentUserRequest);
        requestGroup.begin(
                GQNetworkQueue.getInstance(this).getRequestQueue(),
                new RequestGroup.Listener() {
                    @Override
                    public void success() {
                        beginMainActivity();
                    }
                });
    }

    public void beginMainActivity() {
        Intent intent = new Intent(this, GQMainActivity.class);
        intent.putExtra("target", getString(R.string.Frag_Target_Staff));
        startActivity(intent);
    }

    public void beginLoginActivity() {
        Intent intent = new Intent(this, GQLoginActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
