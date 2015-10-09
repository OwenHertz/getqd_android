package com.app.upincode.getqd.activities;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app.upincode.getqd.networking.parsers.box_office.BOStaffBuyParser;

public class GQCompActivity extends GQBaseMyTicketsCheckoutActivity {
    EditText FirstName;
    EditText LastName;
    EditText Email;
    EditText MobilePhoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.activity_gqinteract);

        setTitle("Sell Tickets - Customer Info");
        initBackButtonToolbar();

        FirstName = (EditText) findViewById(com.app.upincode.getqd.R.id.FirstName);
        LastName = (EditText) findViewById(com.app.upincode.getqd.R.id.LastName);
        Email = (EditText) findViewById(com.app.upincode.getqd.R.id.Email);
        MobilePhoneNumber = (EditText) findViewById(com.app.upincode.getqd.R.id.MobileNumber);

        Button btsubmit = (Button) findViewById(com.app.upincode.getqd.R.id.SubmitButton);
        btsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BOStaffBuyParser parser = new BOStaffBuyParser(
                        FirstName.getText().toString(),
                        LastName.getText().toString(),
                        Email.getText().toString(),
                        MobilePhoneNumber.getText().toString(),
                        eventAccess.slug
                );

                performBasketPurchase(parser);
            }
        });
    }
}

