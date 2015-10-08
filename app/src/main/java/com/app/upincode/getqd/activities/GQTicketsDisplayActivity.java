package com.app.upincode.getqd.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.app.upincode.getqd.databinding.MyTicketsTicketItemBinding;
import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.networking.parsers.user_based.UBTicketGroupParser;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.networking.parsers.user_based.UBVenueParser;
import com.app.upincode.getqd.utils.GQBarcodeUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class GQTicketsDisplayActivity extends GQBaseActivity {
    public static final String TICKET_GROUP = "ticket_group";

    ViewFlipper vf = null;
    Button nextB = null;
    Button prevB = null;

    UBTicketGroupParser ticketGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.displaytickets);

        //Retrieve selected ticket group from intent
        Intent intent = getIntent();
        ticketGroup = UBVenueParser.fromString(UBTicketGroupParser.class, intent.getStringExtra(TICKET_GROUP));

        this.setTitle("My Tickets - " + ticketGroup.type.event.name);
        this.initBackButtonToolbar();

        prevB = (Button) findViewById(R.id.prevButton);
        prevB.setOnClickListener(PrevStatsClickListener);
        nextB = (Button) findViewById(R.id.nextButton);
        nextB.setOnClickListener(NextStatsClickListener);
        vf = (ViewFlipper) findViewById(R.id.viewFlipper);

        populateTicketGroup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gqprofile, menu);
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

    public void populateTicketGroup() {
        vf.removeAllViews(); //Clear any views currently added

        //Create view for each ticket
        for (int i = 0; i < ticketGroup.tickets.length; i++) {
            UBTicketGroupParser.TicketParser ticket = ticketGroup.tickets[i];

            //Set special view values
            ticket._position = i;
            try {
                ticket._barcode_bitmap = GQBarcodeUtils.encodeAsBitmap(
                        ticket.barcode_string, BarcodeFormat.CODE_39, 600, 300);
            } catch (WriterException e) {
                e.printStackTrace();
            }

            //Create & bind view
            MyTicketsTicketItemBinding binding = MyTicketsTicketItemBinding.inflate(getLayoutInflater());
            binding.setTicketGroup(ticketGroup);
            binding.setTicket(ticket);
            vf.addView(binding.getRoot());
        }
    }

    private View.OnClickListener NextStatsClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            GQLog.dObj(this, "goto next ");
            vf.showPrevious();
        }

    };
    private View.OnClickListener PrevStatsClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            GQLog.dObj(this, "going back to stats listview");
            vf.showNext();
        }

    };

}
