package com.app.upincode.getqd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.models.GQBaseModel;
import com.app.upincode.getqd.utils.ISO8601;
import com.app.upincode.getqd.utils.MyViewDownloaderTaskSalesActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;


public class GQSalesActivity extends ActionBarActivity {
    ListView listview = null;
    String CurrentJSON = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.activity_sales);

        listview = (ListView) findViewById(com.app.upincode.getqd.R.id.listViewStaff);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GQLog.dObj(this, "Sales event Selected Clicked postion = " + position);
                final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                globalVariable.setSellingRole("Seller");
                Intent intenty = new Intent(getApplicationContext(), GQSelectTicketActivity.class);
                intenty.putExtra("position", Integer.toString(position)); // start with the display statistics activity
                startActivity(intenty);
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

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        String targetJSON = globalVariable.getBoxOfficeEventAccesses();
        if (targetJSON != null) {
            PopulateWithJSON(targetJSON);
        } else {
            String URL = new String("api/events/box-office/event-accesses/");

            MyBaseModel mybm = new MyBaseModel(getApplicationContext());
            mybm.execute("Get", URL, "0");// Instantiate the custom HttpClient
        }
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

    boolean verbose = false;

    public void PopulateWithJSON(String JSON) {

        ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
        try {
            String TargetTimeZone = new String("US/Mountain");  // the default is calgary time.
            JSONArray jArray = new JSONArray(JSON);
            int n;
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            //Set name and email in global/application context
            String UserVenue = globalVariable.getUserVenuesJSON();

            for (n = 0; n < jArray.length(); n = n + 1) {
                JSONObject event = jArray.getJSONObject(n);
                JSONObject venue_specifics = event.getJSONObject("venue");
                GQLog.dObj(this, "Parse Venue ID for this event IS is = " + venue_specifics.getString("id"));

                /*
                We are going to need to know which time zones the venues are in.  WE going to get a UTC time for GMT Time.  If we do not know
                the timezone of the venue we cannot put the correct time on the banner.  so we are going to search the USER/VENUE JSON to
                match the venue IDsand then grab the time zone for the venue.
                 */
                int index = 0;
                JSONArray jArrayVenue = new JSONArray(UserVenue);
                for (index = 0; index < jArrayVenue.length(); index = index + 1) {
                    JSONObject ven = jArrayVenue.getJSONObject(index);
                    if (venue_specifics.getInt("id") == ven.getInt("id")) {
                        if (verbose == true)
                             GQLog.dObj(this, "====FOUND TIME ZONE FOR the Venue " + ven.getString("timezone"));
                        TargetTimeZone = new String(ven.getString("timezone"));
                        if (verbose == true)
                             GQLog.dObj(this, "====FOUND TIME ZONE FOR the Venu");
                        break;
                    }
                }

                JSONObject event_specifics = event.getJSONObject("event");
                if (verbose == true)
                     GQLog.dObj(this, "Parse event image is = " + event_specifics.getString("image"));
                if (verbose == true)
                     GQLog.dObj(this, "Parse event name is = " + event_specifics.getString("name"));
                String eventName = event_specifics.getString("name");
                if (verbose == true)
                     GQLog.dObj(this, "Parse event start time is = " + event_specifics.getString("starts_on"));
                JSONObject venue = event.getJSONObject("venue");
                if (verbose == true)
                     GQLog.dObj(this, "Parse event venue name is = " + venue.getString("name"));
                String date = event_specifics.getString("starts_on");

                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
                SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
                SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
                SimpleDateFormat hourFormat = new SimpleDateFormat("hh");
                SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
                SimpleDateFormat ampmFormat = new SimpleDateFormat("aa");
                SimpleDateFormat dowFormat = new SimpleDateFormat("EEE");
                // Build the GMT time for the this reservation.
                //
                TimeZone fromTimeZone = TimeZone.getTimeZone("GMT");
                TimeZone toTimeZone = TimeZone.getTimeZone(TargetTimeZone);

                ISO8601 targetDateAndTime = new ISO8601();
                Calendar cal = targetDateAndTime.toCalendar(event_specifics.getString("starts_on"));
                if (verbose == true)
                     GQLog.dObj(this, " start time is = " + event_specifics.getString("starts_on"));
                DumpCalendar("CAL in GMT", cal);

                Calendar calendar = Calendar.getInstance();                             // get calendar for the venue
                // Set the calendar's time with the given date
                calendar.setTimeZone(toTimeZone);                                       // set the venue time zone
                calendar.setTime(cal.getTime());                                         // set the club time into the gmt calendar
                calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);   // now go to venue time.


                if (fromTimeZone.inDaylightTime(calendar.getTime())) {
                     GQLog.dObj(this, "============= Adjust for timezone differnce from");
                    calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
                }
                DumpCalendar("Calendar in Venue Time Zone", calendar);

                // This target date represent the time and date at the venue location (this is what we display)
                //Date targetDate = offsetTimeZone(c2.getTime(),"GMT",theTimeZone);
                String year = formatYYYYFromCalendar(calendar);//yearFormat.format(targetDate);
                String month = formatMMFromCalendar(calendar);//monthFormat.format(targetDate);
                String day = formatddFromCalendar(calendar);//dayFormat.format(targetDate);
                String hour = formatHHFromCalendar(calendar);// hourFormat.format(targetDate);
                String minute = formatmmFromCalendar(calendar);//minuteFormat.format(targetDate
                String dow = formatdowFromCalendar(calendar);//minuteFormat.format(targetDate);
/*

                String year    = yearFormat.format(targetDate);
                String month   = monthFormat.format(targetDate);
                String day     = dayFormat.format(targetDate);
                String hour    = hourFormat.format(targetDate);
                String minute  = minuteFormat.format(targetDate);
                String AMPM    = ampmFormat.format(targetDate);
                String dow     = dowFormat.format(targetDate);
*/
                dow = getDOW[Integer.parseInt(dow)];
                String AMPM = new String("AM");
                int hourInt = Integer.parseInt(hour);
                if (hourInt > 13) {
                    hourInt = hourInt - 12;
                    hour = Integer.toString(hourInt);
                    AMPM = new String("PM");
                }

                int mon = Integer.parseInt(month);
                mon = mon - 1;
                month = targetDateAndTime.getMonths[mon];

                if (minute.length() == 1) {
                    minute = new String("0" + minute);
                }

                String theDate = new String(dow + " " + month + " " + day);
                String theTime = new String(hour + ":" + minute + " " + AMPM);
                mNavItems.add(new NavItem(event_specifics.getString("image"),
                        eventName, // event name
                        " " + venue.getString("name"),
                        theDate + " ",
                        theTime + " "
                ));
            }
        } catch (Exception e) {
             GQLog.dObj(this, "Problem parsing json 444" + e.toString());
        }

        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        listview.setAdapter(adapter);

    }

    String[] getMonths = {" Jan", " Feb", " Mar", " Apr", " May", " Jun", " Jul", " Aug", "Sept", " Oct", " Nov", " Dec"};
    String[] getDOW = {"Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

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

    public String formatdowFromCalendar(Calendar calendar) {
        String dow = new String(Integer.toString(calendar.get(Calendar.DAY_OF_WEEK)));
        return (dow);
    }


    class NavItem {
        String mURL;
        String mEvent_Name;
        String mVenue_Name;
        String mDate;
        String mTime;

        public NavItem(String URL, String EventName, String Venue_Name, String Date, String Time) {
            mURL = URL;  // image URL
            mEvent_Name = EventName;
            mVenue_Name = Venue_Name;
            mDate = Date;
            mTime = Time;
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(com.app.upincode.getqd.R.layout.sales_item, null);
            } else {
                view = convertView;
            }
            //new MyViewDownloaderTask(view).execute(mNavItems.get(position).mURL);  // Download the bitmap into the view.
            new MyViewDownloaderTaskSalesActivity(view).execute(mNavItems.get(position).mURL);  // Download the bitmap into the view.


            TextView eventView = (TextView) view.findViewById(com.app.upincode.getqd.R.id.eventTitle);
            TextView venueView = (TextView) view.findViewById(com.app.upincode.getqd.R.id.eventVenue);
            TextView dateView = (TextView) view.findViewById(com.app.upincode.getqd.R.id.eventDate);
            TextView timeView = (TextView) view.findViewById(com.app.upincode.getqd.R.id.eventTime);

            eventView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        Toast.makeText(getApplicationContext(), "Event Info goes here", Toast.LENGTH_LONG).show();
                    }
                }
            });

            eventView.setText(mNavItems.get(position).mEvent_Name);
            eventView.setTextColor(Color.parseColor("#FFFFFF"));

            venueView.setText(mNavItems.get(position).mVenue_Name);
            venueView.setTextColor(Color.parseColor("#FFFFFF"));

            dateView.setText(mNavItems.get(position).mDate);
            dateView.setTextColor(Color.parseColor("#FFFFFF"));

            timeView.setText(mNavItems.get(position).mTime);
            timeView.setTextColor(Color.parseColor("#FFFFFF"));
            return view;
        }
    }

    private View.OnClickListener GetTicketsClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);
             GQLog.dObj(this, "position in listview=" + position);

        }

    };

    public class MyBaseModel extends GQBaseModel {

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
                final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                globalVariable.setBoxOfficeEventAccesses(JSON);  // save the JSON for future usage
                PopulateWithJSON(JSON);
                // printJSON(JSON);  // Looks Like the following

            } else if (code == 400) {
                GQLog.dObj(this, "Getting 400  Error from the website");
                // Username or password false, display and an error

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQSalesActivity.this);

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


                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQSalesActivity.this);

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
            /*
            {
  "slug": "slug",
  "venue": {
    "id": 0,
    "slug": "slug",
    "name": "",
    "thumbnail": "field"
  },
  "given_by": {
    "id": 0,
    "slug": "slug",
    "name": "",
    "thumbnail": "field"
  },
  "event": {
    "id": 0,
    "created": "datetime",
    "updated": "datetime",
    "slug": "slug",
    "name": "",
    "subtitle": "",
    "venue": "field",
    "location": "field",
    "starts_on": "datetime",
    "ends_on": "datetime",
    "opens_at": "datetime",
    "terms": "",
    "description": "",
    "getqd_fee": "decimal",
    "getqd_fee_added": "decimal",
    "image": "image upload",
    "thumbnail": "image upload",
    "facebook_id": ""
  },
  "complimentary_enabled": "field",
  "frontend_share_url": "field"
}
             */
            try {

                 GQLog.dObj(this, "Trying to print JSON.");

                JSONArray jArray = new JSONArray(JSON);
                int n;
                for (n = 0; n < jArray.length(); n = n + 1) {
                    JSONObject event = jArray.getJSONObject(n);
                    JSONObject venue = event.getJSONObject("venue");
                     GQLog.dObj(this, "Parse event venue name is = " + venue.getString("name"));
                    JSONObject eventinfo = event.getJSONObject("event");
                     GQLog.dObj(this, "Parse event name is = " + eventinfo.getString("name"));
                     GQLog.dObj(this, "Parse event start time is = " + eventinfo.getString("starts_on"));
                     GQLog.dObj(this, "Parse Slug  = " + event.getString("slug"));
                }


            } catch (Exception e) {
                 GQLog.dObj(this, "Problem Printing Parsing" + e.toString());
            }
        }
    }
}
