package com.app.upincode.getqd.activities.frags;


/**
 * Created by herbert on 7/23/2015.
 */


//TODO remove
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.upincode.getqd.activities.GQSelectTicketActivity;
import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.utils.ISO8601;
import com.app.upincode.getqd.utils.MyViewDownloaderTaskSalesActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GQMain_event_fragment extends Fragment {
    //private OnFragmentInteractionListener mlistener;
    ListView listview = null;
    String CurrentJSON = null;
    Activity thisActivity = null;

    public static GQMain_event_fragment newInstance() {
        GQMain_event_fragment fragment = new GQMain_event_fragment();
        return fragment;
    }

    public GQMain_event_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gqmain_fragment_event, container, false);

        listview = (ListView) rootView.findViewById(R.id.listViewEvents);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GQLog.dObj(this, "Sales event Selected Clicked postion = " + position);
                final GlobalClass globalVariable = (GlobalClass) thisActivity.getApplicationContext();
                globalVariable.setSellingRole("Buyer");
                Intent intent = new Intent(thisActivity.getApplicationContext(), GQSelectTicketActivity.class);
                intent.putExtra("position", Integer.toString(position)); // start with the display statistics activity
                startActivity(intent);
            }

        });


        final GlobalClass globalVariable = (GlobalClass) thisActivity.getApplicationContext();
        String targetJSON = globalVariable.getBoxOfficeEventAccesses();
        if (targetJSON != null) {
            PopulateWithJSON(targetJSON);
        } else {
            String URL = new String("api/events/box-office/event-accesses/");

            //TODO fix
//            MyBaseModel mybm = new MyBaseModel(thisActivity.getApplicationContext());
//            mybm.execute("Get", URL, "0");// Instantiate the custom HttpClient
        }
        return rootView;
    }


    boolean verbose = false;

    public void PopulateWithJSON(String JSON) {

        ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
        try {
            String TargetTimeZone = new String("US/Mountain");  // the default is calgary time.
            JSONArray jArray = new JSONArray(JSON);
            int n;
            final GlobalClass globalVariable = (GlobalClass) thisActivity.getApplicationContext();
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

                ISO8601 targetDateAndTime = new ISO8601();
                Calendar cal = targetDateAndTime.toCalendar(event_specifics.getString("starts_on"));
                Date targetDate = targetDateAndTime.offsetTimeZone(cal.getTime(), "GMT", TargetTimeZone);

                String year = yearFormat.format(targetDate);
                String month = monthFormat.format(targetDate);
                String day = dayFormat.format(targetDate);
                String hour = hourFormat.format(targetDate);
                String minute = minuteFormat.format(targetDate);
                String AMPM = ampmFormat.format(targetDate);
                String dow = dowFormat.format(targetDate);
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

        DrawerListAdapter adapter = new DrawerListAdapter(getActivity(), mNavItems);
        listview.setAdapter(adapter);

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
                view = inflater.inflate(R.layout.sales_item, null);
            } else {
                view = convertView;
            }
            //new MyViewDownloaderTask(view).execute(mNavItems.get(position).mURL);  // Download the bitmap into the view.
            new MyViewDownloaderTaskSalesActivity(view).execute(mNavItems.get(position).mURL);  // Download the bitmap into the view.


            TextView eventView = (TextView) view.findViewById(R.id.eventTitle);
            TextView venueView = (TextView) view.findViewById(R.id.eventVenue);
            TextView dateView = (TextView) view.findViewById(R.id.eventDate);
            TextView timeView = (TextView) view.findViewById(R.id.eventTime);

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
             GQLog.dObj(this, "position in listView=" + position);

        }

    };

    //TODO remove
//    public class MyBaseModel extends GQBaseModel {
//
//        public MyBaseModel(Context context) {
//            super(context);
//        }
//
//
//        public void updateView(Boolean retCode, int code, String JSON) {
//            GQLog.dObj(this, "doing updateView retcode = " + retCode + "Code = " + code);
//            if (retCode == false) {
//                GQLog.dObj(this, "Unable to Access the Website");
//                return;
//            }
//            if ((code == 200) || (code == 201)) {  //  JSON Looks like the following
//                final GlobalClass globalVariable = (GlobalClass) thisActivity.getApplicationContext();
//                globalVariable.setBoxOfficeEventAccesses(JSON);  // save the JSON for future usage
//                PopulateWithJSON(JSON);
//                // printJSON(JSON);  // Looks Like the following
//
//            } else if (code == 400) {
//                GQLog.dObj(this, "Getting 400  Error from the website");
//                // Username or password false, display and an error
//
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(thisActivity);
//
//                dlgAlert.setMessage("Website 400 Problem");
//                dlgAlert.setTitle(JSON);
//                dlgAlert.setPositiveButton("OK", null);
//                dlgAlert.setCancelable(true);
//                dlgAlert.create().show();
//
//                dlgAlert.setPositiveButton("Ok",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//
//            } else {
//                 GQLog.dObj(this, "Getting Error From Website =  " + code);
//
//
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(thisActivity);
//
//                dlgAlert.setMessage("Error Message " + code);
//                dlgAlert.setTitle("Server Says " + JSON);
//                dlgAlert.setPositiveButton("OK", null);
//                dlgAlert.setCancelable(true);
//                dlgAlert.create().show();
//
//                dlgAlert.setPositiveButton("Ok",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//
//            }
//
//        }
//
//        public void printJSON(String JSON) {
//            /*
//            {
//  "slug": "slug",
//  "venue": {
//    "id": 0,
//    "slug": "slug",
//    "name": "",
//    "thumbnail": "field"
//  },
//  "given_by": {
//    "id": 0,
//    "slug": "slug",
//    "name": "",
//    "thumbnail": "field"
//  },
//  "event": {
//    "id": 0,
//    "created": "datetime",
//    "updated": "datetime",
//    "slug": "slug",
//    "name": "",
//    "subtitle": "",
//    "venue": "field",
//    "location": "field",
//    "starts_on": "datetime",
//    "ends_on": "datetime",
//    "opens_at": "datetime",
//    "terms": "",
//    "description": "",
//    "getqd_fee": "decimal",
//    "getqd_fee_added": "decimal",
//    "image": "image upload",
//    "thumbnail": "image upload",
//    "facebook_id": ""
//  },
//  "complimentary_enabled": "field",
//  "frontend_share_url": "field"
//}
//             */
//            try {
//
//                 GQLog.dObj(this, "Trying to print JSON.");
//
//                JSONArray jArray = new JSONArray(JSON);
//                int n;
//                for (n = 0; n < jArray.length(); n = n + 1) {
//                    JSONObject event = jArray.getJSONObject(n);
//                    JSONObject venue = event.getJSONObject("venue");
//                     GQLog.dObj(this, "Parse event venue name is = " + venue.getString("name"));
//                    JSONObject eventinfo = event.getJSONObject("event");
//                     GQLog.dObj(this, "Parse event name is = " + eventinfo.getString("name"));
//                     GQLog.dObj(this, "Parse event start time is = " + eventinfo.getString("starts_on"));
//                     GQLog.dObj(this, "Parse Slug  = " + event.getString("slug"));
//                }
//
//
//            } catch (Exception e) {
//                 GQLog.dObj(this, "Problem Printing Parsing" + e.toString());
//            }
//        }
//    }
}
