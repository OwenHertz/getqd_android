package com.app.upincode.getqd.frags;


/**
 * Created by herbert on 7/23/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.upincode.getqd.GQMainActivity;
import com.app.upincode.getqd.GQTicketsDisplayActivity;
import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.models.GQBaseModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.app.upincode.getqd.utils.ISO8601;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class GQMain_ticket_fragment extends Fragment {
    //private OnFragmentInteractionListener mlistener;
    int thePostion = 0;
    // stuff on the screen we are going to update
    ListView listview = null;
    boolean debug = true;
    Activity ticket_activity = null;

    public static GQMain_ticket_fragment newInstance() {
        GQMain_ticket_fragment fragment = new GQMain_ticket_fragment();
        return fragment;
    }

    public GQMain_ticket_fragment() {
    }


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ticket_activity = activity;
        ((GQMainActivity) activity).onSectionAttached(4);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gqmain_fragment_ticket, container, false);

        listview = (ListView) rootView.findViewById(R.id.listViewTickets);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GQLog.dObj(this, "ticket Clicked postion = " + position);
                Intent intenty = new Intent((GQMainActivity) ticket_activity, GQTicketsDisplayActivity.class);
                intenty.putExtra("position", Integer.toString(position)); // start with the display statistics activity
                startActivity(intenty);
            }

        });

        final GlobalClass globalVariable = (GlobalClass) ticket_activity.getApplicationContext();
        //we are interested in populating listview with JSON. If don't have any go get some.
        String analyticsJSON = null;
        if (analyticsJSON == null) {
            String logonJSON = globalVariable.getloginJSON();
            if (logonJSON == null) {
                 GQLog.dObj(this, "logonJSON is null-cannot get id");
            }
            String UserId = null;
            try {
                JSONObject loginJSONObject = new JSONObject(logonJSON);
                UserId = loginJSONObject.getString("id");
            } catch (Exception e) {
                 GQLog.eObj(this, "cannot get user id");
            }
            // here is a big deal. C1 is in Venue time.  Setup at time of construction. Getqd website want GMT time.
            // TargetDate will be in  GMT time for the C1 Venue time. One line does the trick. Lucky.
            Calendar c1 = new GregorianCalendar();
            Date TargetDate = c1.getTime();

            //Format the date correctly
            SimpleDateFormat timeFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat2 = new SimpleDateFormat("hh:mm:");
            String targetDate = new String(timeFormat1.format(TargetDate) + "T" + timeFormat2.format(TargetDate) + "00.000Z");

            String URL = new String("api/user/tickets/ticket-groups/");

            MyBaseModel mybm = new MyBaseModel((GQMainActivity) ticket_activity);
            mybm.execute("Get", URL, "6",
                    "starts_on__gte", targetDate,
                    "page_size", "20",
                    "ordering", "starts_on");// Instantiate the custom HttpClient
        } else {
            PopulateViewListView(analyticsJSON);
        }
        ;
        return rootView;
    }


    public static int target_v = 0;

    public void PopulateViewListViewORIG(String JSON) {

        try {
            JSONObject loginJSONObject = new JSONObject(JSON);
            int our_target_v = target_v;
            target_v = target_v + 1;  // next build will build the next view.  we only build one.
            GQLog.dObj(this, "JSON Parse count = " + loginJSONObject.getString("count"));
            int count = Integer.parseInt(loginJSONObject.getString("count"));
            int v = 0;
            JSONArray jArray = loginJSONObject.getJSONArray("results");
            String eventName[] = new String[jArray.length()];
            String cash[] = new String[jArray.length()];
            String credit[] = new String[jArray.length()];
            String debit[] = new String[jArray.length()];
            String comp[] = new String[jArray.length()];

            for (; v < count; v = v + 1) {    // N represents each Event
                try {
                    boolean verbose = false;
                     GQLog.dObj(this, "attempting to build view target = " + v);
                    JSONObject venue = jArray.getJSONObject(v);
                    JSONObject event = venue.getJSONObject("event");
                    if (verbose == true)
                         GQLog.dObj(this, "    list Item " + v + " Parse name " + event.getString("name"));
                    eventName[v] = event.getString("name");

                    /*  From  the Iphone Developer
                            Cash = 1,
                            CreditCard = 2,
                            Interac = 3,
                            Complimentary = 4,
                            Free = 5,
                    */
                    JSONArray tt = venue.getJSONArray("tt_stats");
                    int ca = 0, cr = 0, de = 0, co = 0;
                    double caf = 0, crf = 0, def = 0, cof = 0;

                    int x;
                    for (x = 0; x < tt.length(); x = x + 1) {
                        JSONObject tt_target = tt.getJSONObject(x);
                        JSONObject tType = tt_target.getJSONObject("ticket_type");
                        if (verbose == true)
                             GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount id " + Integer.toString(tType.getInt("id")));
                        if (verbose == true)
                             GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount name " + tType.getString("name"));
                        if (verbose == true)
                             GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse number ticket sold " + Integer.toString(tt_target.getInt("num_tickets_sold")));
                        if (verbose == true)
                             GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount sold " + tt_target.getString("amount_sold"));
                        JSONObject breakdown = tt_target.getJSONObject("tickets_sold_breakdown");

                        double amount_sold = Double.parseDouble(tt_target.getString("amount_sold"));
                        double ticket_price = amount_sold / tt_target.getInt("num_tickets_sold");
                        if (verbose == true)
                             GQLog.dObj(this, "Ticket Price =" + String.format("%.2f", ticket_price));

                        String cashtemp = new String("0");
                        String credittemp = new String("0");
                        String debittemp = new String("0");
                        String comptemp = new String("0");


                        try {
                            cashtemp = Integer.toString(breakdown.getInt("1"));
                            ca = ca + breakdown.getInt("1");
                            caf = caf + (breakdown.getInt("1") * ticket_price);
                            if (verbose == true)
                                 GQLog.dObj(this, "cashtemp=" + cashtemp + "cash total: " + ca + " $ total = " + String.format("%.2f", caf) + " ticket price = " + String.format("%.2f", ticket_price));
                        } catch (Exception e) {
                            if (verbose == true)
                                 GQLog.dObj(this, "problem getting cash - skipping making it 0");
                        }
                        try {
                            credittemp = Integer.toString(breakdown.getInt("2"));
                            cr = cr + breakdown.getInt("2");
                            crf = crf + (breakdown.getInt("2") * ticket_price);
                            if (verbose == true)
                                 GQLog.dObj(this, "credittemp=" + credittemp + "credittemp total: " + cr);
                        } catch (Exception e) {
                            if (verbose == true)
                                 GQLog.dObj(this, "problem getting credit- skipping making it 0");
                        }
                        try {
                            debittemp = Integer.toString(breakdown.getInt("3"));
                            de = de + breakdown.getInt("3");
                            def = def + (breakdown.getInt("3") * ticket_price);
                            if (verbose == true)
                                 GQLog.dObj(this, "debittemp=" + debittemp + "debittemp total: " + de);
                        } catch (Exception e) {
                            if (verbose == true)
                                GQLog.d("stat", "problem getting debit - skipping making it 0");
                        }
                        try {
                            comptemp = Integer.toString(breakdown.getInt("4"));
                            co = co + breakdown.getInt("4");
                            cof = cof + (breakdown.getInt("4") * ticket_price);
                            if (verbose == true)
                                 GQLog.dObj(this, "comptemp=" + comptemp + "comptemp total: " + co);
                        } catch (Exception e) {
                            if (verbose == true)
                                GQLog.d("stat", "problem getting debit - skipping making it 0");
                        }

                    }
                    if (verbose == true)
                         GQLog.dObj(this, "====total items sold========> ca= " + ca + " cr= " + cr + " de= " + de + " co= " + co);
                    if (verbose == true)
                         GQLog.dObj(this, "============> caf= " + String.format("%.2f", caf) + " cr= " + String.format("%.2f", crf) + " de= " + String.format("%.2f", def) + " co= " + String.format("%.2f", cof));
                    // setup the field to be displayed in stats itme.
                    cash[v] = "$" + String.format("%.2f", caf);
                    credit[v] = "$" + String.format("%.2f", crf);
                    debit[v] = "$" + String.format("%.2f", def);
                    comp[v] = "$" + String.format("%.2f", cof);
                } catch (Exception e) {
                     GQLog.dObj(this, " Problem while parsing JSON = " + e.toString());
                }
            }
            PopulateListView(eventName, cash, credit, debit, comp);
            // }
        } catch (Exception e) {
             GQLog.dObj(this, " exception parsing" + e.toString());
        }
    }

    public boolean verbose = true;
    public boolean skipFindingTargeTimeZone = true;

    public void PopulateViewListView(String JSON) {
        ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
        String TargetTimeZone = new String("US/Mountain");  // the default is calgary time.

        final GlobalClass globalVariable = (GlobalClass) ticket_activity.getApplicationContext();
        //Set name and email in global/application context
        String UserVenue = globalVariable.getUserVenuesJSON();

        try {
            JSONObject loginJSONObject = new JSONObject(JSON);

            JSONArray jArray = loginJSONObject.getJSONArray("results");
            int n;
            for (n = 0; n < jArray.length(); n = n + 1) {
                JSONObject event = jArray.getJSONObject(n);
                if (skipFindingTargeTimeZone == false) {
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
                }
                JSONObject event2 = jArray.getJSONObject(n);

                 GQLog.dObj(this, "    list Item " + n + " Parse id " + Integer.toString(event2.getInt("id")));
                JSONObject type = event2.getJSONObject("type");
                JSONObject e = type.getJSONObject("event");
                 GQLog.dObj(this, "    list Item " + n + " Parse name " + e.getString("name"));
                 GQLog.dObj(this, "    list Item " + n + " Parse starts_on " + e.getString("starts_on"));


                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
                SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
                SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
                SimpleDateFormat hourFormat = new SimpleDateFormat("hh");
                SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
                SimpleDateFormat ampmFormat = new SimpleDateFormat("aa");
                SimpleDateFormat dowFormat = new SimpleDateFormat("EEE");

                ISO8601 targetDateAndTime = new ISO8601();
                Calendar cal = targetDateAndTime.toCalendar(e.getString("starts_on"));
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

                JSONArray tt = event.getJSONArray("tickets");
                 GQLog.dObj(this, "    list Item Number of tickets = " + tt.length());
                String name = e.getString("name");
                if (name.length() > 45) {
                    name = new String(name.substring(0, 45) + new String("  ..."));
                }
                mNavItems.add(new NavItem(name, theDate + " " + theTime, Integer.toString(tt.length())));
            }
        } catch (Exception e) {
             GQLog.dObj(this, "json parsing " + e.toString());
        }
        DrawerListAdapter adapter = new DrawerListAdapter(getActivity(), mNavItems);
        listview.setAdapter(adapter);
    }


    public void PopulateListView(String[] eventName, String[] cash, String[] credit, String[] debit, String[] comp) {
        ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
        for (int n = 0; n < eventName.length; n = n + 1) {
            // mNavItems.add(new NavItem( eventName[n],cash[n],credit[n],debit[n],comp[n]));
        }
        DrawerListAdapter adapter = new DrawerListAdapter(getActivity(), mNavItems);
        listview.setAdapter(adapter);
    }

    class NavItem {
        String mTitle;
        String mDate;
        String mCount;

        public NavItem(String title, String date, String count) {
            mTitle = title;
            mDate = date;
            mCount = count;

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
                view = inflater.inflate(R.layout.ticket_item, null);
            } else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.eventTitle);
            TextView dateView = (TextView) view.findViewById(R.id.eventDateAndTime);
            TextView countView = (TextView) view.findViewById(R.id.eventTicketCount);


            titleView.setText(mNavItems.get(position).mTitle);
            dateView.setText(mNavItems.get(position).mDate);
            countView.setText(mNavItems.get(position).mCount);

            return view;
        }
    }


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
                final GlobalClass globalVariable = (GlobalClass) ticket_activity.getApplicationContext();
                globalVariable.setTicketsTicketGroup(JSON);
                 GQLog.dObj(this, "JSON=" + JSON);
                //printJSON(JSON);
                //globalVariable.setAnalyticsEventStats(JSON);  // save the JSON for future usage
                PopulateViewListView(JSON);
/*
                {"count": 385, "next": "http://beta.getqd.me/api/venue/reservations/all/?page=2", "previous": null, "next_page_number": 2, "previous_page_number": null, "page_number": 1, "num_pages": 20, "next_list": [2, 3, 4, 5, 6, 7, 8, 9, 10, 11], "previous_list": [], "results": [{"id": 2311, "name": "Milan GetQd Pecov", "num_of_people": 10, "details": "Corporate function", "sms_data": null, "reason": 1, "booked_by": null, "is_approved": false, "arrival_date": "2014-06-22T04:00:00Z", "location": null, "type": 49, "customer": {"id": 2, "first_name": "Milan GetQd", "last_name": "Pecov", "username": "Milan GetQd Pecov", "email": "mpecov@yahoo.ca", "gravatar": "https://gravatar.com/avatar/d54f492c90d72fc63b716859a11bcb89", "gravatar_small": "https://gravatar.com/avatar/d54f492c90d72fc63b716859a11bcb89", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2633, "name": "Lorriane Haston", "num_of_people": 3, "details": "Corporate function", "sms_data": null, "reason": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T13:33:07.077Z", "location": null, "type": 49, "customer": {"id": 167, "first_name": "Lorriane", "last_name": "Haston", "username": "Lorriane Haston", "email": "soksfa3@afasfd.caa", "gravatar": "https://gravatar.com/avatar/78750f518874cf6dfbe6e936911e405d", "gravatar_small": "https://gravatar.com/avatar/78750f518874cf6dfbe6e936911e405d", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2621, "name": "Cordell Roscoe", "num_of_people": 6, "details": "Corporate function", "sms_data": null, "reason": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T15:13:07.077Z", "location": null, "type": 49, "customer": {"id": 169, "first_name": "Cordell", "last_name": "Roscoe", "username": "Cordell Roscoe", "email": "soksfa5@afasfd.caa", "gravatar": "https://gravatar.com/avatar/1d245263d1555d7e5d7c61e6191cbb22", "gravatar_small": "https://gravatar.com/avatar/1d245263d1555d7e5d7c61e6191cbb22", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2619, "name": "Oswaldo Hartman", "num_of_people": 5, "details": "Stag/Stagette", "sms_data": null, "reason": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T13:23:07.077Z", "location": null, "type": 49, "customer": {"id": 166, "first_name": "Oswaldo", "last_name": "Hartman", "username": "Oswaldo Hartman", "email": "soksfa2@afasfd.caa", "gravatar": "https://gravatar.com/avatar/d3297ba5336906f5a1c4545f767332bb", "gravatar_small": "https://gravatar.com/avatar/d3297ba5336906f5a1c4545f767332bb", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2657, "name": "Oswaldo Hartman", "num_of_people": 3, "details": "Birthday", "sms_data": null, "reason": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T14:53:07.077Z", "location": null, "type": 49, "customer": {"id": 166, "first_name": "Oswaldo", "last_name": "Hartman", "username": "Oswaldo Hartman", "email": "soksfa2@afasfd.caa", "gravatar": "https://gravatar.com/avatar/d3297ba5336906f5a1c4545f767332bb", "gravatar_small": "https://gravatar.com/avatar/d3297ba5336906f5a1c4545f767332bb", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2614, "name": "Cordell Roscoe", "num_of_people": 6, "details": "Bottle service", "sms_data": null, "reason": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T14:13:07.077Z", "location": null, "type": 49, "customer": {"id": 169, "first_name": "Cordell", "last_name": "Roscoe", "username": "Cordell Roscoe", "email": "soksfa5@afasfd.caa", "gravatar": "https://gravatar.com/avatar/1d245263d1555d7e5d7c61e6191cbb22", "gravatar_small": "https://gravatar.com/avatar/1d245263d1555d7e5d7c61e6191cbb22", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2645, "name": "Dwayne Cessna", "num_of_people": 3, "details": "General", "sms_data": null, "reason": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T15:13:07.077Z", "location": null, "type": 49, "custom
*/

            } else if (code == 400) {
                 GQLog.dObj(this, "Getting 400  Error from the website");
                // Username or password false, display and an error

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ticket_activity);

                dlgAlert.setMessage("400 Erros Message");
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
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ticket_activity);

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
  {"count": 5, "next": null, "previous": null, "next_page_number": null, "previous_page_number": null, "page_number": 1, "num_pages": 1, "next_list": [], "previous_list": [],
  "results": [{"event": {"id": 49, "slug": "eat-sleep-rave-repea", "name": "EAT. SLEEP. RAVE. REPEAT - VOL 3 - ENTER THE ELECTRIC RODEO", "image": "https://getqd-beta.s3.amazonaws.com/media/images/events/cowboys-nightclub/images/11270601_975032489195971_3854382511811500659_o.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/images/events/cowboys-nightclub/thumbnails/11270601_975032489195971_3854382511811500659_o.jpg"}, "tt_stats": [{"ticket_type": {"id": 50, "name": "General"}, "num_tickets_sold": 4, "amount_sold": "8.20", "tickets_sold_breakdown": {"4": 4}, "amount_sold_breakdown": {"4": "8.199999999999999289457264240"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}], "link_stats": null},
              {"event": {"id": 41, "slug": "2015-countrylife-music-festival", "name": "2015 Countrylife Music Festival", "image": "https://getqd-beta.s3.amazonaws.com/media/events/Ranchmans Beta/10731201_652424284875371_1523470947567864279_n.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/events/Ranchmans Beta/thumbnails/10731201_652424284875371_1523470947567864279_n.jpg"}, "tt_stats": [{"ticket_type": {"id": 39, "name": "Reserved Section 1"}, "num_tickets_sold": 2, "amount_sold": "258.00", "tickets_sold_breakdown": {"1": 2}, "amount_sold_breakdown": {"1": "258.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}, {"ticket_type": {"id": 40, "name": "Reserved Section 2"}, "num_tickets_sold": 1, "amount_sold": "159.00", "tickets_sold_breakdown": {"1": 1}, "amount_sold_breakdown": {"1": "159.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}, {"ticket_type": {"id": 38, "name": "Gen. Admission Lawn"}, "num_tickets_sold": 9, "amount_sold": "981.00", "tickets_sold_breakdown": {"1": 6, "2": 1, "4": 2}, "amount_sold_breakdown": {"1": "654.00", "2": "109.00", "4": "218.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}, {"ticket_type": {"id": 41, "name": "Reserved Section 3"}, "num_tickets_sold": 17, "amount_sold": "3213.00", "tickets_sold_breakdown": {"1": 15, "5": 2}, "amount_sold_breakdown": {"1": "2835.00", "5": "378.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}, {"ticket_type": {"id": 42, "name": "VIP"}, "num_tickets_sold": 7, "amount_sold": "2093.00", "tickets_sold_breakdown": {"1": 5, "5": 2}, "amount_sold_breakdown": {"1": "1495.00", "5": "598.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}], "link_stats": {"views": 4, "visitors": 3}},
              {"event": {"id": 48, "slug": "alan-jackson-25th-anniversary-keepin-it-country-to", "name": "Alan Jackson 25th Anniversary Keepin It Country Tour", "image": "https://getqd-beta.s3.amazonaws.com/media/events/no-image.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/events/no-image.jpg"}, "tt_stats": [], "link_stats": {"views": 2, "visitors": 1}},
              {"event": {"id": 46, "slug": "the-only-pi-day-of-our-lives", "name": "The Only Pi Day of Our Lives", "image": "https://getqd-beta.s3.amazonaws.com/media/events/Country Life Music Festival/1.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/events/Country Life Music Festival/thumbnails/1.jpg"}, "tt_stats": [{"ticket_type": {"id": 47, "name": "General"}, "num_tickets_sold": 1, "amount_sold": "100.00", "tickets_sold_breakdown": {"2": 1}, "amount_sold_breakdown": {"2": "100.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}], "link_stats": null},
              {"event": {"id": 29, "slug": "yg-test", "name": "YG Test", "image": "https://getqd-beta.s3.amazonaws.com/media/events/Lucas Test Venue/10371338_734601526596778_114120445325209431_o.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/events/Lucas Test Venue/thumbnails/events/Lucas Test Venue/10371338_734601526596778_114120445325209431_o.jpg"}, "tt_stats": [{"ticket_type               */
                 GQLog.dObj(this, "Trying to print JSON.");
                JSONObject loginJSONObject = new JSONObject(JSON);
                 GQLog.dObj(this, "Parse count = " + loginJSONObject.getString("count"));
                 GQLog.dObj(this, "Parse next = " + loginJSONObject.getString("next"));
                 GQLog.dObj(this, "Parse previous = " + loginJSONObject.getString("previous"));
                 GQLog.dObj(this, "Parse next_page_number = " + loginJSONObject.getString("next_page_number"));
                 GQLog.dObj(this, "Parse prev_page_number = " + loginJSONObject.getString("previous_page_number"));
                 GQLog.dObj(this, "Parse page number = " + loginJSONObject.getString("page_number"));
                 GQLog.dObj(this, "Parse num_pages = " + loginJSONObject.getString("num_pages"));
                 GQLog.dObj(this, "Parse next_list = " + loginJSONObject.getString("next_list"));
                 GQLog.dObj(this, "Parse previous_list " + loginJSONObject.getString("previous_list"));
                JSONArray jArray = loginJSONObject.getJSONArray("results");
                int n;
                for (n = 0; n < jArray.length(); n = n + 1) {
                    /*
 "results": [{"event": {"id": 49, "slug": "eat-sleep-rave-repea", "name": "EAT. SLEEP. RAVE. REPEAT - VOL 3 - ENTER THE ELECTRIC RODEO", "image": "https://getqd-beta.s3.amazonaws.com/media/images/events/cowboys-nightclub/images/11270601_975032489195971_3854382511811500659_o.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/images/events/cowboys-nightclub/thumbnails/11270601_975032489195971_3854382511811500659_o.jpg"},
 "tt_stats": [{"ticket_type": {"id": 50, "name": "General"}, "num_tickets_sold": 4, "amount_sold": "8.20", "tickets_sold_breakdown": {"4": 4}, "amount_sold_breakdown": {"4": "8.199999999999999289457264240"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}], "link_stats": null}, {"event": {"id": 41, "slug": "2015-countrylife-music-festival", "name": "2015 Countrylife Music Festival", "image": "https://getqd-beta.s3.amazonaws.com/media/events/Ranchmans Beta/10731201_652424284875371_1523470947567864279_n.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/events/Ranchmans Beta/thumbnails/10731201_652424284875371_1523470947567864279_n.jpg"}, "tt_stats": [{"ticket_type": {"id": 39, "name": "Reserved Section 1"}, "num_tickets_sold": 2, "amount_sold": "258.00", "tickets_sold_breakdown": {"1": 2}, "amount_sold_breakdown": {"1": "258.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}, {"ticket_type": {"id": 40, "name": "Reserved Section 2"}, "num_tickets_sold": 1, "amount_sold": "159.00", "tickets_sold_breakdown": {"1": 1}, "amount_sold_breakdown": {"1": "159.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}, {"ticket_type": {"id": 38, "name": "Gen. Admission Lawn"}, "num_tickets_sold": 9, "amount_sold": "981.00", "tickets_sold_breakdown": {"1": 6, "2": 1, "4": 2}, "amount_sold_breakdown": {"1": "654.00", "2": "109.00", "4": "218.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}, {"ticket_type": {"id": 41, "name": "Reserved Section 3"}, "num_tickets_sold": 17, "amount_sold": "3213.00", "tickets_sold_breakdown": {"1": 15, "5": 2}, "amount_sold_breakdown": {"1": "2835.00", "5": "378.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}, {"ticket_type": {"id": 42, "name": "VIP"}, "num_tickets_sold": 7, "amount_sold": "2093.00", "tickets_sold_breakdown": {"1": 5, "5": 2}, "amount_sold_breakdown": {"1": "1495.00", "5": "598.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}], "link_stats": {"views": 4, "visitors": 3}}, {"event": {"id": 48, "slug": "alan-jackson-25th-anniversary-keepin-it-country-to", "name": "Alan Jackson 25th Anniversary Keepin It Country Tour", "image": "https://getqd-beta.s3.amazonaws.com/media/events/no-image.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/events/no-image.jpg"}, "tt_stats": [], "link_stats": {"views": 2, "visitors": 1}}, {"event": {"id": 46, "slug": "the-only-pi-day-of-our-lives", "name": "The Only Pi Day of Our Lives", "image": "https://getqd-beta.s3.amazonaws.com/media/events/Country Life Music Festival/1.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/events/Country Life Music Festival/thumbnails/1.jpg"}, "tt_stats": [{"ticket_type": {"id": 47, "name": "General"}, "num_tickets_sold": 1, "amount_sold": "100.00", "tickets_sold_breakdown": {"2": 1}, "amount_sold_breakdown": {"2": "100.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}], "link_stats": null}, {"event": {"id": 29, "slug": "yg-test", "name": "YG Test", "image": "https://getqd-beta.s3.amazonaws.com/media/events/Lucas Test Venue/10371338_734601526596778_114120445325209431_o.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/events/Lucas Test Venue/thumbnails/events/Lucas Test Venue/10371338_734601526596778_114120445325209431_o.jpg"}, "tt_stats": [{"ticket_type
                     */
                    JSONObject event = jArray.getJSONObject(n);

                     GQLog.dObj(this, "    list Item " + n + " Parse id " + Integer.toString(event.getInt("id")));
                    JSONObject type = event.getJSONObject("type");
                    JSONObject e = type.getJSONObject("event");
                     GQLog.dObj(this, "    list Item " + n + " Parse name " + e.getString("name"));
                     GQLog.dObj(this, "    list Item " + n + " Parse starts_on " + e.getString("starts_on"));


                    JSONArray tt = event.getJSONArray("tickets");
                     GQLog.dObj(this, "    list Item Number of tickets = " + tt.length());
                    int x = tt.length();
                    for (; x < tt.length(); x = x + 1) {
                        JSONObject tt_target = tt.getJSONObject(x);
                        JSONObject tType = tt_target.getJSONObject("ticket_type");
                         GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount id " + Integer.toString(tType.getInt("id")));
                         GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount name " + tType.getString("name"));
                         GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse number ticket sold " + Integer.toString(tt_target.getInt("num_tickets_sold")));
                         GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount sold " + tt_target.getString("amount_sold"));
                         GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount tickets_sold_breakdown " + tt_target.getString("tickets_sold_breakdown"));
                        JSONObject tSoldBreakdown = tt_target.getJSONObject("tickets_sold_breakdown");
                         GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount amount_sold_breakdown " + tt_target.getString("amount_sold_breakdown"));
                        JSONObject aSoldBreakdown = tt_target.getJSONObject("amount_sold_breakdown");
                         GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount num_tickets_sold_by_referral " + tt_target.getInt("num_tickets_sold_by_referral"));
                         GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount amount_sold_by_referral " + tt_target.getString("amount_sold_by_referral"));
                    }
                    /*
                    Cash = 1,
                    CreditCard = 2,
                    Interac = 3,
                    Complimentary = 4,
                    Free = 5,

                    "tickets_sold_breakdown": {"4": 4},
                    "amount_sold_breakdown": {"4": "8.199999999999999289457264240"},
                    "num_tickets_sold_by_referral": 0,
                     "amount_sold_by_referral": "0.00"}],
                    /*
                     GQLog.dObj(this, "    reservation " + n + " Parse booked_by " + venue.getString("booked_by"));
                     GQLog.dObj(this, "    reservation " + n + " Parse is_approved " + venue.getString("is_approved"));
                     GQLog.dObj(this, "    reservation " + n + " Parse arrival_date " + venue.getString("arrival_date"));
                     GQLog.dObj(this, "    reservation " + n + " Parse location " + venue.getString("location"));
                    JSONObject jo = venue.getJSONObject("customer");
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.id " + jo.getString("id"));
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.first_name " + jo.getString("first_name"));
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.last_name " + jo.getString("last_name"));
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.username"+ jo.getString("username"));
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.email " + jo.getString("email"));
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.gravatar " + jo.getString("gravatar"));
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.gravatar_small " + jo.getString("gravatar_small"));
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.phone_number " + jo.getString("phone_number"));
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.checkin_set " + venue.getString("checkin_set"));
                     GQLog.dObj(this, "    reservation " + n + " Parse customer.type_text " + venue.getString("type_text"));
*/
                     GQLog.dObj(this, "======================================================");
                }
            } catch (Exception e) {
                 GQLog.dObj(this, "Problem Printing" + e.toString());
            }
        }
    }
}

