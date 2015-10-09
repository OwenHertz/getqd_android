package com.app.upincode.getqd.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.networking.parsers.box_office.BOStaffBuyParser;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class GQCardioActivity extends GQBaseMyTicketsCheckoutActivity {
    private int MY_SCAN_REQUEST_CODE = 100; // arbitrary int
    String results = new String("Results:\n");

    private Button scanButton;
    private TextView resultTextView;
    private EditText EditCCN;
    private Spinner EditMon;
    private Spinner EditYear;
    private EditText EditCVV;
    private EditText EditPCode;
    EditText FirstName;
    EditText LastName;
    EditText Email;
    EditText MobilePhoneNumber;
    EditText NameOnCC;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.activity_gqcard_io);

        setTitle("Sell Tickets - Customer Info");
        initBackButtonToolbar();

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

        Button btsubmit = (Button) findViewById(com.app.upincode.getqd.R.id.SubmitButton);
        btsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String theMonth = EditMon.getSelectedItem().toString();
                if (theMonth.startsWith("0")) {
                    theMonth = theMonth.replace("0", "");  // jeremy does not like 05 getFormat of month.
                }

                BOStaffBuyParser parser = new BOStaffBuyParser(
                        FirstName.getText().toString(),
                        LastName.getText().toString(),
                        Email.getText().toString(),
                        MobilePhoneNumber.getText().toString(),
                        eventAccess.slug,
                        NameOnCC.getText().toString(),
                        EditCCN.getText().toString(),
                        theMonth,
                        "20" + EditYear.getSelectedItem().toString(),
                        EditCVV.getText().toString(),
                        EditPCode.getText().toString()
                );

                performBasketPurchase(parser);
            }
        });
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
}

