package com.app.upincode.getqd.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.upincode.getqd.logging.GQLog;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class GQCardioActivity extends GQBaseActivity {
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
    int thePosition = 0;

    EditText FirstName = null;
    EditText LastName = null;
    EditText Email = null;
    EditText MobilePhoneNumber = null;
    EditText NameOnCC = null;
    // VARIABLE TO BE SENT TO PURCHASE
    String basketID = null;
    String Slug = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.activity_gqcard_io);

        Intent intent = getIntent();
        String target = intent.getStringExtra("position");
        thePosition = Integer.parseInt(target);
        basketID = intent.getStringExtra("basketid");
        Slug = intent.getStringExtra("slug");

        resultTextView = (TextView) findViewById(com.app.upincode.getqd.R.id.resultsTextView);
        resultTextView.setText("card.io library version: " + CardIOActivity.sdkVersion() + "\nBuilt: " + CardIOActivity.sdkBuildDate());

        scanButton = (Button) findViewById(com.app.upincode.getqd.R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GQLog.dObj(this, "Scan Button Pressed");
                onScanPress(v);
            }
        });

        EditCCN = (EditText) findViewById(com.app.upincode.getqd.R.id.resultCreditCardNumber);
        EditMon = (Spinner) findViewById(com.app.upincode.getqd.R.id.resultMonth);
        EditYear = (Spinner) findViewById(com.app.upincode.getqd.R.id.resultYear);
        EditCVV = (EditText) findViewById(com.app.upincode.getqd.R.id.resultCVV);
        EditPCode = (EditText) findViewById(com.app.upincode.getqd.R.id.resultPostalCode);

        FirstName = (EditText) findViewById(com.app.upincode.getqd.R.id.FirstName);
        LastName = (EditText) findViewById(com.app.upincode.getqd.R.id.LastName);
        Email = (EditText) findViewById(com.app.upincode.getqd.R.id.Email);
        MobilePhoneNumber = (EditText) findViewById(com.app.upincode.getqd.R.id.MobileNumber);
        NameOnCC = (EditText) findViewById(com.app.upincode.getqd.R.id.nameoncreditcard);
        Button btyes = (Button) findViewById(com.app.upincode.getqd.R.id.StaffCancelReservationButton);

        btyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GQLog.dObj(this, "CANCEL RESERVATION: onclick yes try to call the activity");
                try {
                    Intent intent = new Intent(getApplicationContext(), GQSelectTicketActivity.class);
                    //intenty.putExtra("target", getString(R.string.Frag_Target_Staff)); // start with the Staff fragment
                    intent.putExtra("position", Integer.toString(thePosition));
                    startActivity(intent);
                } catch (ClassCastException cce) {
                    GQLog.dObj(this, "CANCEL RESERVATION cast problem");
                }

            }
        });
        Button btsubmit = (Button) findViewById(com.app.upincode.getqd.R.id.SubmitButton);
        btsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GQLog.dObj(this, "Ready to send tell the how that we want to purchase some tickets");
                try {
                    // here is the URL we are going to use to send to purchase a basket
                    String URL = new String("api/events/box-office/baskets/" + basketID + "/staff-buy/");

                    String theMonth = EditMon.getSelectedItem().toString();
                    if (theMonth.startsWith("0")) {
                        theMonth = theMonth.replace("0", "");  // jeremey does not like 05 getFormat of month.
                    }
                    // create a model and send it up.
                    //TODO fix
//                    MyBaseModel mybm = new MyBaseModel(getApplicationContext());
//                    mybm.execute("Post", URL, "22",
//                            "number", EditCCN.getText().toString(),
//                            "expiration_0", theMonth,
//                            "postal", EditPCode.getText().toString(),
//                            "expiration_1", "20" + EditYear.getSelectedItem().toString(),
//                            "event_access", Slug,
//                            "first_name", FirstName.getText().toString(),
//                            "last_name", LastName.getText().toString(),   // Not Used
//                            "email", Email.getText().toString(),  //reasons (birthday etc)
//                            "phone_number", MobilePhoneNumber.getText().toString(),  // apparently not used
//                            "ccv", EditCVV.getText().toString(),
//                            "name", NameOnCC.getText().toString()
//
//                    );

                } catch (ClassCastException cce) {
                    GQLog.dObj(this, "CANCEL RESERVATION cast problem");
                }

            }
        });
        try {
            // here is the URL we are going to use to send to purchase a basket
            String URL = new String("api/events/box-office/baskets/" + basketID);

            // create a model and send it up.
            //TODO fix
//            MyBaseModelPayAndShip mybmPandS = new MyBaseModelPayAndShip(getApplicationContext());
//            mybmPandS.execute("Put", URL, "4",
//                    "payment_type", "2",
//                    "shipping_type", "2"
//            );
        } catch (Exception cce) {
            GQLog.dObj(this, " problem" + cce.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (CardIOActivity.canReadCardWithCamera()) {
            scanButton.setText("Press to Scan a credit card");
        } else {
            scanButton.setText("Enter credit card information");
            scanButton.setEnabled(false);  // do nothing if we got no camera.
        }
    }

    public void onScanPress(View v) {
        // This method is set up as an onClick handler in the layout xml
        // e.g. android:onClick="onScanPress"

        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // hides the manual entry button
        // if set, developers should provide their own manual entry mechanism in the app
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default: false

        // matches the theme of your application
        scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String resultStr = null;
        if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
            CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

            // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
            //resultStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";
            EditCCN.setText(scanResult.getRedactedCardNumber());
            // Do something with the raw number, e.g.:
            // myService.setCardNumber( scanResult.cardNumber );

            if (scanResult.isExpiryValid()) {
                //resultStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                EditMon.setSelection(scanResult.expiryMonth);  // Index might be off by one.
                EditYear.setSelection(scanResult.expiryYear - 2014);  // Index might be off by one
            }

            if (scanResult.cvv != null) {
                // Never log or display a CVV
                // resultStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                EditCVV.setText(scanResult.cvv);
            }

            if (scanResult.postalCode != null) {
                //resultStr += "Postal Code: " + scanResult.postalCode + "\n";
                EditPCode.setText(scanResult.postalCode);
            }
        } else {
            scanButton.setText("Scan was canceled. Please Enter CC Info.");
        }
        resultTextView.setText(results + resultStr);

    }

    //TODO remove
//    public class MyBaseModel extends GQBaseModel {
//
//        public MyBaseModel(Context context) {
//            super(context);
//        }
//
//
//        public void updateView(Boolean retCode, int code, String JSON) {
//            GQLog.dObj(this, "MyBaseModel:updateView:doing updateView retcode = " + retCode + "Code = " + code);
//            if (retCode == false) {
//                GQLog.dObj(this, "MyBaseModel: updateView:Unable to Access the Website");
//                return;
//            }
//            if ((code == 200) || (code == 201)) {  //  JSON Looks like the following
//                //JSONReasons = JSON;
//                GQLog.dObj(this, "Looking to print JSON" + JSON);
//                //printJSON(JSON);  // Looks Like the following
//                //  We just successfully placed a reservation toast and back to staff of mainactivity.
//                Toast.makeText(getApplicationContext(), "Successful Transaction.  You should receive your tickets via the specified delivery method. Thank You.", Toast.LENGTH_LONG).show();
//                // Send the user back to buy more tickets.
//                Intent intenty = new Intent(getApplicationContext(), GQSelectTicketActivity.class);
//                intenty.putExtra("position", Integer.toString(thePosition));
//                startActivity(intenty);
//            } else if (code == 400) {
//                GQLog.dObj(this, "MyBaseModel :UpdateView: Getting 400  Error from the website" + JSON);
//                // Username or password false, display and an error
//
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQCardioActivity.this);
//
//                dlgAlert.setMessage(JSON);
//                dlgAlert.setTitle("400 Error Message Bad Request Error");
//                dlgAlert.setPositiveButton("OK", null);
//                dlgAlert.setCancelable(true);
//                dlgAlert.create().show();
//
//                dlgAlert.setPositiveButton("Ok",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//
//            } else {
//                GQLog.dObj(this, "MyBaseModel :Getting Error From Website =  " + code);
//
//
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getApplicationContext());
//
//                dlgAlert.setMessage("Error Message " + code);
//                dlgAlert.setTitle("Server Says " + JSON);
//                dlgAlert.setPositiveButton("OK", null);
//                dlgAlert.setCancelable(true);
//                dlgAlert.create().show();
//
//                dlgAlert.setPositiveButton("Ok",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//
//            }
//
//        }
//
//        public void printJSON(String JSON) {
//            try {
//                GQLog.dObj(this, "Trying to print JSON.");
//                JSONObject loginJSONObject = new JSONObject(JSON);
//                GQLog.dObj(this, "Parse id = " + loginJSONObject.getString("id"));
//                GQLog.dObj(this, "Parse last_login = " + loginJSONObject.getString("last_login"));
//                GQLog.dObj(this, "Parse first_name = " + loginJSONObject.getString("first_name"));
//                GQLog.dObj(this, "Parse last_name = " + loginJSONObject.getString("last_name"));
//                GQLog.dObj(this, "Parse email = " + loginJSONObject.getString("email"));
//                GQLog.dObj(this, "Parse updated_on = " + loginJSONObject.getString("updated_on"));
//                boolean is_admin = loginJSONObject.getBoolean("is_admin");
//                GQLog.dObj(this, "Parse is_admin = " + is_admin);
//                boolean is_staff = loginJSONObject.getBoolean("is_staff");
//                GQLog.dObj(this, "Parse is_staff = " + is_staff);
//                boolean is_active = loginJSONObject.getBoolean("is_active");
//                GQLog.dObj(this, "Parse is_active = " + is_active);
//                GQLog.dObj(this, "Parse birthday = " + loginJSONObject.getString("birthday"));
//                GQLog.dObj(this, "Parse email_hex " + loginJSONObject.getString("email_hex"));
//                JSONArray jArray = loginJSONObject.getJSONArray("venue_employments");
//                int n;
//                for (n = 0; n < jArray.length(); n = n + 1) {
//                    JSONObject venue = jArray.getJSONObject(n);
//
//                    GQLog.dObj(this, "    venue:" + n + " Parse created " + venue.getString("created"));
//                    GQLog.dObj(this, "    venue:" + n + " Parse updated " + venue.getString("updated"));
//                    int venue1 = venue.getInt("venue");
//                    GQLog.dObj(this, "    venue:" + n + " Parse venue = " + venue1);
//                    int group = venue.getInt("group");
//                    GQLog.dObj(this, "    venue:" + n + " Parse group = " + group);
//                    boolean is_owner = venue.getBoolean("is_owner");
//                    GQLog.dObj(this, "    venue:" + n + " Parse  is_owner = " + is_owner);
//                    boolean receives_daily_reports = venue.getBoolean("receives_daily_reports");
//                    GQLog.dObj(this, "    venue:" + n + " Parse  receives_daily_reports = " + receives_daily_reports);
//                    boolean receives_ticket_sale_emails = venue.getBoolean("receives_ticket_sale_emails");
//                    GQLog.dObj(this, "    venue:" + n + " Parse  receives_ticket_sale_emails = " + receives_ticket_sale_emails);
//
//                    boolean receives_alarm_SMS = venue.getBoolean("receives_alarm_SMS");
//                    GQLog.dObj(this, "    venue:" + n + " Parse  receives_alarm_SMS = " + receives_alarm_SMS);
//
//                    boolean notify = venue.getBoolean("notify");
//                    GQLog.dObj(this, "    venue:" + n + " Parse  notify = " + notify);
//                    GQLog.dObj(this, "======================================================");
//                }
//            } catch (Exception e) {
//                GQLog.dObj(this, "Problem Printing" + e.toString());
//            }
//        }
//    }

    //TODO remove
//    public class MyBaseModelPayAndShip extends GQBaseModel {
//
//        public MyBaseModelPayAndShip(Context context) {
//            super(context);
//        }
//
//
//        public void updateView(Boolean retCode, int code, String JSON) {
//            GQLog.dObj(this, "MyBaseModel:updateView:doing updateView retcode = " + retCode + "Code = " + code);
//            if (retCode == false) {
//                GQLog.dObj(this, "MyBaseModel: updateView:Unable to Access the Website");
//                return;
//            }
//            if ((code == 200) || (code == 201)) {  //  JSON Looks like the following
//                //JSONReasons = JSON;
//                GQLog.dObj(this, "Looking to print JSON for Pay ANd Ship" + JSON);
//                //printJSON(JSON);  // Looks Like the following
//                //  We just successfully placed a reservation toast and back to staff of mainactivity.
//                //Toast.makeText(getApplicationContext(), JSON, Toast.LENGTH_LONG).show();
//                // Send the user back to buy more tickets.
//
//            } else if (code == 400) {
//                GQLog.dObj(this, "MyBaseModel :UpdateView: Getting 400  Error from the website");
//                // Username or password false, display and an error
//
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getApplicationContext());
//
//                dlgAlert.setMessage("Website Problem");
//                dlgAlert.setTitle("400 Error Message Bad Request Error");
//                dlgAlert.setPositiveButton("OK", null);
//                dlgAlert.setCancelable(true);
//                dlgAlert.create().show();
//
//                dlgAlert.setPositiveButton("Ok",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//
//            } else {
//                GQLog.dObj(this, "MyBaseModel :Getting Error From Website =  " + code);
//
//
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getApplicationContext());
//
//                dlgAlert.setMessage("Network Problem");
//                dlgAlert.setTitle("Error Message " + code);
//                dlgAlert.setPositiveButton("OK", null);
//                dlgAlert.setCancelable(true);
//                dlgAlert.create().show();
//
//                dlgAlert.setPositiveButton("Ok",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//
//            }
//
//        }
//
//        public void printJSON(String JSON) {
//            try {
//                GQLog.dObj(this, "Trying to print JSON.");
//                JSONObject loginJSONObject = new JSONObject(JSON);
//                GQLog.dObj(this, "Parse id = " + loginJSONObject.getString("id"));
//                GQLog.dObj(this, "Parse last_login = " + loginJSONObject.getString("last_login"));
//                GQLog.dObj(this, "Parse first_name = " + loginJSONObject.getString("first_name"));
//                GQLog.dObj(this, "Parse last_name = " + loginJSONObject.getString("last_name"));
//                GQLog.dObj(this, "Parse email = " + loginJSONObject.getString("email"));
//                GQLog.dObj(this, "Parse updated_on = " + loginJSONObject.getString("updated_on"));
//                boolean is_admin = loginJSONObject.getBoolean("is_admin");
//                GQLog.dObj(this, "Parse is_admin = " + is_admin);
//                boolean is_staff = loginJSONObject.getBoolean("is_staff");
//                GQLog.dObj(this, "Parse is_staff = " + is_staff);
//                boolean is_active = loginJSONObject.getBoolean("is_active");
//                GQLog.dObj(this, "Parse is_active = " + is_active);
//                GQLog.dObj(this, "Parse birthday = " + loginJSONObject.getString("birthday"));
//                GQLog.dObj(this, "Parse email_hex " + loginJSONObject.getString("email_hex"));
//                JSONArray jArray = loginJSONObject.getJSONArray("venue_employments");
//                int n;
//                for (n = 0; n < jArray.length(); n = n + 1) {
//                    JSONObject venue = jArray.getJSONObject(n);
//
//                    GQLog.dObj(this, "    venue:" + n + " Parse created " + venue.getString("created"));
//                    GQLog.dObj(this, "    venue:" + n + " Parse updated " + venue.getString("updated"));
//                    int venue1 = venue.getInt("venue");
//                    GQLog.dObj(this, "    venue:" + n + " Parse venue = " + venue1);
//                    int group = venue.getInt("group");
//                    GQLog.dObj(this, "    venue:" + n + " Parse group = " + group);
//                    boolean is_owner = venue.getBoolean("is_owner");
//                    GQLog.dObj(this, "    venue:" + n + " Parse  is_owner = " + is_owner);
//                    boolean receives_daily_reports = venue.getBoolean("receives_daily_reports");
//                    GQLog.dObj(this, "    venue:" + n + " Parse  receives_daily_reports = " + receives_daily_reports);
//                    boolean receives_ticket_sale_emails = venue.getBoolean("receives_ticket_sale_emails");
//                    GQLog.dObj(this, "    venue:" + n + " Parse  receives_ticket_sale_emails = " + receives_ticket_sale_emails);
//
//                    boolean receives_alarm_SMS = venue.getBoolean("receives_alarm_SMS");
//                    GQLog.dObj(this, "    venue:" + n + " Parse  receives_alarm_SMS = " + receives_alarm_SMS);
//
//                    boolean notify = venue.getBoolean("notify");
//                    GQLog.dObj(this, "    venue:" + n + " Parse  notify = " + notify);
//                    GQLog.dObj(this, "======================================================");
//                }
//            } catch (Exception e) {
//                GQLog.dObj(this, "Problem Printing" + e.toString());
//            }
//        }
//    }
}

