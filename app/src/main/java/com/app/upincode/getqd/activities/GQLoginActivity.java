package com.app.upincode.getqd.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.errors.GQUnexpectedErrorHandler;
import com.app.upincode.getqd.errors.GQVolleyErrorHandler;
import com.app.upincode.getqd.networking.GQNetworkQueue;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.requests.auth.CreateAuthPasswordResetRequest;
import com.app.upincode.getqd.networking.requests.auth.CreateAuthJWTLoginRequest;
import com.app.upincode.getqd.networking.parsers.auth.AuthJWTLoginParser;
import com.app.upincode.getqd.networking.parsers.auth.AuthJWTLoginTokenParser;
import com.app.upincode.getqd.networking.parsers.auth.AuthPasswordResetParser;
import com.app.upincode.getqd.usr.GQUserPropertiesUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class GQLoginActivity extends GQBaseActivity {
    ViewFlipper viewFlipper;
    EditText pw2 = null;
    boolean pw2Once = true;
    Button buttonfl = null;
    // Calling Application class (see application tag in AndroidManifest.xml)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final GlobalClass globalVariable = GQActivityUtils.getGlobalClass(this);
        //Set name and email in global/application context
        globalVariable.setCsrfToken("HerbRushPutThisTokenIn");
        globalVariable.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6Ikx1Y2FzIE1jQ2FydGh5Iiwib3JpZ19pYXQiOjE0MzgyOTA5NzYsInVzZXJfaWQiOjEyLCJlbWFpbCI6Imx1Y2FzYy5tY2NhcnRoeUBnbWFpbC5jb20iLCJleHAiOjE0MzgzNzczNzZ9.1Fv2e4XHycS1t3Fk2rZ_jOsXO0UIFKlFn1uPPmkSHw4");

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper1);
        viewFlipper.showNext();

        //  GQLog.dObj(this, "Using PathName of     "+ Environment.getDataDirectory().getAbsolutePath());
        pw2 = (EditText) findViewById(R.id.GQStartActivity_textPassword);

        /*
        The xml describes which views have the buttons.  we just hook up the
        buttons to code.
         */
        buttonfl = (Button) findViewById(R.id.GQStartActivity_buttonFL);
        buttonfl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // establish a model and request login.
                TextView em = (TextView) findViewById(R.id.GQStartActivity_textEmail);

                GQLoginActivity.this.forgotPassword(em.getText().toString());
            }
        });

        final Button button = (Button) findViewById(R.id.GQStartActivity_buttonEL);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
            }
        });

        final Button button1 = (Button) findViewById(R.id.GQStartActivity_buttonRE);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GQProfileActivity.class);
                startActivity(intent);
            }
        });

        final ImageButton button2 = (ImageButton) findViewById(R.id.GQStartActivity_imageButton1);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //viewFlipper.showNext();
            }
        });

        final Button buttonLogin = (Button) findViewById(R.id.GQStartActivity_buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                Clicking the login with email button we come here. We are going to logon
                with username and password from the screen.  If we are successful, it
                will give us a bunch of information about the  user. Id,...
                 */
                TextView em = (TextView) findViewById(R.id.GQStartActivity_textEmail);
                TextView pw = (TextView) findViewById(R.id.GQStartActivity_textPassword);

                String emailAddress = em.getText().toString();
                String password = pw.getText().toString();

                //TODO clean up/remove
//                globalVariable.setProfileValue(getApplicationContext(), "LoggedIn", "no");                // we may want to consult this field in the future.  If yes, Autologin
//                globalVariable.setProfileValue(getApplicationContext(), "LoginID", emailAddress.trim());
//                globalVariable.setProfileValue(getApplicationContext(), "Password", password.trim());

                login(emailAddress, password);

            }
        });

    }

    private void forgotPassword(String email) {
        //Build request body
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
        } catch (JSONException e) {
            new GQUnexpectedErrorHandler(e).handle(this);
        }

        // Perform request
        AuthPasswordResetParser serializer = new AuthPasswordResetParser(email);
        CreateAuthPasswordResetRequest request = new CreateAuthPasswordResetRequest(
                serializer, GQNetworkUtils.getRequestHeaders(this),
                new Response.Listener<AuthPasswordResetParser>() {
                    @Override
                    public void onResponse(AuthPasswordResetParser json) {
                        //Success!
                        GQActivityUtils.showAlert(GQLoginActivity.this, "Success",
                                "GetQd will send you an email with instructions on resetting your password");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure!
                        new GQVolleyErrorHandler(error).handle(GQLoginActivity.this);
                    }
                });
        // Add the request to the RequestQueue.
        GQNetworkQueue.getInstance(this).addToRequestQueue(request);
    }

    public void login(String emailAddress, String password) {

        AuthJWTLoginParser serializer = new AuthJWTLoginParser(emailAddress, password);
        Request request = new CreateAuthJWTLoginRequest(
                serializer, GQNetworkUtils.getRequestHeaders(this),
                new Response.Listener<AuthJWTLoginTokenParser>() {
                    @Override
                    public void onResponse(AuthJWTLoginTokenParser result) {
                        //TODO use users/me? Change?
//                            final GlobalClass globalVariable = GQActivityUtils.getGlobalClass(this);
//
//                            //we are going to save some information for the profile.
//                            String userName = json.getString("first_name") + "  " + json.getString("last_name");
//                            String email = json.getString("email");
//                            globalVariable.setProfileValue(getApplicationContext(), "UserName", userName.trim());
//                            globalVariable.setProfileValue(getApplicationContext(), "Email", email.trim());  //LoggedIn
//                            globalVariable.setProfileValue(getApplicationContext(), "LoggedIn", "yes");
//
//                            globalVariable.setloginJSON(json.toString());  // save the JSON for future usage

                        //Store JWT token given
                        GQUserPropertiesUtils.setLoginToken(GQLoginActivity.this, result.token);

                        //Delete user cache - ensures that data cached from previous sessions is refreshed
                        GQUserPropertiesUtils.clearCache(GQLoginActivity.this);

                        // Login succeeded! Go back to start activity
                        Intent intent = new Intent(GQLoginActivity.this, GQStartActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure!
                        new GQVolleyErrorHandler(error).handle(GQLoginActivity.this);
                    }
                });
        GQNetworkQueue.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
