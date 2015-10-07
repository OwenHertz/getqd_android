package com.app.upincode.getqd.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.activities.inputs.DatePickerButton;
import com.app.upincode.getqd.activities.inputs.TimePickerButton;
import com.app.upincode.getqd.databinding.ActivityGqstaffbookguestBinding;
import com.app.upincode.getqd.errors.GQVolleyErrorHandler;
import com.app.upincode.getqd.networking.GQNetworkQueue;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.user_based.UBVenueParser;
import com.app.upincode.getqd.networking.parsers.venue_based.VBCreateStaffReservationViaMobileParser;
import com.app.upincode.getqd.networking.parsers.venue_based.VBReservationReasonParser;
import com.app.upincode.getqd.networking.requests.venue_based.VBCreateStaffReservationViaMobileRequest;
import com.app.upincode.getqd.networking.requests.venue_based.VBListReservationReasonRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GQBookGuestActivity extends GQBaseActivity {
    public static final String VENUE = "VENUE";

    DatePickerButton btnDate;
    TimePickerButton btnTime;
    Button btnPlus;
    Button btnMinus;
    Spinner spinnerReasons;

    UBVenueParser venue;
    VBCreateStaffReservationViaMobileParser reservation;
    List<VBReservationReasonParser> reasons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityGqstaffbookguestBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_gqstaffbookguest);

        //Retrieve selected venue sent via JSON
        Intent intent = getIntent();
        venue = UBVenueParser.fromString(UBVenueParser.class, intent.getStringExtra(VENUE));

        //Bind reservation to view
        reservation = new VBCreateStaffReservationViaMobileParser();
        reservation.arrival_date = reservation.arrival_date.toDateTime(venue.timezone); //Localize calendar to venue TZ
        binding.setReservation(reservation);

        btnDate = (DatePickerButton) findViewById(com.app.upincode.getqd.R.id.BookGuestDateButton);
        btnTime = (TimePickerButton) findViewById(com.app.upincode.getqd.R.id.BookGuestTimeButton);
        btnPlus = (Button) findViewById(com.app.upincode.getqd.R.id.PlusButton);
        btnMinus = (Button) findViewById(com.app.upincode.getqd.R.id.MinusButton);
        spinnerReasons = (Spinner) findViewById(com.app.upincode.getqd.R.id.BookGuestTotalPartyReason);

        // Add to num_of_people when button is pressed
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reservation.num_of_people = reservation.num_of_people + 1;
                reservation.notifyChange();
            }
        });

        // Subtract frpm num_of_people when button is pressed
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reservation.num_of_people > 1) {
                    reservation.num_of_people = reservation.num_of_people - 1;
                }
                reservation.notifyChange();
            }
        });

        // Update arrival date when date is selected
        btnDate.addDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                reservation.arrival_date =  reservation.arrival_date
                        .withYear(year).withMonthOfYear(monthOfYear).withDayOfMonth(dayOfMonth);
                reservation.notifyChange();
            }
        });

        // Update arrival date when time is selected
        btnTime.addTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                reservation.arrival_date = reservation.arrival_date
                        .withHourOfDay(hourOfDay).withMinuteOfHour(minute);
                reservation.notifyChange();
            }
        });

        //Finish intent if cancel button is clicked
        Button btyes = (Button) findViewById(com.app.upincode.getqd.R.id.StaffCancelReservationButton);
        btyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Send data to server when submit button pushed
        Button submitButton = (Button) findViewById(com.app.upincode.getqd.R.id.SubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReservation();
            }
        });

        // Update reservation's type field when spinner changed
        spinnerReasons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reservation.type = reasons.get(position).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                reservation.type = null;
            }
        });

        // Retrieve reservation reasons
        VBListReservationReasonRequest request = new VBListReservationReasonRequest(
                this.venue.id, GQNetworkUtils.getRequestHeaders(this),
                new Response.Listener<VBReservationReasonParser[]>() {
                    @Override
                    public void onResponse(VBReservationReasonParser[] json) {
                        //Success!
                        reasons = Arrays.asList(json);
                        populateReasonList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure!
                        new GQVolleyErrorHandler(error).handle(GQBookGuestActivity.this);
                    }
                });
        // Add the request to the RequestQueue.
        GQNetworkQueue.getInstance(this).addToRequestQueue(request);
    }

    /**
     * Simple proxy for reason parser - used for custom toString function in adapter
     */
    class VenueReservationReasonProxy {
        public VBReservationReasonParser parser;
        public VenueReservationReasonProxy(VBReservationReasonParser parser) {
            this.parser = parser;
        }

        public String toString() {
            return this.parser.name;
        }
    }

    /**
     * Called after reasons are retrieved. Populates the reasons spinner
     */
    public void populateReasonList() {
        List<VenueReservationReasonProxy> reasonProxies = new ArrayList<>();
        for (VBReservationReasonParser parser : reasons) {
            reasonProxies.add(new VenueReservationReasonProxy(parser));
        }

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, reasonProxies);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReasons.setAdapter(spinnerArrayAdapter);
    }

    /**
     * Saves the reservation created by the user
     */
    public void saveReservation () {
        // Perform request
        VBCreateStaffReservationViaMobileRequest request = new VBCreateStaffReservationViaMobileRequest(
                this.venue.id, this.reservation, GQNetworkUtils.getRequestHeaders(this),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String json) {
                        //Success!
                        GQActivityUtils.showToast(getApplicationContext(), getString(R.string.reservation_created_toast));
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure!
                        new GQVolleyErrorHandler(error).handle(GQBookGuestActivity.this);
                    }
                });
        // Add the request to the RequestQueue.
        GQNetworkQueue.getInstance(this).addToRequestQueue(request);
    }
}
