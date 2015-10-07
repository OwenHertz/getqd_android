package com.app.upincode.getqd.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ViewFlipper;

import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.logging.GQLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class GQStatisticsActivity extends GQBaseActivity {
    ViewFlipper vf = null;
    int thePostion = 0;
    // stuff on the screen we are going to update
    ListView listview = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.activity_statistics);


        listview = (ListView) findViewById(com.app.upincode.getqd.R.id.SalesStatsListView);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GQLog.dObj(this, "Sales stats Clicked postion = " + position);
                Intent intent = new Intent(getApplicationContext(), GQStatisticsDisplayActivity.class);
                intent.putExtra("position", Integer.toString(position)); // start with the display statistics activity
                startActivity(intent);
            }

        });

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        //we are interested in populating listview with JSON. If don't have any go get some.
        String analyticsJSON = globalVariable.getAnalyticsEventStats();
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
            String URL = new String("api/user/analytics/event-stats/");

            //TODO fix
//            MyBaseModel mybm = new MyBaseModel(getApplicationContext());
//            mybm.execute("Get", URL, "4", "page_size", "20", "ordering", "starts_on");// Instantiate the custom HttpClient
        } else {
            PopulateViewListView(analyticsJSON);
        }
        // we go back to the staff upon pressing return.
        Button btyes = (Button) findViewById(com.app.upincode.getqd.R.id.StatisticsCancelButton);
        btyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GQLog.dObj(this, "Return to main activity: onclick yes try to call the activity");
                try {
                    Intent intent = new Intent(getApplicationContext(), GQMainActivity.class);
                    intent.putExtra("target", getString(com.app.upincode.getqd.R.string.Frag_Target_Staff)); // start with the Staff fragment
                    startActivity(intent);
                } catch (ClassCastException cce) {
                    GQLog.dObj(this, "CANCEL RESERVATION cast problem");
                }

            }
        });
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

    public static int target_v = 0;

    public void PopulateViewListView(String JSON) {

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

    public void PopulateListView(String[] eventName, String[] cash, String[] credit, String[] debit, String[] comp) {
        ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
        for (int n = 0; n < eventName.length; n = n + 1) {
            mNavItems.add(new NavItem(eventName[n], cash[n], credit[n], debit[n], comp[n]));
        }
        DrawerListAdapter adapter = new DrawerListAdapter(GQStatisticsActivity.this, mNavItems);
        listview.setAdapter(adapter);
    }

    class NavItem {
        String mTitle;
        String mCash;
        String mCredit;
        String mDebit;
        String mComp;

        public NavItem(String title, String cash, String credit, String debit, String comp) {
            mTitle = title;
            mCash = cash;
            mCredit = credit;
            mDebit = debit;
            mComp = comp;
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
                view = inflater.inflate(com.app.upincode.getqd.R.layout.sales_stats_item, null);
            } else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(com.app.upincode.getqd.R.id.eventTitle);
            TextView cashView = (TextView) view.findViewById(com.app.upincode.getqd.R.id.cash);
            TextView creditView = (TextView) view.findViewById(com.app.upincode.getqd.R.id.credit);
            TextView debitView = (TextView) view.findViewById(com.app.upincode.getqd.R.id.debit);
            TextView compView = (TextView) view.findViewById(com.app.upincode.getqd.R.id.comp);

            titleView.setText(mNavItems.get(position).mTitle);
            cashView.setText(mNavItems.get(position).mCash);
            creditView.setText(mNavItems.get(position).mCredit);
            debitView.setText(mNavItems.get(position).mDebit);
            compView.setText(mNavItems.get(position).mComp);
            return view;
        }
    }


    //TODO fix
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
//                final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
//                globalVariable.setAnalyticsEventStats(JSON);  // save the JSON for future usage
//                PopulateViewListView(JSON);
///*
//                {"count": 385, "next": "http://beta.getqd.me/api/venue/reservations/all/?page=2", "previous": null, "next_page_number": 2, "previous_page_number": null, "page_number": 1, "num_pages": 20, "next_list": [2, 3, 4, 5, 6, 7, 8, 9, 10, 11], "previous_list": [], "results": [{"id": 2311, "name": "Milan GetQd Pecov", "num_of_people": 10, "details": "Corporate function", "sms_data": null, "spinnerReasons": 1, "booked_by": null, "is_approved": false, "arrival_date": "2014-06-22T04:00:00Z", "location": null, "type": 49, "customer": {"id": 2, "first_name": "Milan GetQd", "last_name": "Pecov", "username": "Milan GetQd Pecov", "email": "mpecov@yahoo.ca", "gravatar": "https://gravatar.com/avatar/d54f492c90d72fc63b716859a11bcb89", "gravatar_small": "https://gravatar.com/avatar/d54f492c90d72fc63b716859a11bcb89", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2633, "name": "Lorriane Haston", "num_of_people": 3, "details": "Corporate function", "sms_data": null, "spinnerReasons": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T13:33:07.077Z", "location": null, "type": 49, "customer": {"id": 167, "first_name": "Lorriane", "last_name": "Haston", "username": "Lorriane Haston", "email": "soksfa3@afasfd.caa", "gravatar": "https://gravatar.com/avatar/78750f518874cf6dfbe6e936911e405d", "gravatar_small": "https://gravatar.com/avatar/78750f518874cf6dfbe6e936911e405d", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2621, "name": "Cordell Roscoe", "num_of_people": 6, "details": "Corporate function", "sms_data": null, "spinnerReasons": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T15:13:07.077Z", "location": null, "type": 49, "customer": {"id": 169, "first_name": "Cordell", "last_name": "Roscoe", "username": "Cordell Roscoe", "email": "soksfa5@afasfd.caa", "gravatar": "https://gravatar.com/avatar/1d245263d1555d7e5d7c61e6191cbb22", "gravatar_small": "https://gravatar.com/avatar/1d245263d1555d7e5d7c61e6191cbb22", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2619, "name": "Oswaldo Hartman", "num_of_people": 5, "details": "Stag/Stagette", "sms_data": null, "spinnerReasons": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T13:23:07.077Z", "location": null, "type": 49, "customer": {"id": 166, "first_name": "Oswaldo", "last_name": "Hartman", "username": "Oswaldo Hartman", "email": "soksfa2@afasfd.caa", "gravatar": "https://gravatar.com/avatar/d3297ba5336906f5a1c4545f767332bb", "gravatar_small": "https://gravatar.com/avatar/d3297ba5336906f5a1c4545f767332bb", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2657, "name": "Oswaldo Hartman", "num_of_people": 3, "details": "Birthday", "sms_data": null, "spinnerReasons": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T14:53:07.077Z", "location": null, "type": 49, "customer": {"id": 166, "first_name": "Oswaldo", "last_name": "Hartman", "username": "Oswaldo Hartman", "email": "soksfa2@afasfd.caa", "gravatar": "https://gravatar.com/avatar/d3297ba5336906f5a1c4545f767332bb", "gravatar_small": "https://gravatar.com/avatar/d3297ba5336906f5a1c4545f767332bb", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2614, "name": "Cordell Roscoe", "num_of_people": 6, "details": "Bottle service", "sms_data": null, "spinnerReasons": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T14:13:07.077Z", "location": null, "type": 49, "customer": {"id": 169, "first_name": "Cordell", "last_name": "Roscoe", "username": "Cordell Roscoe", "email": "soksfa5@afasfd.caa", "gravatar": "https://gravatar.com/avatar/1d245263d1555d7e5d7c61e6191cbb22", "gravatar_small": "https://gravatar.com/avatar/1d245263d1555d7e5d7c61e6191cbb22", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2645, "name": "Dwayne Cessna", "num_of_people": 3, "details": "General", "sms_data": null, "spinnerReasons": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T15:13:07.077Z", "location": null, "type": 49, "custom
//*/
//
//            } else if (code == 400) {
//                 GQLog.dObj(this, "Getting 400  Error from the website");
//                // Username or password false, display and an error
//
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQStatisticsActivity.this);
//
//                dlgAlert.setMessage("400 Erros Message");
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
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQStatisticsActivity.this);
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
//            try {
//                /*
//  {"count": 5, "next": null, "previous": null, "next_page_number": null, "previous_page_number": null, "page_number": 1, "num_pages": 1, "next_list": [], "previous_list": [],
//  "results": [{"event": {"id": 49, "slug": "eat-sleep-rave-repea", "name": "EAT. SLEEP. RAVE. REPEAT - VOL 3 - ENTER THE ELECTRIC RODEO", "image": "https://getqd-beta.s3.amazonaws.com/media/images/events/cowboys-nightclub/images/11270601_975032489195971_3854382511811500659_o.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/images/events/cowboys-nightclub/thumbnails/11270601_975032489195971_3854382511811500659_o.jpg"}, "tt_stats": [{"ticket_type": {"id": 50, "name": "General"}, "num_tickets_sold": 4, "amount_sold": "8.20", "tickets_sold_breakdown": {"4": 4}, "amount_sold_breakdown": {"4": "8.199999999999999289457264240"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}], "link_stats": null},
//              {"event": {"id": 41, "slug": "2015-countrylife-music-festival", "name": "2015 Countrylife Music Festival", "image": "https://getqd-beta.s3.amazonaws.com/media/events/Ranchmans Beta/10731201_652424284875371_1523470947567864279_n.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/events/Ranchmans Beta/thumbnails/10731201_652424284875371_1523470947567864279_n.jpg"}, "tt_stats": [{"ticket_type": {"id": 39, "name": "Reserved Section 1"}, "num_tickets_sold": 2, "amount_sold": "258.00", "tickets_sold_breakdown": {"1": 2}, "amount_sold_breakdown": {"1": "258.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}, {"ticket_type": {"id": 40, "name": "Reserved Section 2"}, "num_tickets_sold": 1, "amount_sold": "159.00", "tickets_sold_breakdown": {"1": 1}, "amount_sold_breakdown": {"1": "159.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}, {"ticket_type": {"id": 38, "name": "Gen. Admission Lawn"}, "num_tickets_sold": 9, "amount_sold": "981.00", "tickets_sold_breakdown": {"1": 6, "2": 1, "4": 2}, "amount_sold_breakdown": {"1": "654.00", "2": "109.00", "4": "218.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}, {"ticket_type": {"id": 41, "name": "Reserved Section 3"}, "num_tickets_sold": 17, "amount_sold": "3213.00", "tickets_sold_breakdown": {"1": 15, "5": 2}, "amount_sold_breakdown": {"1": "2835.00", "5": "378.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}, {"ticket_type": {"id": 42, "name": "VIP"}, "num_tickets_sold": 7, "amount_sold": "2093.00", "tickets_sold_breakdown": {"1": 5, "5": 2}, "amount_sold_breakdown": {"1": "1495.00", "5": "598.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}], "link_stats": {"views": 4, "visitors": 3}},
//              {"event": {"id": 48, "slug": "alan-jackson-25th-anniversary-keepin-it-country-to", "name": "Alan Jackson 25th Anniversary Keepin It Country Tour", "image": "https://getqd-beta.s3.amazonaws.com/media/events/no-image.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/events/no-image.jpg"}, "tt_stats": [], "link_stats": {"views": 2, "visitors": 1}},
//              {"event": {"id": 46, "slug": "the-only-pi-day-of-our-lives", "name": "The Only Pi Day of Our Lives", "image": "https://getqd-beta.s3.amazonaws.com/media/events/Country Life Music Festival/1.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/events/Country Life Music Festival/thumbnails/1.jpg"}, "tt_stats": [{"ticket_type": {"id": 47, "name": "General"}, "num_tickets_sold": 1, "amount_sold": "100.00", "tickets_sold_breakdown": {"2": 1}, "amount_sold_breakdown": {"2": "100.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}], "link_stats": null},
//              {"event": {"id": 29, "slug": "yg-test", "name": "YG Test", "image": "https://getqd-beta.s3.amazonaws.com/media/events/Lucas Test Venue/10371338_734601526596778_114120445325209431_o.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/events/Lucas Test Venue/thumbnails/events/Lucas Test Venue/10371338_734601526596778_114120445325209431_o.jpg"}, "tt_stats": [{"ticket_type               */
//                 GQLog.dObj(this, "Trying to print JSON.");
//                JSONObject loginJSONObject = new JSONObject(JSON);
//                 GQLog.dObj(this, "Parse count = " + loginJSONObject.getString("count"));
//                 GQLog.dObj(this, "Parse next = " + loginJSONObject.getString("next"));
//                 GQLog.dObj(this, "Parse previous = " + loginJSONObject.getString("previous"));
//                 GQLog.dObj(this, "Parse next_page_number = " + loginJSONObject.getString("next_page_number"));
//                 GQLog.dObj(this, "Parse prev_page_number = " + loginJSONObject.getString("previous_page_number"));
//                 GQLog.dObj(this, "Parse page number = " + loginJSONObject.getString("page_number"));
//                 GQLog.dObj(this, "Parse num_pages = " + loginJSONObject.getString("num_pages"));
//                 GQLog.dObj(this, "Parse next_list = " + loginJSONObject.getString("next_list"));
//                 GQLog.dObj(this, "Parse previous_list " + loginJSONObject.getString("previous_list"));
//                JSONArray jArray = loginJSONObject.getJSONArray("results");
//                int n;
//                for (n = 0; n < jArray.length(); n = n + 1) {
//                    /*
// "results": [{"event": {"id": 49, "slug": "eat-sleep-rave-repea", "name": "EAT. SLEEP. RAVE. REPEAT - VOL 3 - ENTER THE ELECTRIC RODEO", "image": "https://getqd-beta.s3.amazonaws.com/media/images/events/cowboys-nightclub/images/11270601_975032489195971_3854382511811500659_o.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/images/events/cowboys-nightclub/thumbnails/11270601_975032489195971_3854382511811500659_o.jpg"},
// "tt_stats": [{"ticket_type": {"id": 50, "name": "General"}, "num_tickets_sold": 4, "amount_sold": "8.20", "tickets_sold_breakdown": {"4": 4}, "amount_sold_breakdown": {"4": "8.199999999999999289457264240"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}], "link_stats": null}, {"event": {"id": 41, "slug": "2015-countrylife-music-festival", "name": "2015 Countrylife Music Festival", "image": "https://getqd-beta.s3.amazonaws.com/media/events/Ranchmans Beta/10731201_652424284875371_1523470947567864279_n.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/events/Ranchmans Beta/thumbnails/10731201_652424284875371_1523470947567864279_n.jpg"}, "tt_stats": [{"ticket_type": {"id": 39, "name": "Reserved Section 1"}, "num_tickets_sold": 2, "amount_sold": "258.00", "tickets_sold_breakdown": {"1": 2}, "amount_sold_breakdown": {"1": "258.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}, {"ticket_type": {"id": 40, "name": "Reserved Section 2"}, "num_tickets_sold": 1, "amount_sold": "159.00", "tickets_sold_breakdown": {"1": 1}, "amount_sold_breakdown": {"1": "159.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}, {"ticket_type": {"id": 38, "name": "Gen. Admission Lawn"}, "num_tickets_sold": 9, "amount_sold": "981.00", "tickets_sold_breakdown": {"1": 6, "2": 1, "4": 2}, "amount_sold_breakdown": {"1": "654.00", "2": "109.00", "4": "218.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}, {"ticket_type": {"id": 41, "name": "Reserved Section 3"}, "num_tickets_sold": 17, "amount_sold": "3213.00", "tickets_sold_breakdown": {"1": 15, "5": 2}, "amount_sold_breakdown": {"1": "2835.00", "5": "378.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}, {"ticket_type": {"id": 42, "name": "VIP"}, "num_tickets_sold": 7, "amount_sold": "2093.00", "tickets_sold_breakdown": {"1": 5, "5": 2}, "amount_sold_breakdown": {"1": "1495.00", "5": "598.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}], "link_stats": {"views": 4, "visitors": 3}}, {"event": {"id": 48, "slug": "alan-jackson-25th-anniversary-keepin-it-country-to", "name": "Alan Jackson 25th Anniversary Keepin It Country Tour", "image": "https://getqd-beta.s3.amazonaws.com/media/events/no-image.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/events/no-image.jpg"}, "tt_stats": [], "link_stats": {"views": 2, "visitors": 1}}, {"event": {"id": 46, "slug": "the-only-pi-day-of-our-lives", "name": "The Only Pi Day of Our Lives", "image": "https://getqd-beta.s3.amazonaws.com/media/events/Country Life Music Festival/1.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/events/Country Life Music Festival/thumbnails/1.jpg"}, "tt_stats": [{"ticket_type": {"id": 47, "name": "General"}, "num_tickets_sold": 1, "amount_sold": "100.00", "tickets_sold_breakdown": {"2": 1}, "amount_sold_breakdown": {"2": "100.00"}, "num_tickets_sold_by_referral": 0, "amount_sold_by_referral": "0.00"}], "link_stats": null}, {"event": {"id": 29, "slug": "yg-test", "name": "YG Test", "image": "https://getqd-beta.s3.amazonaws.com/media/events/Lucas Test Venue/10371338_734601526596778_114120445325209431_o.jpg", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/events/Lucas Test Venue/thumbnails/events/Lucas Test Venue/10371338_734601526596778_114120445325209431_o.jpg"}, "tt_stats": [{"ticket_type
//                     */
//                    JSONObject venue = jArray.getJSONObject(n);
//                    JSONObject event = venue.getJSONObject("event");
//
//                     GQLog.dObj(this, "    list Item " + n + " Parse id " + Integer.toString(event.getInt("id")));
//                     GQLog.dObj(this, "    list Item " + n + " Parse name " + event.getString("name"));
//                     GQLog.dObj(this, "    list Item " + n + " Parse slug " + event.getString("slug"));
//                     GQLog.dObj(this, "    list Item " + n + " Parse image " + event.getString("image"));
//                     GQLog.dObj(this, "    list Item " + n + " Parse thumbnail " + event.getString("thumbnail"));
//
//                    JSONArray tt = venue.getJSONArray("tt_stats");
//                    int x;
//                    for (x = 0; x < tt.length(); x = x + 1) {
//                        JSONObject tt_target = tt.getJSONObject(x);
//                        JSONObject tType = tt_target.getJSONObject("ticket_type");
//                         GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount id " + Integer.toString(tType.getInt("id")));
//                         GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount name " + tType.getString("name"));
//                         GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse number ticket sold " + Integer.toString(tt_target.getInt("num_tickets_sold")));
//                         GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount sold " + tt_target.getString("amount_sold"));
//                         GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount tickets_sold_breakdown " + tt_target.getString("tickets_sold_breakdown"));
//                        JSONObject tSoldBreakdown = tt_target.getJSONObject("tickets_sold_breakdown");
//                         GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount amount_sold_breakdown " + tt_target.getString("amount_sold_breakdown"));
//                        JSONObject aSoldBreakdown = tt_target.getJSONObject("amount_sold_breakdown");
//                         GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount num_tickets_sold_by_referral " + tt_target.getInt("num_tickets_sold_by_referral"));
//                         GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount amount_sold_by_referral " + tt_target.getString("amount_sold_by_referral"));
//                    }
//                    /*
//                    Cash = 1,
//                    CreditCard = 2,
//                    Interac = 3,
//                    Complimentary = 4,
//                    Free = 5,
//
//                    "tickets_sold_breakdown": {"4": 4},
//                    "amount_sold_breakdown": {"4": "8.199999999999999289457264240"},
//                    "num_tickets_sold_by_referral": 0,
//                     "amount_sold_by_referral": "0.00"}],
//                    /*
//                     GQLog.dObj(this, "    reservation " + n + " Parse booked_by " + venue.getString("booked_by"));
//                     GQLog.dObj(this, "    reservation " + n + " Parse is_approved " + venue.getString("is_approved"));
//                     GQLog.dObj(this, "    reservation " + n + " Parse arrival_date " + venue.getString("arrival_date"));
//                     GQLog.dObj(this, "    reservation " + n + " Parse location " + venue.getString("location"));
//                    JSONObject jo = venue.getJSONObject("customer");
//                     GQLog.dObj(this, "    reservation " + n + " Parse customer.id " + jo.getString("id"));
//                     GQLog.dObj(this, "    reservation " + n + " Parse customer.first_name " + jo.getString("first_name"));
//                     GQLog.dObj(this, "    reservation " + n + " Parse customer.last_name " + jo.getString("last_name"));
//                     GQLog.dObj(this, "    reservation " + n + " Parse customer.username"+ jo.getString("username"));
//                     GQLog.dObj(this, "    reservation " + n + " Parse customer.email " + jo.getString("email"));
//                     GQLog.dObj(this, "    reservation " + n + " Parse customer.gravatar " + jo.getString("gravatar"));
//                     GQLog.dObj(this, "    reservation " + n + " Parse customer.gravatar_small " + jo.getString("gravatar_small"));
//                     GQLog.dObj(this, "    reservation " + n + " Parse customer.phone_number " + jo.getString("phone_number"));
//                     GQLog.dObj(this, "    reservation " + n + " Parse customer.checkin_set " + venue.getString("checkin_set"));
//                     GQLog.dObj(this, "    reservation " + n + " Parse customer.type_text " + venue.getString("type_text"));
//*/
//                     GQLog.dObj(this, "======================================================");
//                }
//            } catch (Exception e) {
//                 GQLog.dObj(this, "Problem Printing" + e.toString());
//            }
//        }
//    }
}
