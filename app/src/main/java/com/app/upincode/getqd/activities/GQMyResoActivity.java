package com.app.upincode.getqd.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.activities.inputs.GenericArrayAdapter;
import com.app.upincode.getqd.databinding.ReservationItemBinding;
import com.app.upincode.getqd.errors.GQVolleyErrorHandler;
import com.app.upincode.getqd.networking.GQNetworkQueue;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.user_based.UBVenueParser;
import com.app.upincode.getqd.networking.parsers.venue_based.VBReservationParser;
import com.app.upincode.getqd.networking.requests.venue_based.VBPaginatedListReservationRequest;
import com.app.upincode.getqd.utils.DateTimeUtils;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class GQMyResoActivity extends GQBaseActivity {
    public static final String VENUE = "VENUE";

    // stuff on the screen we are going to update
    ListView listview = null;
    TextView tvCheckedIn = null;
    TextView tvBooked = null;
    UBVenueParser venue;
    List<VBReservationParser> reservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.activity_myresos);

        this.setTitle("Reservations");
        this.initBackButtonToolbar();

        final GlobalClass globalVariable = GQActivityUtils.getGlobalClass(this);

        Intent intent = getIntent();
        venue = UBVenueParser.fromString(UBVenueParser.class, intent.getStringExtra(VENUE));

        tvBooked = (TextView) findViewById(com.app.upincode.getqd.R.id.textView21);
        tvCheckedIn = (TextView) findViewById(com.app.upincode.getqd.R.id.textView23);

        // get a list view established cause we are going to update later
        listview = (ListView) findViewById(com.app.upincode.getqd.R.id.listViewStaff);

        //Build query params
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("page_size", "100");
        queryParams.put("arrival_date__gt", DateTimeUtils.toIsoString(new DateTime()));
        queryParams.put("booked_by", globalVariable.getCurrentUser().id.toString());
        queryParams.put("ordering", "-arrival_date");

        Request request = new VBPaginatedListReservationRequest(
                venue.id, GQNetworkUtils.getRequestHeaders(this), queryParams,
                new Response.Listener<VBReservationParser.PaginationParser>() {
                    @Override
                    public void onResponse(VBReservationParser.PaginationParser json) {
                        reservations = Arrays.asList(json.results);

                        populateReservations();
                        updateTotals();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure!
                        new GQVolleyErrorHandler(error).handle(GQMyResoActivity.this);
                    }
                });
        GQNetworkQueue.getInstance(this).addToRequestQueue(request);
    }

    public void populateReservations() {
        ReservationListAdapter adapter = new ReservationListAdapter(this, reservations);
        listview.setAdapter(adapter);
    }

    public void updateTotals() {
        int booked = 0;
        int checkedIn = 0;

        for (VBReservationParser reservation : reservations) {
            checkedIn += reservation.checkin_set.length;
            booked += reservation.num_of_people;
        }

        tvBooked.setText(Integer.toString(booked));
        tvCheckedIn.setText(Integer.toString(checkedIn));
    }

    class ReservationListAdapter extends GenericArrayAdapter<VBReservationParser> {
        public ReservationListAdapter(Context context, List<VBReservationParser> objects) {
            super(context, objects);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final VBReservationParser reservation = getItem(position);

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ReservationItemBinding binding = ReservationItemBinding.inflate(inflater);
                binding.setReservation(reservation);
                view = binding.getRoot();
            }

            return view;
        }
    }
}