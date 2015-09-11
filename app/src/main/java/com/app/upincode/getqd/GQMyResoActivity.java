package com.app.upincode.getqd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.upincode.getqd.config.GQConfig;
import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.models.GQBaseModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;


public class GQMyResoActivity extends ActionBarActivity {
    int thePostion = 0;
    // stuff on the screen we are going to update
    ListView listview = null;
    TextView tvCheckedIn = null;
    TextView tvBooked = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.activity_myresos);

        // this gives us the position in the listview (same as position in json) we will access json this way.
        Intent intenty = getIntent();
        String target = intenty.getStringExtra("position");
        thePostion = Integer.parseInt(target);          // Setting the index into the venues array

        tvBooked = (TextView) findViewById(com.app.upincode.getqd.R.id.textView21);
        tvCheckedIn = (TextView) findViewById(com.app.upincode.getqd.R.id.textView23);

        // we go back to the staff upon pressing return.
        Button btyes = (Button) findViewById(com.app.upincode.getqd.R.id.ResoReturnButton);
        btyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GQLog.dObj(this, "Return to main activity: onclick yes try to call the activity");
                try {
                    Intent intenty = new Intent(getApplicationContext(), GQMainActivity.class);
                    intenty.putExtra("target", getString(com.app.upincode.getqd.R.string.Frag_Target_Staff)); // start with the Staff fragment
                    startActivity(intenty);
                } catch (ClassCastException cce) {
                    GQLog.dObj(this, "CANCEL RESERVATION cast problem");
                }

            }
        });
        /*
        The goal here is fire off TWO host requests.
        1) for a session for the venue.  Requests for reservation are relative to the venue session.
        2) Request all reservations that this user.
         */
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        //Set name and email in global/application context
        String logonJSON = globalVariable.getloginJSON();
        if (logonJSON == null) {
             GQLog.dObj(this, "logonJSON is null-cannot get id");
        }
        String UserId = null;

        // get the uservenueJSON
        String UserVenue = globalVariable.getUserVenuesJSON();

        // get a list view established cause we are going to update later
        listview = (ListView) findViewById(com.app.upincode.getqd.R.id.listViewStaff);

        // get the venue JSON to get the ID.
        int position = thePostion;
        int id = 0;
        try {

            JSONArray jArray = new JSONArray(UserVenue);
            JSONObject venue = jArray.getJSONObject(position);
            id = venue.getInt("id");

            JSONObject loginJSONObject = new JSONObject(logonJSON);
            UserId = loginJSONObject.getString("id");
             GQLog.dObj(this, "Booked by (using UserID)" + UserId);
        } catch (Exception e) {
            GQLog.eObj(this, "cannot get user id out of JSON");
        }


        // fire the session request off.
        String URLSession = new String("api/user/venues/session/" + id);

        MyBaseModel mybm = new MyBaseModel(getApplicationContext());
        // mybm.execute("Put",URLSession,"0");// Instantiate the custom HttpClient

        // Also request all reservations at this venue
        String URL = new String("api/venue/reservations/all/");
        String newURL = new String("api/venue/" + Integer.toString(id) + "/reservations/all/");
        String targetTime = new String("2015-08-04T13:17:39-05:00");
        Calendar c = Calendar.getInstance();
        //System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss");
        String formattedDate1 = df1.format(c.getTime());
        String formattedDate2 = df2.format(c.getTime());
        String formattedDate = new String(formattedDate1 + "T" + formattedDate2 + "-05:00");
         GQLog.dObj(this, "==========test for dates in get reservations=========");
         GQLog.dObj(this, targetTime);
         GQLog.dObj(this, formattedDate);
         GQLog.dObj(this, "==========test for dates in get reservations=========");

        MyBaseModel mybm2 = new MyBaseModel(getApplicationContext());
        mybm2.execute("Get", newURL, "8",
                "page_size", "100",
                "arrival_date_gt", formattedDate,
                "booked_by", UserId,
                "ordering", "-arrival_date");// Instantiate the custom HttpClient

        //viewFlipper.showNext();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.app.upincode.getqd.R.menu.menu_gqprofile, menu);
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

    int Booked = 0;
    int CheckedIn = 0;

    public void PopulateWithJSON(String JSON) {
        ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
        try {
            // Example of how to get stuff out of the JSON.
            // GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse customer.username"+ jo.getString("username"));  // this one
            Booked = 0;
            CheckedIn = 0;
            JSONObject loginJSONObject = new JSONObject(JSON);
            JSONArray jArray = loginJSONObject.getJSONArray("results");
            int n;
            for (n = 0; n < jArray.length(); n = n + 1) {
                JSONObject venue = jArray.getJSONObject(n);
                int num = Integer.parseInt(venue.getString("num_of_people"));
                Booked = Booked + num;
                JSONArray jArray1 = venue.getJSONArray("checkin_set");
                CheckedIn = CheckedIn + jArray1.length();  // We only care how many checked - not when they checked in.
                JSONObject jo = venue.getJSONObject("customer");

                int approved = 0; //false
                if (venue.getString("is_approved").equals("true")) {
                    approved = 1;
                }
                JSONArray ja = venue.getJSONArray("checkin_set");
                int len = ja.length();  // number of people checked in

                mNavItems.add(new NavItem(
                        jo.getString("username"),
                        venue.getString("arrival_date"),
                        jo.getString("email"),
                        venue.getString("details"),
                        Integer.valueOf(venue.getString("num_of_people")),
                        len,
                        approved
                ));
            }
        } catch (Exception e) {
             GQLog.dObj(this, "Parsing JSON for Building GUI " + e.toString());
        }
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        listview.setAdapter(adapter);

        tvCheckedIn.setText(Integer.toString(CheckedIn));
        tvBooked.setText(Integer.toString(Booked));
    }

    class NavItem {
        String mUserName;
        String mDate;
        String mEmail;
        String mDetails;
        String mTotalPartySize;
        String mTotalCheckedIn;
        String mApproved = new String("Not Approved");


        public NavItem(String UserName, String Date, String Email, String Details, int TotalPartySize, int TotalCheckIn, int Approved) {
            mUserName = UserName;
            mDate = Date;
            mEmail = Email;
            mDetails = Details;
            mTotalPartySize = Integer.toString(TotalPartySize);
            mTotalCheckedIn = Integer.toString(TotalCheckIn);
            if (Approved == 1)
                mApproved = new String("Approved");

        }
    }

    class DrawerListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<NavItem> mNavItems;

        public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
            mContext = context;
            mNavItems = navItems;
        }

        @Override
        public int getCount() {
            return mNavItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mNavItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        boolean doOnce = true;
        String theTimeZone = null;
        // we do not use these formats because it makes us use the DATE class. Which does not work well for our applications.
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat hourFormat = new SimpleDateFormat("hh");
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
        SimpleDateFormat ampmFormat = new SimpleDateFormat("aa");

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(com.app.upincode.getqd.R.layout.reservation_item, null);
            } else {
                view = convertView;
            }
            // Find the spots on the listview item to put stuff
            ImageView approveView = (ImageView) view.findViewById(com.app.upincode.getqd.R.id.resusersImage2);
            TextView monthView = (TextView) view.findViewById(com.app.upincode.getqd.R.id.resmonth);
            TextView dayView = (TextView) view.findViewById(com.app.upincode.getqd.R.id.resday);
            TextView timeView = (TextView) view.findViewById(com.app.upincode.getqd.R.id.restime);
            TextView usernameView = (TextView) view.findViewById(com.app.upincode.getqd.R.id.resusername);
            TextView detailView = (TextView) view.findViewById(com.app.upincode.getqd.R.id.resdetails);
            TextView checkinView = (TextView) view.findViewById(com.app.upincode.getqd.R.id.rescheckin);

            // This next section specific timezone of the Venue (in timezone)
            if (theTimeZone == null) { // do this just Once
                final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                String UserVenue = globalVariable.getUserVenuesJSON();
                position = thePostion;  // This is the index into the listview
                int id = 0;
                try {
                    JSONArray jArray = new JSONArray(UserVenue);
                    JSONObject venue = jArray.getJSONObject(position);
                    theTimeZone = venue.getString("timezone");

                } catch (Exception e) {
                     GQLog.eObj(this, "cannot get TimeZone from JSON");
                }
            }

            // deal with the Damn Time problem  Get the date it looks something like 2014-10-17T13:33:07.077Z
            String date = mNavItems.get(position).mDate;
            StringTokenizer st1 = new StringTokenizer(date, "T");  //  break into pre(Date) T and Post t (time)
            String newDate = (String) st1.nextElement();
            String newTime = (String) st1.nextElement();
            newTime = newTime.replace("Z", "");

            st1 = new StringTokenizer(newDate, "-");
            StringTokenizer st2 = new StringTokenizer(newTime, ":");
            String year = (String) st1.nextElement();
            String month = (String) st1.nextElement();
            String day = (String) st1.nextElement();
            String hour = (String) st2.nextElement();
            String minute = (String) st2.nextElement();
            String AMPM = new String("AM");


            // The month is off by one.
            int mon1 = Integer.parseInt(month);
            mon1 = mon1 - 1;
            month = Integer.toString(mon1);
            // Build the GMT time for the this reservation.
            //
            TimeZone fromTimeZone = TimeZone.getTimeZone("GMT");
            TimeZone toTimeZone = TimeZone.getTimeZone(theTimeZone);

            Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            c2.set(Integer.parseInt(year),
                    Integer.parseInt(month),
                    Integer.parseInt(day),
                    Integer.parseInt(hour),
                    Integer.parseInt(minute));


            if (GQConfig.DEBUG) DumpCalendar("C2 in GMT Time", c2);
            // Get a Calendar instance using the default time zone and locale.
            Calendar calendar = Calendar.getInstance();                             // get calendar for the venue
            // Set the calendar's time with the given date
            calendar.setTimeZone(toTimeZone);                                       // set the venue time zone
            calendar.setTime(c2.getTime());                                         // set the club time into the gmt calendar
            calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);   // now go to venue time.


            if (fromTimeZone.inDaylightTime(calendar.getTime())) {
                 GQLog.dObj(this, "============= Adjust for timezone differnce from");
                calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
            }
            DumpCalendar("Calendar in Venue Time Zone", calendar);

            // This target date represent the time and date at the venue location (this is what we display)
            //Date targetDate = offsetTimeZone(c2.getTime(),"GMT",theTimeZone);
            year = formatYYYYFromCalendar(calendar);//yearFormat.format(targetDate);
            month = formatMMFromCalendar(calendar);//monthFormat.format(targetDate);
            day = formatddFromCalendar(calendar);//dayFormat.format(targetDate);
            hour = formatHHFromCalendar(calendar);// hourFormat.format(targetDate);
            minute = formatmmFromCalendar(calendar);//minuteFormat.format(targetDate);
            //AMPM    = formatYYYYFromCalendar(c2);//ampmFormat.format(targetDate);


            int mon = Integer.parseInt(month);
            mon = mon - 1;
            month = getMonths[mon];

            int hourInt = Integer.parseInt(hour);
            if (hourInt > 13) {
                hourInt = hourInt - 12;
                hour = Integer.toString(hourInt);
                AMPM = new String("PM");
            }
            String details = mNavItems.get(position).mDetails;
            details = details.trim();  // get rid of white space leading and following.

            String CheckedIn = mNavItems.get(position).mTotalCheckedIn;
            String PartySize = mNavItems.get(position).mTotalPartySize;
            String partyStatus = new String(CheckedIn + "/" + PartySize + " Guests");

            monthView.setText("  " + month);
            dayView.setText(" " + day);
            timeView.setText(hour + ":" + minute + " " + AMPM);
            usernameView.setText(mNavItems.get(position).mUserName);
            detailView.setText(details);
            checkinView.setText(partyStatus);
            String approvedString = mNavItems.get(position).mApproved;
            if (approvedString.equals("Not Approved")) {
                String uri = "@drawable/notapproved";
                int id1 = getResources().getIdentifier(uri, null, getPackageName());//R.drawable.notapproved;
                Drawable res = getResources().getDrawable(id1);
                approveView.setImageDrawable(res);
            }
            view.setBackground(getResources().getDrawable(com.app.upincode.getqd.R.drawable.dark_button_normal_background2x));
            return view;
        }
    }

    String[] getMonths = {" Jan", " Feb", " Mar", " Apr", " May", " Jun", " Jul", " Aug", "Sept", " Oct", " Nov", " Dec"};

    public void DumpCalendar(String calname, Calendar c2) {
        String year = null;
        String month = null;
        String day = null;
        String hour = null;
        String minute = null;

        year = formatYYYYFromCalendar(c2);//yearFormat.format(targetDate);
        month = formatMMFromCalendar(c2);//monthFormat.format(targetDate);
        day = formatddFromCalendar(c2);//dayFormat.format(targetDate);
        hour = formatHHFromCalendar(c2);// hourFormat.format(targetDate);
        minute = formatmmFromCalendar(c2);//minuteFormat.format(targetDate);
        //AMPM    = formatYYYYFromCalendar(c2);//ampmFormat.format(targetDate);


         GQLog.dObj(this, " Calendar = " + calname +
                "   year:   " + year +
                "   month:   " + month +
                "   day:   " + day +
                "   hour:   " + hour +
                "   minute:   " + minute);

    }

    public String formatYYYYFromCalendar(Calendar calendar) {
        String yyyy = new String(Integer.toString(calendar.get(Calendar.YEAR)));
        return (yyyy);
    }

    public String formatMMFromCalendar(Calendar calendar) {
        String MM = new String(Integer.toString(calendar.get(Calendar.MONTH) + 1));
        if (MM.length() == 1) MM = new String("0" + MM);  // fill the zero if need
        return (MM);
    }

    public String formatddFromCalendar(Calendar calendar) {
        String dd = new String(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
        if (dd.length() == 1) dd = new String("0" + dd);  // fill the zero if need
        return (dd);
    }

    public String formatHHFromCalendar(Calendar calendar) {
        String HH = new String(Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)));
        if (HH.length() == 1) HH = new String("0" + HH);  // fill the zero if need
        return (HH);
    }

    public String formatmmFromCalendar(Calendar calendar) {
        String mm = new String(Integer.toString(calendar.get(Calendar.MINUTE)));
        if (mm.length() == 1) mm = new String("0" + mm);  // fill the zero if need
        return (mm);
    }

    public class MyBaseModel extends GQBaseModel {
        boolean secondtry = false;

        public MyBaseModel(Context context) {
            super(context);
        }


        public void updateView(Boolean retCode, int code, String JSON) {
             GQLog.dObj(this, "doing updateView retcode = " + retCode + "Code = " + code);
            if (retCode == false) {
                 GQLog.dObj(this, "Unable to Access the Website");
                return;
            }
            if ((code == 200) || (code == 201)) {  //  JSON Looks like the following
                //Log.d("GQMyResoActiviy", "Looking to print JSON here" + JSON);
                //if(secondtry == false) { secondtry = true; return;}  // skip it the first time - print the second
                PopulateWithJSON(JSON);
                //printJSON(JSON);  // Looks Like the following
/*
What to take out of the parse and put into listview item
8-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse created 3095
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse name Jessica Lacey
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse num_of_people 3  (0/9 this is the 9)
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse details  Extra tables and Chairs   (PGA TOUR CHAMPS)
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse sms_data
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse reason 1
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse booked_by 12
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse is_approved true   (APPROVED)
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse arrival_date 2015-08-11T03:00:00Z  (Aug 3,1200am)
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse location null
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse customer.id 294
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse customer.first_name Jessica
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse customer.last_name Lacey
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse customer.usernameJessica Lacey  (Jack Wilson)
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse customer.email jessica@yahoo.com
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse customer.gravatar https://gravatar.com/avatar/8f2b61d011f059afa11e589363a8c663
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse customer.gravatar_small https://gravatar.com/avatar/8f2b61d011f059afa11e589363a8c663
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse customer.phone_number 4034075822
08-06 21:30:02.417    9456-9456/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse customer.checkin_set [1155,1156]   (0/9 this is the 0)
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ reservation 0 Parse customer.type_text Birthday
08-06 21:22:27.034    9334-9334/com.app.upincode.getqd D/QMyResoActiviy﹕ ======================================================
08-06 21:
                {"count": 385, "next": "http://beta.getqd.me/api/venue/reservations/all/?page=2", "previous": null, "next_page_number": 2, "previous_page_number": null, "page_number": 1, "num_pages": 20, "next_list": [2, 3, 4, 5, 6, 7, 8, 9, 10, 11], "previous_list": [], "results": [{"id": 2311, "name": "Milan GetQd Pecov", "num_of_people": 10, "details": "Corporate function", "sms_data": null, "reason": 1, "booked_by": null, "is_approved": false, "arrival_date": "2014-06-22T04:00:00Z", "location": null, "type": 49, "customer": {"id": 2, "first_name": "Milan GetQd", "last_name": "Pecov", "username": "Milan GetQd Pecov", "email": "mpecov@yahoo.ca", "gravatar": "https://gravatar.com/avatar/d54f492c90d72fc63b716859a11bcb89", "gravatar_small": "https://gravatar.com/avatar/d54f492c90d72fc63b716859a11bcb89", "phone_number": null}, "checkin_set": [], "type_text": "Other"},
                {"id": 2633, "name": "Lorriane Haston", "num_of_people": 3, "details": "Corporate function", "sms_data": null, "reason": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T13:33:07.077Z", "location": null, "type": 49, "customer": {"id": 167, "first_name": "Lorriane", "last_name": "Haston", "username": "Lorriane Haston", "email": "soksfa3@afasfd.caa", "gravatar": "https://gravatar.com/avatar/78750f518874cf6dfbe6e936911e405d", "gravatar_small": "https://gravatar.com/avatar/78750f518874cf6dfbe6e936911e405d", "phone_number": null}, "checkin_set": [], "type_text": "Other"},

                {"id": 2621, "name": "Cordell Roscoe", "num_of_people": 6, "details": "Corporate function", "sms_data": null, "reason": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T15:13:07.077Z", "location": null, "type": 49, "customer": {"id": 169, "first_name": "Cordell", "last_name": "Roscoe", "username": "Cordell Roscoe", "email": "soksfa5@afasfd.caa", "gravatar": "https://gravatar.com/avatar/1d245263d1555d7e5d7c61e6191cbb22", "gravatar_small": "https://gravatar.com/avatar/1d245263d1555d7e5d7c61e6191cbb22", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2619, "name": "Oswaldo Hartman", "num_of_people": 5, "details": "Stag/Stagette", "sms_data": null, "reason": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T13:23:07.077Z", "location": null, "type": 49, "customer": {"id": 166, "first_name": "Oswaldo", "last_name": "Hartman", "username": "Oswaldo Hartman", "email": "soksfa2@afasfd.caa", "gravatar": "https://gravatar.com/avatar/d3297ba5336906f5a1c4545f767332bb", "gravatar_small": "https://gravatar.com/avatar/d3297ba5336906f5a1c4545f767332bb", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2657, "name": "Oswaldo Hartman", "num_of_people": 3, "details": "Birthday", "sms_data": null, "reason": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T14:53:07.077Z", "location": null, "type": 49, "customer": {"id": 166, "first_name": "Oswaldo", "last_name": "Hartman", "username": "Oswaldo Hartman", "email": "soksfa2@afasfd.caa", "gravatar": "https://gravatar.com/avatar/d3297ba5336906f5a1c4545f767332bb", "gravatar_small": "https://gravatar.com/avatar/d3297ba5336906f5a1c4545f767332bb", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2614, "name": "Cordell Roscoe", "num_of_people": 6, "details": "Bottle service", "sms_data": null, "reason": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T14:13:07.077Z", "location": null, "type": 49, "customer": {"id": 169, "first_name": "Cordell", "last_name": "Roscoe", "username": "Cordell Roscoe", "email": "soksfa5@afasfd.caa", "gravatar": "https://gravatar.com/avatar/1d245263d1555d7e5d7c61e6191cbb22", "gravatar_small": "https://gravatar.com/avatar/1d245263d1555d7e5d7c61e6191cbb22", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2645, "name": "Dwayne Cessna", "num_of_people": 3, "details": "General", "sms_data": null, "reason": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T15:13:07.077Z", "location": null, "type": 49, "custom
*/

            } else if (code == 400) {
                 GQLog.dObj(this, "Getting 400  Error from the website");
                // Username or password false, display and an error

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getApplicationContext());

                dlgAlert.setMessage("Website 400 Problem");
                dlgAlert.setTitle(JSON);
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

            } else {
                 GQLog.dObj(this, "Getting Error From Website =  " + code);


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
                /*
  {"count": 385, "next": "http://beta.getqd.me/api/venue/reservations/all/?page=2", "previous": null, "next_page_number": 2, "previous_page_number": null, "page_number": 1, "num_pages": 20, "next_list": [2, 3, 4, 5, 6, 7, 8, 9, 10, 11], "previous_list": [], "results": [
                 */
                 GQLog.dObj(this, "Trying to print JSON.");
                JSONObject loginJSONObject = new JSONObject(JSON);
                 GQLog.dObj(this, "Parse count = " + loginJSONObject.getString("count"));
                 GQLog.dObj(this, "Parse next = " + loginJSONObject.getString("next"));
                 GQLog.dObj(this, "Parse previous = " + loginJSONObject.getString("previous"));
                 GQLog.dObj(this, "Parse next_page_number = " + loginJSONObject.getString("next_page_number"));
                 GQLog.dObj(this, "Parse prev_page_number = " + loginJSONObject.getString("previous_page_number"));
                 GQLog.dObj(this, "Parse num_pages = " + loginJSONObject.getString("num_pages"));
                 GQLog.dObj(this, "Parse next_list = " + loginJSONObject.getString("next_list"));
                 GQLog.dObj(this, "Parse previous_list " + loginJSONObject.getString("previous_list"));
                JSONArray jArray = loginJSONObject.getJSONArray("results");
                int n;
                for (n = 0; n < jArray.length(); n = n + 1) {
                    JSONObject venue = jArray.getJSONObject(n);

                     GQLog.dObj(this, "    reservation " + n + " Parse created " + venue.getString("id"));
                     GQLog.dObj(this, "    reservation " + n + " Parse name " + venue.getString("name"));
                     GQLog.dObj(this, "    reservation " + n + " Parse num_of_people " + venue.getString("num_of_people"));  // this one
                     GQLog.dObj(this, "    reservation " + n + " Parse details " + venue.getString("details")); // this one
                     GQLog.dObj(this, "    reservation " + n + " Parse sms_data " + venue.getString("sms_data"));
                     GQLog.dObj(this, "    reservation " + n + " Parse reason " + venue.getString("reason"));
                     GQLog.dObj(this, "    reservation " + n + " Parse booked_by " + venue.getString("booked_by"));
                     GQLog.dObj(this, "    reservation " + n + " Parse is_approved " + venue.getString("is_approved"));// this one
                     GQLog.dObj(this, "    reservation " + n + " Parse arrival_date " + venue.getString("arrival_date"));
                     GQLog.dObj(this, "    reservation " + n + " Parse location " + venue.getString("location"));
                    JSONObject jo = venue.getJSONObject("customer");
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.id " + jo.getString("id"));
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.first_name " + jo.getString("first_name"));
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.last_name " + jo.getString("last_name"));
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.username" + jo.getString("username"));  // this one
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.email " + jo.getString("email"));  // this one
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.gravatar " + jo.getString("gravatar"));
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.gravatar_small " + jo.getString("gravatar_small"));
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.phone_number " + jo.getString("phone_number"));
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.checkin_set " + venue.getString("checkin_set")); // this one
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.type_text " + venue.getString("type_text"));

                     GQLog.dObj(this, "======================================================");
                }
            } catch (Exception e) {
                 GQLog.dObj(this, "Problem Printing" + e.toString());
            }
        }
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

        //Log.d("reso", " offsetTimeZone Input: " + calendar.getTime() + " in " + fromTimeZone.getDisplayName());

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
}