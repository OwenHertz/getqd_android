package com.app.upincode.getqd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;


import com.app.upincode.getqd.config.GQConfig;
import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.models.GQBaseModel;

import org.json.JSONArray;
import org.json.JSONObject;


public class GQStartActivity extends ActionBarActivity {
    ViewFlipper viewFlipper;
    EditText pw2 = null;
    boolean pw2Once = true;
    Button buttonfl = null;
    // Calling Application class (see application tag in AndroidManifest.xml)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.activity_main);


        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        //Set name and email in global/application context
        globalVariable.setName("GetQD Copyright 2015,2016");
        globalVariable.setEmail("herbrush@gmail.com");
        globalVariable.setPassword("maniac");
        globalVariable.setCsrfToken("HerbRushPutThisTokenIn");
        globalVariable.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6Ikx1Y2FzIE1jQ2FydGh5Iiwib3JpZ19pYXQiOjE0MzgyOTA5NzYsInVzZXJfaWQiOjEyLCJlbWFpbCI6Imx1Y2FzYy5tY2NhcnRoeUBnbWFpbC5jb20iLCJleHAiOjE0MzgzNzczNzZ9.1Fv2e4XHycS1t3Fk2rZ_jOsXO0UIFKlFn1uPPmkSHw4");

        globalVariable.setPropertiesFileName("GetQD.Properties");  // for both reading and writing

        if ((globalVariable.getProfileValue(getApplicationContext(), "LoggedIn") != null)) {                           // This means we can find the properties file (not the first time through here.
            if ((globalVariable.getProfileValue(getApplicationContext(), "LoggedIn").equals("yes"))) {                  // If yes, Autologin
                login(globalVariable.getProfileValue(getApplicationContext(), "LoginID"),
                        globalVariable.getProfileValue(getApplicationContext(), "Password"));
                GQLog.dObj(this, "Attempted to auto login.");

            }
        }

        viewFlipper = (ViewFlipper) findViewById(com.app.upincode.getqd.R.id.viewFlipper1);
        viewFlipper.showNext();

        //  GQLog.dObj(this, "Using PathName of     "+ Environment.getDataDirectory().getAbsolutePath());
        pw2 = (EditText) findViewById(com.app.upincode.getqd.R.id.GQStartActivity_textPassword);
        pw2.setOnKeyListener(new EditText.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                GQLog.dObj(this, "Key typed in pw field");
                if (pw2Once == true) {
                    pw2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    pw2.setText("");
                    pw2Once = false;
                }

                return false;
            }
        });

        /*
        The xml describes which views have the buttons.  we just hook up the
        buttons to code.
         */
        buttonfl = (Button) findViewById(com.app.upincode.getqd.R.id.GQStartActivity_buttonFL);
        buttonfl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // establish a model and request login.
                TextView em = (TextView) findViewById(com.app.upincode.getqd.R.id.GQStartActivity_textEmail);
                String URL_Login = new String("api/users/password_reset/");
                MyBaseModelForgotLogin mybmfl = new MyBaseModelForgotLogin(getApplicationContext());
                mybmfl.execute("Post", URL_Login, "2", "email", em.getText().toString());// Instantiate the custom HttpClient
            }
        });

        final Button button = (Button) findViewById(com.app.upincode.getqd.R.id.GQStartActivity_buttonEL);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
            }
        });

        final Button button1 = (Button) findViewById(com.app.upincode.getqd.R.id.GQStartActivity_buttonRE);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenty = new Intent(getApplicationContext(), GQProfileActivity.class);
                startActivity(intenty);
            }
        });

        final ImageButton button2 = (ImageButton) findViewById(com.app.upincode.getqd.R.id.GQStartActivity_imageButton1);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //viewFlipper.showNext();
            }
        });

        final Button buttonLogin = (Button) findViewById(com.app.upincode.getqd.R.id.GQStartActivity_buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                Clicking the login with email button we come here. We are going to logon
                with username and password from the screen.  If we are successful, it
                will give us a bunch of information about the  user. Id,...
                 */
                TextView em = (TextView) findViewById(com.app.upincode.getqd.R.id.GQStartActivity_textEmail);
                TextView pw = (TextView) findViewById(com.app.upincode.getqd.R.id.GQStartActivity_textPassword);

                String emailAddress = em.getText().toString();
                String password = pw.getText().toString();

                globalVariable.setProfileValue(getApplicationContext(), "LoggedIn", "no");                // we may want to consult this field in the future.  If yes, Autologin
                globalVariable.setProfileValue(getApplicationContext(), "LoginID", emailAddress.trim());
                globalVariable.setProfileValue(getApplicationContext(), "Password", password.trim());

                login(emailAddress, password);

            }
        });

    }

    public void login(String emailAddress, String password) {
        // establish a model and request login.
        String URL_Login = new String("api/login/");
        MyBaseModel mybm = new MyBaseModel(getApplicationContext());
        mybm.execute("Post", URL_Login, "4", "email", emailAddress, "password", password);// Instantiate the custom HttpClient
    }

    public class MyBaseModel extends GQBaseModel {

        public MyBaseModel(Context context) {
            super(context);
        }


        public void updateView(Boolean retCode, int code, String JSON) {
             GQLog.dObj(this, "doing updateView retcode = " + retCode + "Code = " + code);
            if (retCode == false) {
                GQLog.dObj(this, "Unable to Access the Website");
                return;
            }
            if (code == 200) {  //  JSON Looks like the following
                try {
                    final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                    JSONObject loginJSONObject = new JSONObject(JSON);
                    GQLog.dObj(this, "=========> USER ID IS= " + loginJSONObject.getString("id"));

                    // we are going to save some information for the profile.
                    String UserName = loginJSONObject.getString("first_name") + "  " + loginJSONObject.getString("last_name");
                    String Email = loginJSONObject.getString("email");
                    globalVariable.setProfileValue(getApplicationContext(), "UserName", UserName.trim());
                    globalVariable.setProfileValue(getApplicationContext(), "Email", Email.trim());  //LoggedIn
                    globalVariable.setProfileValue(getApplicationContext(), "LoggedIn", "yes");
                } catch (Exception e) {
                    GQLog.dObj(this, "Trouble parsing the JSON data=" + JSON);
                }
                //Log.d("GQStartActivity", "Looking to print JSON");
                //printJSON(JSON);  // Looks Like the following

                //{"id": 12, "last_login": "2015-07-26T19:06:38.102Z", "first_name": "Lucas", "last_name": "McCarthy",
                // "email": "lucasc.mccarthy@gmail.com", "updated_on": "2015-07-27T09:30:36.392Z", "is_admin": true, "is_staff": true,
                // "is_active": true, "birthday": null, "email_hex": "f0b9d14dbf2e21243553921abfb259e9",
                // "venue_employments": [{"created": "2014-11-12T18:05:49.508Z", "updated": "2014-11-12T18:05:49.508Z", "venue": 15,
                // "group": 58, "is_owner": true, "receives_daily_reports": false, "receives_ticket_sale_emails": false,
                // "receives_alarm_SMS": false, "notify": false}, {"created": null, "updated": null, "venue": 12, "group": 13,
                // "is_owner": true, "receives_daily_reports": false, "receives_ticket_sale_emails": false,
                // "receives_alarm_SMS": false, "notify": false}, {"created": null, "updated": null, "venue": 18, "group": 44,
                // "is_owner": true, "receives_daily_reports": true, "receives_ticket_sale_emails": false,
                // "receives_alarm_SMS": false, "notify": true}]}
                final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                globalVariable.setloginJSON(JSON);  // save the JSON for future usage

                // we going to the main activity the target of staff
                Intent intenty = new Intent(getApplicationContext(), GQMainActivity.class);
                intenty.putExtra("target", getString(com.app.upincode.getqd.R.string.Frag_Target_Staff)); // start with the Staff fragment
                startActivity(intenty);
            } else if (code == 400) {
                GQLog.dObj(this, "Getting 400  Error from the website" + JSON);
                // Username or password false, display and an error

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQStartActivity.this);

                dlgAlert.setMessage("400 Error Message");
                dlgAlert.setTitle(JSON);
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

            } else {
                GQLog.dObj(this, "Getting Error From Website =  " + code);


                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQStartActivity.this);

                dlgAlert.setMessage("Network Problem Code = " + code);
                dlgAlert.setTitle("Server says " + JSON);
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

            }

        }

        public void printJSON(String JSON) {
            try {
                 GQLog.dObj(this, "Trying to print JSON.");
                JSONObject loginJSONObject = new JSONObject(JSON);
                 GQLog.dObj(this, "Parse id = " + loginJSONObject.getString("id"));
                 GQLog.dObj(this, "Parse last_login = " + loginJSONObject.getString("last_login"));
                 GQLog.dObj(this, "Parse first_name = " + loginJSONObject.getString("first_name"));
                 GQLog.dObj(this, "Parse last_name = " + loginJSONObject.getString("last_name"));
                 GQLog.dObj(this, "Parse email = " + loginJSONObject.getString("email"));
                 GQLog.dObj(this, "Parse updated_on = " + loginJSONObject.getString("updated_on"));
                boolean is_admin = loginJSONObject.getBoolean("is_admin");
                 GQLog.dObj(this, "Parse is_admin = " + is_admin);
                boolean is_staff = loginJSONObject.getBoolean("is_staff");
                 GQLog.dObj(this, "Parse is_staff = " + is_staff);
                boolean is_active = loginJSONObject.getBoolean("is_active");
                 GQLog.dObj(this, "Parse is_active = " + is_active);
                 GQLog.dObj(this, "Parse birthday = " + loginJSONObject.getString("birthday"));
                 GQLog.dObj(this, "Parse email_hex " + loginJSONObject.getString("email_hex"));
                JSONArray jArray = loginJSONObject.getJSONArray("venue_employments");
                int n;
                for (n = 0; n < jArray.length(); n = n + 1) {
                    JSONObject venue = jArray.getJSONObject(n);

                     GQLog.dObj(this, "    venue:" + n + " Parse created " + venue.getString("created"));
                     GQLog.dObj(this, "    venue:" + n + " Parse updated " + venue.getString("updated"));
                    int venue1 = venue.getInt("venue");
                     GQLog.dObj(this, "    venue:" + n + " Parse venue = " + venue1);
                    int group = venue.getInt("group");
                     GQLog.dObj(this, "    venue:" + n + " Parse group = " + group);
                    boolean is_owner = venue.getBoolean("is_owner");
                     GQLog.dObj(this, "    venue:" + n + " Parse  is_owner = " + is_owner);
                    boolean receives_daily_reports = venue.getBoolean("receives_daily_reports");
                     GQLog.dObj(this, "    venue:" + n + " Parse  receives_daily_reports = " + receives_daily_reports);
                    boolean receives_ticket_sale_emails = venue.getBoolean("receives_ticket_sale_emails");
                     GQLog.dObj(this, "    venue:" + n + " Parse  receives_ticket_sale_emails = " + receives_ticket_sale_emails);

                    boolean receives_alarm_SMS = venue.getBoolean("receives_alarm_SMS");
                     GQLog.dObj(this, "    venue:" + n + " Parse  receives_alarm_SMS = " + receives_alarm_SMS);

                    boolean notify = venue.getBoolean("notify");
                     GQLog.dObj(this, "    venue:" + n + " Parse  notify = " + notify);
                     GQLog.dObj(this, "======================================================");
                }
            } catch (Exception e) {
                 GQLog.dObj(this, "Problem Printing" + e.toString());
            }
        }
    }

    public class MyBaseModelForgotLogin extends GQBaseModel {

        public MyBaseModelForgotLogin(Context context) {
            super(context);
        }


        public void updateView(Boolean retCode, int code, String JSON) {
             GQLog.dObj(this, "doing updateView retcode for Forgot Login= " + retCode + "Code = " + code);
            if (retCode == false) {
                GQLog.dObj(this, "Unable to Access the Website");
                return;
            }
            if (code == 200) {  //  JSON Looks like the following
                try {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQStartActivity.this);

                    dlgAlert.setMessage("GetQ'd will send you an email with instructions on reseting your password");
                    dlgAlert.setTitle("Reguest Succeeded");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();

                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                } catch (Exception e) {
                    GQLog.dObj(this, "Trouble parsing the JSON data=" + JSON);
                }

            } else if (code == 400) {
                GQLog.dObj(this, "Getting 400  Error from the website" + JSON);
                // Username or password false, display and an error

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQStartActivity.this);

                dlgAlert.setMessage("400 Error Message");
                dlgAlert.setTitle(JSON);
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

            } else {
                GQLog.dObj(this, "Getting Error From Website =  " + code);


                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQStartActivity.this);

                dlgAlert.setMessage("Network Problem Code = " + code);
                dlgAlert.setTitle("Server says " + JSON);
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

            }

        }

        public void printJSON(String JSON) {
            try {
                 GQLog.dObj(this, "Trying to print JSON.");
                JSONObject loginJSONObject = new JSONObject(JSON);
                 GQLog.dObj(this, "Parse id = " + loginJSONObject.getString("id"));
                 GQLog.dObj(this, "Parse last_login = " + loginJSONObject.getString("last_login"));
                 GQLog.dObj(this, "Parse first_name = " + loginJSONObject.getString("first_name"));
                 GQLog.dObj(this, "Parse last_name = " + loginJSONObject.getString("last_name"));
                 GQLog.dObj(this, "Parse email = " + loginJSONObject.getString("email"));
                 GQLog.dObj(this, "Parse updated_on = " + loginJSONObject.getString("updated_on"));
                boolean is_admin = loginJSONObject.getBoolean("is_admin");
                 GQLog.dObj(this, "Parse is_admin = " + is_admin);
                boolean is_staff = loginJSONObject.getBoolean("is_staff");
                 GQLog.dObj(this, "Parse is_staff = " + is_staff);
                boolean is_active = loginJSONObject.getBoolean("is_active");
                 GQLog.dObj(this, "Parse is_active = " + is_active);
                 GQLog.dObj(this, "Parse birthday = " + loginJSONObject.getString("birthday"));
                 GQLog.dObj(this, "Parse email_hex " + loginJSONObject.getString("email_hex"));
                JSONArray jArray = loginJSONObject.getJSONArray("venue_employments");
                int n;
                for (n = 0; n < jArray.length(); n = n + 1) {
                    JSONObject venue = jArray.getJSONObject(n);

                     GQLog.dObj(this, "    venue:" + n + " Parse created " + venue.getString("created"));
                     GQLog.dObj(this, "    venue:" + n + " Parse updated " + venue.getString("updated"));
                    int venue1 = venue.getInt("venue");
                     GQLog.dObj(this, "    venue:" + n + " Parse venue = " + venue1);
                    int group = venue.getInt("group");
                     GQLog.dObj(this, "    venue:" + n + " Parse group = " + group);
                    boolean is_owner = venue.getBoolean("is_owner");
                     GQLog.dObj(this, "    venue:" + n + " Parse  is_owner = " + is_owner);
                    boolean receives_daily_reports = venue.getBoolean("receives_daily_reports");
                     GQLog.dObj(this, "    venue:" + n + " Parse  receives_daily_reports = " + receives_daily_reports);
                    boolean receives_ticket_sale_emails = venue.getBoolean("receives_ticket_sale_emails");
                     GQLog.dObj(this, "    venue:" + n + " Parse  receives_ticket_sale_emails = " + receives_ticket_sale_emails);

                    boolean receives_alarm_SMS = venue.getBoolean("receives_alarm_SMS");
                     GQLog.dObj(this, "    venue:" + n + " Parse  receives_alarm_SMS = " + receives_alarm_SMS);

                    boolean notify = venue.getBoolean("notify");
                     GQLog.dObj(this, "    venue:" + n + " Parse  notify = " + notify);
                     GQLog.dObj(this, "======================================================");
                }
            } catch (Exception e) {
                 GQLog.dObj(this, "Problem Printing" + e.toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.app.upincode.getqd.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.app.upincode.getqd.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
