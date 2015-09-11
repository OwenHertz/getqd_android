package com.app.upincode.getqd;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.models.GQBaseModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;


public class GQBookGuestActivity extends ActionBarActivity {
    Button btnCalendar = null;
    Button btnTimePicker = null;
    Button btnPlus = null;
    Button btnMinus = null;
    TextView tvFirstName = null;
    TextView tvLastName = null;
    TextView tvEmail = null;
    TextView tvMobilePhone = null;
    TextView tvDetails = null;
    TextView tvCount = null;
    int thePostion = 0;
    int groupSizeCount = 1;  // must have one in your party
    int mYear, mMonth, mDay, mHour, mMinute;
    Spinner reason = null;
    String JSONReasons = null;
    Calendar c1 = null;  // this is the calendar for picking the reservation date and time
    String TargetTimeZone = new String("America/Edmonton");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.activity_gqstaffbookguest);

        btnCalendar = (Button) findViewById(com.app.upincode.getqd.R.id.BookGuestDateButton);
        btnTimePicker = (Button) findViewById(com.app.upincode.getqd.R.id.BookGuestTimeButton);
        btnPlus = (Button) findViewById(com.app.upincode.getqd.R.id.PlusButton);
        btnMinus = (Button) findViewById(com.app.upincode.getqd.R.id.MinusButton);
        tvFirstName = (TextView) findViewById(com.app.upincode.getqd.R.id.BookGuestFirstName);
        tvLastName = (TextView) findViewById(com.app.upincode.getqd.R.id.BookGuestLastName);
        tvEmail = (TextView) findViewById(com.app.upincode.getqd.R.id.BookGuestEmail);
        tvMobilePhone = (TextView) findViewById(com.app.upincode.getqd.R.id.BookGuestMobileNumber);
        tvDetails = (TextView) findViewById(com.app.upincode.getqd.R.id.BookGuestTotalPartyDetails);
        tvCount = (TextView) findViewById(com.app.upincode.getqd.R.id.textcounterView11);
        reason = (Spinner) findViewById(com.app.upincode.getqd.R.id.BookGuestTotalPartyReason);

        //we are passed the position in the list view. we are going to need venue info to make this request
        Intent intenty = getIntent();
        String target = intenty.getStringExtra("position");
        thePostion = Integer.parseInt(target);          // Setting the index into the venues array

        /*Setup The Calendar */
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        String UserVenue = globalVariable.getUserVenuesJSON();

        int position = thePostion;
        try {
            JSONArray jArray = new JSONArray(UserVenue);
            JSONObject venue = jArray.getJSONObject(position);
            TargetTimeZone = venue.getString("timezone");
            GQLog.dObj(this, "Target time zone from JSON = " + TargetTimeZone);
            // set the time to current targettimezone time.
            // there is code below that is similar.  the buttons will operate on the
            // this calandar.  When we send to website we convert to GMT.
            c1 = new GregorianCalendar(TimeZone.getTimeZone(TargetTimeZone));

            /*
            So now we are going to put the current venue time into the buttons for date and time.
             */
            int mYear1 = c1.get(Calendar.YEAR);
            int mMonth1 = c1.get(Calendar.MONTH);
            int mDay1 = c1.get(Calendar.DAY_OF_MONTH);
            String date1 = new String((mMonth1 + 1) + "-" + mDay1 + "-" + mYear1);
            SetDate(date1);     // put it in the Date button to be seen

            int hourOfDay = c1.get(Calendar.HOUR_OF_DAY);
            int minute = c1.get(Calendar.MINUTE);
            String displaystring = null;

            if (hourOfDay == 0) {
                hourOfDay = 12;
                displaystring = new String("AM");
            } else if (hourOfDay >= 12) {
                displaystring = new String("PM");
            } else {
                displaystring = new String("AM");
            }
            if (hourOfDay > 13) {
                hourOfDay = hourOfDay - 12;
            }
            // put it into the time button.
            if (minute < 10) {
                SetTime(hourOfDay + ":0" + minute + " " + displaystring);  // put in button to be seen
            } else {
                SetTime(hourOfDay + ":" + minute + " " + displaystring);   // put in button to be seen
            }
            // This is not the end of the construction we are going to setup a bunch listeners
            // for these buttons. THEN WE GO GET THE REASONS DOWN BELOW in the OnCreate.
        } catch (Exception e) {
            GQLog.dObj(this, "tx +" + e.toString());
        }
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GQLog.dObj(this, "Party Size Plus the count");
                groupSizeCount = groupSizeCount + 1;
                tvCount.setText(Integer.toString(groupSizeCount));  // update the count
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GQLog.dObj(this, "Party Size Minus the count");
                groupSizeCount = groupSizeCount - 1;
                if (groupSizeCount < 1) {
                    groupSizeCount = 1;
                }
                tvCount.setText(Integer.toString(groupSizeCount));  // update the count
            }
        });
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectDate();   // we need to put up the date dialog. So user can select the date
            }
        });
        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Process to get Current Time
                SelectTime();    // We need to put up the time dialog .  So users can select the time for the reservations

            }
        });
        Button btyes = (Button) findViewById(com.app.upincode.getqd.R.id.StaffCancelReservationButton);
        btyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GQLog.dObj(this, "CANCEL RESERVATION: onclick yes try to call the activity");
                try {
                    Intent intenty = new Intent(getApplicationContext(), GQMainActivity.class);
                    intenty.putExtra("target", getString(com.app.upincode.getqd.R.string.Frag_Target_Staff)); // start with the Staff fragment
                    startActivity(intenty);
                } catch (ClassCastException cce) {
                    GQLog.dObj(this, "CANCEL RESERVATION cast problem");
                }

            }
        });
        Button btno = (Button) findViewById(com.app.upincode.getqd.R.id.RegisterButton);
        btno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GQLog.dObj(this, "onclick on submit");
                try {

                    /*
                    This is where we attempt to send the reservation to the getqd website
                     */
                    final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                    //  First thing we need is the UserId for who is booking this reservation.
                    String logonJSON = globalVariable.getloginJSON();
                    if (logonJSON == null) {
                        GQLog.dObj(this, "on submit logonJSON is null");
                    }
                    JSONObject loginJSONObject = new JSONObject(logonJSON);
                    String UserId = loginJSONObject.getString("id");


                    // Next we need to get the venue id for the venue we are going to book this thing at
                    // This is based on the index into the listview same positin in JSON.
                    String UserVenue = globalVariable.getUserVenuesJSON();
                    int position = thePostion;
                    JSONArray jArray = new JSONArray(UserVenue);
                    JSONObject venue = jArray.getJSONObject(position);
                    int id = venue.getInt("id");
                    int n = 0;
                    String venueID = Integer.toString(id);

                    // Next we need the reason for the party or resevation (birthday,....)
                    GQLog.dObj(this, "(VenueID = " + venueID + "  Reason getSelectedItem= " + reason.getSelectedItem());
                    int targetReason = reason.getSelectedItemPosition();   // this target index is the same as the index in the JSON.
                    String theReason = new String("29");  // set the default if we find none.
                    GQLog.dObj(this, "VenueID = " + venueID + "  Reason getSelectedItemPosition= " + reason.getSelectedItemPosition());
                    try {
                        JSONArray jArrayR = new JSONArray(JSONReasons);
                        for (n = 0; n < jArrayR.length(); n = n + 1) {
                            JSONObject vR = jArrayR.getJSONObject(n);
                            if (n == targetReason) {
                                theReason = Integer.toString(vR.getInt("id"));
                                break;
                            }
                        }

                    } catch (Exception e) {
                        GQLog.dObj(this, "searching json for reason got a unique exception " + e.toString());
                    }

                    GQLog.dObj(this, "Calendar for Venue time zone for the reservations is " +
                                " Year:" + c1.get(Calendar.YEAR) +
                                " Month:" + (c1.get(Calendar.MONTH) + 1) +
                                " Day:" + (c1.get(Calendar.DAY_OF_MONTH)) +
                                " Hour:" + c1.get(Calendar.HOUR_OF_DAY) +
                                " Minute" + c1.get(Calendar.MINUTE));
                    // here is a big deal. C1 is in Venue time.  Setup at time of construction. Getqd website want GMT time.
                    // TargetDate will be in  GMT time for the C1 Venue time. One line does the trick. Lucky.
                    // NOt so lucky on the real device.  Had to change the code to stay away from DATE.

                    TimeZone fromTimeZone = TimeZone.getTimeZone("GMT");
                    TimeZone toTimeZone = TimeZone.getTimeZone(TargetTimeZone);

                    // Get a Calendar instance using the default time zone and locale.
                    Calendar calendar = Calendar.getInstance();

                    // Set the calendar's time with the given date
                    calendar.setTimeZone(fromTimeZone);
                    calendar.setTime(c1.getTime());                                         // set the club time into the gmt calendar
                    calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);


                    if (fromTimeZone.inDaylightTime(calendar.getTime())) {
                         GQLog.dObj(this, "============= Adjust for timezone differnce from");
                        calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
                    }


                    GQLog.dObj(this, "new new new Calendar for Venue time zone for the reservations is " +
                                " Year:" + calendar.get(Calendar.YEAR) +
                                " Month:" + (calendar.get(Calendar.MONTH) + 1) +
                                " Day:" + (calendar.get(Calendar.DAY_OF_MONTH)) +
                                " Hour:" + calendar.get(Calendar.HOUR_OF_DAY) +
                                " Minute" + calendar.get(Calendar.MINUTE));

                    String GMTTarget = formatDateFromCalendar(calendar);
                    GQLog.dObj(this, " new new GMT version of  Venue TIME:     " + GMTTarget);

                    // here is the URL we are going to use to send the up the reservation.
                    String URL = new String("api/reservations/new-staff-reservation-via-mobile/" + venueID + "/");
                    //We should do our final checks here before we send this thing up.
                    // Is there an @ in email address.
                    if (!(tvEmail.getText().toString().contains("@"))) {
                        ErrorInBooking("Not a proper email address");
                        return;
                    }
                    // No null for email,phone, firstname and last name

                    // if the details have not been typed in then do not send up.
                    String target = getResources().getString(com.app.upincode.getqd.R.string.GQReservationsSpecialDetails);
                    if (tvDetails.getText().toString().equals(target)) {
                        tvDetails.setText("");  // the field was unchanged so there is no special request
                    }

                    // create a model and send it up.
                    MyBaseModel mybm = new MyBaseModel(getApplicationContext());
                    mybm.execute("Post", URL, "22",
                            "booked_by", UserId,
                            "num_of_people", String.valueOf(groupSizeCount),
                            "email", tvEmail.getText().toString(),
                            "details", tvDetails.getText().toString(),
                            "arrival_date", GMTTarget,
                            "phone_number", tvMobilePhone.getText().toString(),
                            "location", "",   // Not Used
                            "type", theReason,  //reasons (birthday etc)
                            "time", btnTimePicker.getText().toString(),  // apparently not used
                            "first_name", tvFirstName.getText().toString(),
                            "last_name", tvLastName.getText().toString()

                    );

                } catch (Exception e) {
                    GQLog.dObj(this, "Main method exception" + e.toString());
                }
            }
        });
        // Finally get the reasons During Construction of this Class.  This important.
        // Once we get the reasons (birthday,stag party,.... we update the components on the main screen.
        try {
            //globalVariable = (GlobalClass) getApplicationContext();
            //Set name and email in global/application context
            UserVenue = globalVariable.getUserVenuesJSON();

            position = thePostion;
            JSONArray jArray = new JSONArray(UserVenue);
            JSONObject venue = jArray.getJSONObject(position);

            int id = venue.getInt("id");
            int n = 0;
            String venueID = Integer.toString(id);
            GQLog.dObj(this, "Getting the reasons with venue id:  " + venueID);

            String URL = new String("api/venue/" + venueID + "/reservations/reasons/");  //12 is the Venue ID
            MyBaseModelForReasons mybm = new MyBaseModelForReasons(getApplicationContext());
            mybm.execute("Get", URL, "0");

        } catch (Exception e) {
            GQLog.dObj(this, "Book Guest Activity constructor" + e.toString());
        }
    }

    public String formatDateFromCalendar(Calendar calendar) {

        GQLog.dObj(this, "new new new Calendar for Venue time zone for the reservations is " +
                    " Year:" + calendar.get(Calendar.YEAR) +
                    " Month:" + (calendar.get(Calendar.MONTH) + 1) +
                    " Day:" + (calendar.get(Calendar.DAY_OF_MONTH)) +
                    " Hour:" + calendar.get(Calendar.HOUR_OF_DAY) +
                    " Minute" + calendar.get(Calendar.MINUTE));

        String yyyy = new String(Integer.toString(calendar.get(Calendar.YEAR)));
        String MM = new String(Integer.toString(calendar.get(Calendar.MONTH) + 1));
        if (MM.length() == 1) MM = new String("0" + MM);  // fill the zero if need

        String dd = new String(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
        if (dd.length() == 1) dd = new String("0" + dd);  // fill the zero if need

        String HH = new String(Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)));
        if (HH.length() == 1) HH = new String("0" + HH);  // fill the zero if need

        String mm = new String(Integer.toString(calendar.get(Calendar.MINUTE)));
        if (mm.length() == 1) mm = new String("0" + mm);  // fill the zero if need
        //Format the date correctly
        String GMTTarget = new String(yyyy + "-" + MM + "-" + dd + "T" + HH + ":" + mm + ":00.000Z");
        return GMTTarget;
    }

    public void ErrorInBooking(String Error) {
        //the following was just a test to see if we could get the uservenues json.  It worked 7/28/2015
        //GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);

        dlgAlert.setMessage(Error);
        dlgAlert.setTitle(Error + " - Please Fix and Resubmit.");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();

        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

    }

    private Date offsetTimeZone(Date date, String fromTZ, String toTZ) {

// Construct FROM and TO TimeZone instances
        TimeZone fromTimeZone = TimeZone.getTimeZone(fromTZ);
        TimeZone toTimeZone = TimeZone.getTimeZone(toTZ);

// Get a Calendar instance using the default time zone and locale.
        Calendar calendar = Calendar.getInstance();

// Set the calendar's time with the given date
        calendar.setTimeZone(fromTimeZone);
        calendar.setTime(date);

        GQLog.dObj(this, "Input: " + calendar.getTime() + " in " + fromTimeZone.getDisplayName());

// FROM TimeZone to UTC
        calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);

        if (fromTimeZone.inDaylightTime(calendar.getTime())) {
            calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
        }

// UTC to TO TimeZone
        calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());

        if (toTimeZone.inDaylightTime(calendar.getTime())) {
            calendar.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
        }

        return calendar.getTime();

    }

    public void SelectDate() {
        String date = new String("");
        final Calendar c = Calendar.getInstance();
        mYear = c1.get(Calendar.YEAR);
        mMonth = c1.get(Calendar.MONTH);
        mDay = c1.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        c1.set(year, monthOfYear, dayOfMonth);
                        String date1 = new String((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                        SetDate(date1);

                    }
                }, c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH));
        dpd.show();
        //btnCalendar.setText(date);
    }

    public void SelectTime() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        c1.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c1.set(Calendar.MINUTE, minute);
                        String displaystring = null;
                        if (hourOfDay == 0) {
                            hourOfDay = 12;
                            displaystring = new String("AM");
                        } else if (hourOfDay >= 12) {
                            displaystring = new String("PM");
                        } else {
                            displaystring = new String("AM");
                        }
                        if (hourOfDay > 13) {
                            hourOfDay = hourOfDay - 12;
                        }
                        if (minute < 10) {
                            SetTime(hourOfDay + ":0" + minute + " " + displaystring);
                        } else {
                            SetTime(hourOfDay + ":" + minute + " " + displaystring);
                        }
                    }
                }, c1.get(Calendar.HOUR_OF_DAY), c1.get(Calendar.MINUTE), false);
        tpd.show();
    }

    public void SetDate(String time) {
        btnCalendar.setText(time);
    }

    public void SetTime(String time) {
        btnTimePicker.setText(time);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.app.upincode.getqd.R.menu.menu_gqbook_guest, menu);
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

    public void populateReasonList(List ReasonList) {
        ArrayAdapter dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ReasonList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        reason.setAdapter(dataAdapter);
    }

    public class MyBaseModelForReasons extends GQBaseModel {

        public MyBaseModelForReasons(Context context) {
            super(context);
        }


        public void updateView(Boolean retCode, int code, String JSON) {

            if (retCode == false) {
                GQLog.dObj(this, " MyBaseModelForReasons: Unable to Access the Website");
                return;
            }
            if (code == 200) {  //  JSON Looks like the following
                // we got the reason for this venue. Update the spinner list on the main activity.
                JSONReasons = JSON;
                List reasonList = new ArrayList();
                try {
                    JSONArray jArray = new JSONArray(JSON);
                    int n;
                    for (n = 0; n < jArray.length(); n = n + 1) {
                        JSONObject venue = jArray.getJSONObject(n);
                        reasonList.add(venue.getString("name"));  // Stick the new List into reasonList
                    }
                    populateReasonList(reasonList);
                } catch (Exception e) {
                    e.toString();
                }
            } else if (code == 201) {
                GQLog.dObj(this, " MyBaseModelForReasons Update View Code 201 - String returned from website" + JSON);
                Toast toast = Toast.makeText(getApplicationContext(), "Update view 201 status" + JSON, Toast.LENGTH_SHORT);
            } else if (code == 400) {
                GQLog.dObj(this, " MyBaseModelForReasons Getting 400  Error from the website");
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
                GQLog.dObj(this, "MyBaseModelForReasons:  Getting Error From Website =  " + code);


                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQBookGuestActivity.this);

                dlgAlert.setMessage("Network Problem");
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
                //printJSON(JSON);  // Looks Like the following
                //  We just successfully placed a reservation toast and back to staff of mainactivity.
                Toast.makeText(getApplicationContext(), JSON, Toast.LENGTH_LONG).show();
                Intent intenty = new Intent(getApplicationContext(), GQMainActivity.class);
                intenty.putExtra("target", getString(com.app.upincode.getqd.R.string.Frag_Target_Staff)); // start with the Staff fragment
                startActivity(intenty);
            } else if (code == 400) {
                GQLog.dObj(this, "MyBaseModel :UpdateView: Getting 400  Error from the website");
                // Username or password false, display and an error

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQBookGuestActivity.this);

                dlgAlert.setMessage(JSON);
                dlgAlert.setTitle("Type Problem:" + code);
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


                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getApplicationContext());

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
}
