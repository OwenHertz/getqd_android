package com.app.upincode.getqd;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.models.GQBaseModel;

import org.json.JSONArray;
import org.json.JSONObject;

public class GQInteractActivity extends ActionBarActivity {
    final String TAG = getClass().getName();
    private Button scanButton;
    private TextView resultTextView;
    private EditText ccEditView = null;
    private EditText ExpireDateEditView = null;
    private EditText EditCCN = null;
    private Spinner EditMon = null;
    private Spinner EditYear = null;
    private EditText EditCVV = null;
    private EditText EditPCode = null;
    private int MY_SCAN_REQUEST_CODE = 100; // arbitrary int
    String results = new String("Results:\n");

    EditText FirstName = null;
    EditText LastName = null;
    EditText Email = null;
    EditText MobilePhoneNumber = null;

    // VARIABLE TO BE SENT TO PURCHASE
    String basketID = null;
    String Slug = null;

    int thePosition = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.activity_gqinteract);

        Intent intenty = getIntent();
        String target = intenty.getStringExtra("position");

        thePosition = Integer.parseInt(target);
        basketID = intenty.getStringExtra("basketid");
        Slug = intenty.getStringExtra("slug");

        Button btyes = (Button) findViewById(com.app.upincode.getqd.R.id.StaffCancelReservationButton);
        btyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GQLog.dObj(this, "CANCEL RESERVATION: onclick yes try to call the activity");
                try {
                    Intent intenty = new Intent(getApplicationContext(), GQSelectTicketActivity.class);
                    //intenty.putExtra("target", getString(R.string.Frag_Target_Staff)); // start with the Staff fragment
                    intenty.putExtra("position", Integer.toString(thePosition));
                    startActivity(intenty);
                } catch (ClassCastException cce) {
                    GQLog.dObj(this, "CANCEL RESERVATION cast problem");
                }

            }
        });
        FirstName = (EditText) findViewById(com.app.upincode.getqd.R.id.FirstName);
        LastName = (EditText) findViewById(com.app.upincode.getqd.R.id.LastName);
        Email = (EditText) findViewById(com.app.upincode.getqd.R.id.Email);
        MobilePhoneNumber = (EditText) findViewById(com.app.upincode.getqd.R.id.MobileNumber);

        Button btsubmit = (Button) findViewById(com.app.upincode.getqd.R.id.SubmitButton);
        btsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GQLog.dObj(this, "Ready to send tell the how that we want to purchase some tickets with Interact");
                try {
                    // here is the URL we are going to use to send to purchase a basket
                    String URL = new String("api/events/box-office/baskets/" + basketID + "/staff-buy/");


                    // create a model and send it up.
                    MyBaseModel mybm = new MyBaseModel(getApplicationContext());
                    mybm.execute("Post", URL, "10",
                            "event_access", Slug,
                            "first_name", FirstName.getText().toString(),
                            "last_name", LastName.getText().toString(),   // Not Used
                            "email", Email.getText().toString(),  //reasons (birthday etc)
                            "phone_number", MobilePhoneNumber.getText().toString() // apparently not used


                    );

                } catch (ClassCastException cce) {
                    GQLog.dObj(this, "CANCEL RESERVATION cast problem");
                }

            }
        });
        try {
            // here is the URL we are going to use to send to purchase a basket
            String URL = new String("api/events/box-office/baskets/" + basketID + "/interac-link/");
            //use /api/events/box-office/baskets/{pk}/interac-link/ INSTEAD OF /api/events/box-office/baskets/{pk}/staff-buy/ when completing the purchase. (
            // create a model and send it up.
            MyBaseModelPayAndShip mybmPandS = new MyBaseModelPayAndShip(getApplicationContext());
            mybmPandS.execute("Put", URL, "4",
                    "payment_type", "3",  // interact
                    "shipping_type", "2"
            );
        } catch (Exception cce) {
            GQLog.dObj(this, " problem" + cce.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    public class MyBaseModel extends GQBaseModel {

        public MyBaseModel(Context context) {
            super(context);
        }


        public void updateView(Boolean retCode, int code, String JSON) {
            GQLog.dObj(this, "MyBaseModel:updateView:doing updateView retcode = " + retCode + "Code = " + code);
            if (retCode == false) {
                GQLog.dObj(this, "MyBaseModel: updateView:Unable to Access the Website");
                return;
            }
            if ((code == 200) || (code == 201)) {  //  JSON Looks like the following
                //JSONReasons = JSON;
                GQLog.dObj(this, "Looking to print JSON" + JSON);
                //printJSON(JSON);  // Looks Like the following
                //  We just successfully placed a reservation toast and back to staff of mainactivity.
                Toast.makeText(getApplicationContext(), "Successful Transaction.  You should receive your tickets via the specified delivery method. Thank You.", Toast.LENGTH_LONG).show();
                // Send the user back to buy more tickets.
                Intent intenty = new Intent(getApplicationContext(), GQSelectTicketActivity.class);
                intenty.putExtra("position", Integer.toString(thePosition));
                startActivity(intenty);
            } else if (code == 400) {
                GQLog.dObj(this, "MyBaseModel :UpdateView: Getting 400  Error from the website" + JSON);
                // Username or password false, display and an error

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQInteractActivity.this);

                dlgAlert.setMessage(JSON);
                dlgAlert.setTitle("400 Error Message Bad Request Error");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

            } else {
                GQLog.dObj(this, "MyBaseModel :Getting Error From Website =  " + code);


                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQInteractActivity.this);

                dlgAlert.setMessage("Error Message " + code);
                dlgAlert.setTitle("Server Says " + JSON);
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

    public class MyBaseModelPayAndShip extends GQBaseModel {

        public MyBaseModelPayAndShip(Context context) {
            super(context);
        }


        public void updateView(Boolean retCode, int code, String JSON) {
            GQLog.dObj(this, "MyBaseModel:updateView:doing updateView retcode = " + retCode + "Code = " + code);
            if (retCode == false) {
                GQLog.dObj(this, "MyBaseModel: updateView:Unable to Access the Website");
                return;
            }
            if ((code == 200) || (code == 201)) {  //  JSON Looks like the following
                //JSONReasons = JSON;
                GQLog.dObj(this, "Looking to print JSON for Pay ANd Ship" + JSON);
                //printJSON(JSON);  // Looks Like the following
                //  We just successfully placed a reservation toast and back to staff of mainactivity.
                //Toast.makeText(getApplicationContext(), JSON, Toast.LENGTH_LONG).show();
                // Send the user back to buy more tickets.

            } else if (code == 400) {
                GQLog.dObj(this, "MyBaseModel :UpdateView: Getting 400  Error from the website");
                // Username or password false, display and an error

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getApplicationContext());

                dlgAlert.setMessage("Website Problem");
                dlgAlert.setTitle("400 Error Message Bad Request Error");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

            } else {
                GQLog.dObj(this, "MyBaseModel :Getting Error From Website =  " + code);


                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQInteractActivity.this);

                dlgAlert.setMessage(JSON);
                dlgAlert.setTitle("Error Message " + code);
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
}

