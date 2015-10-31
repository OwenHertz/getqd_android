package com.app.upincode.getqd.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.activities.inputs.GenericArrayAdapter;
import com.app.upincode.getqd.databinding.ReservationItemBinding;
import com.app.upincode.getqd.databinding.ScanEventItemBinding;
import com.app.upincode.getqd.errors.GQVolleyErrorHandler;
import com.app.upincode.getqd.networking.GQNetworkQueue;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.HttpStatus;
import com.app.upincode.getqd.networking.parsers.user_based.UBVenueParser;
import com.app.upincode.getqd.networking.parsers.venue_based.VBEventsCheckInScanParser;
import com.app.upincode.getqd.networking.parsers.venue_based.VBEventsTicketHistoryParser;
import com.app.upincode.getqd.networking.parsers.venue_based.VBGetEventsParser;
//import com.app.upincode.getqd.networking.parsers.venue_based.VBReservationParser;
import com.app.upincode.getqd.networking.requests.venue_based.VBEventsCheckInScanRequest;
import com.app.upincode.getqd.networking.requests.venue_based.VBGetEventsRequest;
import com.app.upincode.getqd.networking.requests.venue_based.VBPaginatedListReservationRequest;
import com.app.upincode.getqd.utils.DateTimeUtils;
import com.app.upincode.getqd.utils.IntentIntegrator;
import com.app.upincode.getqd.utils.IntentResult;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class GQScanEventsActivity extends GQBaseActivity {
    public static final String VENUE = "VENUE";

    // stuff on the screen we are going to update
    ListView listview = null;
    TextView tvCheckedIn = null;
    TextView tvBooked = null;
    UBVenueParser venue;
    List<VBGetEventsParser> events;
    int theEventID = 0;
    IntentIntegrator integrator = null;
    boolean userIntegrator = true;  // true means you external app. false internal bundled with getqd app
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.activity_scan);

        this.setTitle("Select Event to Scan");
        this.initBackButtonToolbar();

        final GlobalClass globalVariable = GQActivityUtils.getGlobalClass(this);

        Intent intent = getIntent();
        venue = UBVenueParser.fromString(UBVenueParser.class, intent.getStringExtra(VENUE));

        tvBooked = (TextView) findViewById(com.app.upincode.getqd.R.id.textView21);
        tvCheckedIn = (TextView) findViewById(com.app.upincode.getqd.R.id.textView23);

        // get a list view established cause we are going to update later
        listview = (ListView) findViewById(com.app.upincode.getqd.R.id.listViewStaff);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View view, int i, long l) {
                theEventID = events.get(i).id;
                if(userIntegrator==true) {
                    integrator = new IntentIntegrator(GQScanEventsActivity.this);
                    integrator.initiateScan();
                } else {
                    // set id and start the scanner class
                    globalVariable.setScanEventID(Integer.toString(theEventID));
                    globalVariable.setScanVenueID(Integer.toString(venue.id));
                    Intent intent = new Intent(GQScanEventsActivity.this, CaptureActivity.class);
                    startActivity(intent);
                }

            }
        });
        Request request = new VBGetEventsRequest(
                venue.id, GQNetworkUtils.getRequestHeaders(this),
                new Response.Listener<VBGetEventsParser[]>() {
                    @Override
                    public void onResponse(VBGetEventsParser[] json) {
                        events = Arrays.asList(json);            //Jeremy the Android studio does not like the "results" part. Wrong parser?
                        populateEvents();
                       // updateTotals();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure!
                        new GQVolleyErrorHandler(error).handle(GQScanEventsActivity.this);
                    }
                });
        GQNetworkQueue.getInstance(this).addToRequestQueue(request);

    }


    public void populateEvents() {
        GetEventsListAdapter adapter = new GetEventsListAdapter(this, events);
       listview.setAdapter(adapter);
    }

    public void updateTotals() {
        /*
        int booked = 0;
        int checkedIn = 0;

        for (VBReservationParser reservation : reservations) {
            checkedIn += reservation.checkin_set.length;
            booked += reservation.num_of_people;
        }

        tvBooked.setText(Integer.toString(booked));
        tvCheckedIn.setText(Integer.toString(checkedIn));
        */
    }

    class GetEventsListAdapter extends GenericArrayAdapter<VBGetEventsParser> {
        public GetEventsListAdapter(Context context, List<VBGetEventsParser> objects) {
            super(context, objects);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final VBGetEventsParser Event = getItem(position);

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ScanEventItemBinding binding = ScanEventItemBinding.inflate(inflater);
                //ReservationItemBinding binding = ReservationItemBinding.inflate(inflater);
                binding.setEvent(Event);
                view = binding.getRoot();
            }

            return view;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d("Herb", "RequestCode in Integrator ScanActivity = " + requestCode);
        Log.d("Herb", "ResultCodeScanActivity = " + resultCode);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String re = scanResult.getContents();
            Log.d("HERB", "The code is = " + re);
            ProcessTheScan(re);
        } else {
            // else continue with any other code you need in the method
            Log.d("HERB", "scanResult is NULL");
        }

    }
    VBEventsCheckInScanRequest request=null;
    public void ProcessTheScan(String ScanCode) {
        boolean scan = true;
        if(ScanCode == null) {
            return; // this is what you get when the scanner terminates itself
        }

        Log.d("Herb", "ProcessingTheScan code = " + ScanCode);

if(scan == true ) {
        Integer[] eventArray = new Integer[1];
        eventArray[0] = new Integer(theEventID);
        VBEventsCheckInScanParser parser = new VBEventsCheckInScanParser(ScanCode, eventArray);

        // Perform request

            request = new VBEventsCheckInScanRequest(
            this.venue.id, parser, GQNetworkUtils.getRequestHeaders(this),
            new Response.Listener<VBEventsTicketHistoryParser>() {
                @Override
                public void onResponse(VBEventsTicketHistoryParser json) {
                    //If this is called, server returned 2xx response!
                    int status=request.networkResponse.statusCode;
                    /*
                    if(request == null ) {
                        Log.d("Herb","null request");
                    } else if(request.networkResponse == null) {
                        Log.d("Herb","null request.networkResponse");
                    } else if(request.networkResponse.statusCode == 0) {
                        Log.d("Herb","0 request.networkResponse.statusCode");
                    } else {
                        status =json.networkResponse.statusCode;
                    }
                    */
;
                    // Ring a bell or something
                    //VolleyError theError = Response.

                    if (status == HttpStatus.SC_CREATED) {
                        // Ticket successfully scanned!
                        Toast toast = Toast.makeText(getApplicationContext(), "Scanned Ticket Good!", Toast.LENGTH_LONG);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        v.setTextColor(Color.GREEN);
                        v.setTextSize(30);
                        toast.show();
                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        toneG.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING, 200);

                    } else if (status == HttpStatus.SC_ACCEPTED) {
                        // No ticket found/scanned

                        Toast toast = Toast.makeText(getApplicationContext(), "Scanned Ticket is No Good!", Toast.LENGTH_LONG);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        v.setTextColor(Color.RED);
                        v.setTextSize(30);
                        toast.show();
                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);

                     } else if (status == 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Testing Toast", Toast.LENGTH_LONG);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        v.setTextColor(Color.GREEN);
                        v.setTextSize(30);
                        toast.show();
                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                    }
                    integrator = new IntentIntegrator(GQScanEventsActivity.this);
                    integrator.initiateScan();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Something went wrong! Server returned 4xx or 5xx error
/*
201 is good
202 no ticket found/scanned
400 cannot perform this action e.g. ticket alread scanned- ticket does not belong to the event.
 */

                    Log.d("HERB","Response.ErrorListener  Caught an Error =" + error.getMessage());
                    NetworkResponse theError = error.networkResponse;
                    Log.d("Herb","Response.ErrorListener = "+Integer.toString(theError.statusCode));
                    int status = theError.statusCode;

                    Toast toast = Toast.makeText(getApplicationContext(), "Ticket is NO Good! Status =" + status, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    v.setTextColor(Color.RED);
                    v.setTextSize(30);
                    toast.show();
                    Toast.makeText(getApplicationContext(), "Ticket is NO good, Status= " + status, Toast.LENGTH_LONG).show();


                    if (status == HttpStatus.SC_CREATED) {
                        // Ticket successfully scanned!
                    } else if (status == HttpStatus.SC_ACCEPTED) {
                        // No ticket found/scanned
                    }

                    //You may want to handle 'Cannot perform action' errors differently
                    // than other request failures. If so, do something like this:
                    //  if (error.networkResponse != null && error.networkResponse.statusCode == HttpStatus.SC_BAD_REQUEST) {
                    //Special error handler
                    //  }
                    //  else {
                    // Regular response handler
                    //  new GQVolleyErrorHandler(error).handle(GQBookGuestActivity.this);
                    //   }


                    //Use generic error handler to tell the user that something went wrong
                    new GQVolleyErrorHandler(error).handle(GQScanEventsActivity.this);
                }
            });
    // Add the request to the RequestQueue.
    GQNetworkQueue.getInstance(this).addToRequestQueue(request);
}

    }
}