package com.app.upincode.getqd.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.upincode.getqd.R;
import com.app.upincode.getqd.databinding.ActivityVenueBinding;
import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.networking.parsers.publik.PublicVenueParser;


public class GQVenueActivity extends GQBaseActivity {
    public final static String VENUE = "venue";

    Button btnChat;
    PublicVenueParser venue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityVenueBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_venue);

        //Retrieve selected venue sent via JSON
        Intent intent = getIntent();
        venue = PublicVenueParser.fromString(PublicVenueParser.class, intent.getStringExtra(VENUE));
        binding.setVenue(venue);

        btnChat = (Button) findViewById(com.app.upincode.getqd.R.id.Chats);

        //btnChat.setText(Html.fromHtml(styledText));
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GQLog.dObj(this, "chat button pressed");

                //selectDate();   // we need to put up the date dialog. So user can select the date
            }
        });
        // we go back to the staff upon pressing return.
        Button btyes = (Button) findViewById(com.app.upincode.getqd.R.id.VenueCancelButton);
        btyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
