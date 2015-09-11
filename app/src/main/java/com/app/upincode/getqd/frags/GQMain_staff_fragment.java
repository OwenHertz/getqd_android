package com.app.upincode.getqd.frags;


/**
 * Created by herbert on 7/23/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.upincode.getqd.GQMainActivity;
import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.models.GQBaseModel;
//import com.app.upincode.getqd.frags.dummy.DummyContent;
import com.app.upincode.getqd.utils.ImageDownloaderTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GQMain_staff_fragment extends Fragment {
    private OnButtonClickedStaffBookguest mlistenerStaffBookguest;
    private OnButtonClickedStaffCheckin mlistenerStaffCheckin;
    private OnButtonClickedStaffMyreso mlistenerStaffMyreso;
    private OnButtonClickedStaffSales mlistenerStaffSales;
    private OnButtonClickedStaffStats mlistenerStaffStates;
    Activity staff_activity = null;
    boolean smallMode = false;
    boolean populateUsingJSONHere = false;
    ListView listview = null;

    boolean debug = true;

    public static GQMain_staff_fragment newInstance() {
        GQMain_staff_fragment fragment = new GQMain_staff_fragment();
        return fragment;
    }

    public GQMain_staff_fragment() {

    }


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        staff_activity = activity;
        if (smallMode == false) {
            ((GQMainActivity) activity).onSectionAttached(5);
        } else {
            ((GQMainActivity) activity).onSectionAttached(2);
        }

        //String URL_Login = new String("api/users/me/");  This gets Me Information
        String URL_Login = new String("api/user/venues/");
        GlobalClass globalVariable = (GlobalClass) activity.getApplicationContext();
        String JSON = globalVariable.getUserVenuesJSON();  // get from earlier reference
        if (JSON == null) {
             GQLog.dObj(this, "Going to the website cause I got not JSON.");
            if (staff_activity != null) {
                GQMain_staff_fragment.MyBaseModel mybm = new GQMain_staff_fragment.MyBaseModel(((GQMainActivity) staff_activity).getApplicationContext());
                mybm.execute("Get", URL_Login, "0");// Instantiate the custom HttpClient
            } else {
                 GQLog.dObj(this, "No Activity So no base model created");
            }
        } else {  // I got the JSON earlier no sense going back to the website
             GQLog.dObj(this, "Not Going to the website cause I got some JSON.");
            PopulateListView(JSON);
            populateUsingJSONHere = true;
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gqmain_fragment_staff, container, false);

        getActivity().setTitle("Reps");

        Button GetStats = (Button) rootView.findViewById(R.id.buttonStaffGetStats);
        GetStats.setOnClickListener(StatsClickListener);
        Button Sales = (Button) rootView.findViewById(R.id.buttonStaffSellTickets);
        Sales.setOnClickListener(SalesClickListener);

        //getActivity().setTitle("Reps");

        listview = (ListView) rootView.findViewById(R.id.listViewStaff);
        if (populateUsingJSONHere == true) {
            GlobalClass globalVariable = (GlobalClass) staff_activity.getApplicationContext();
            String JSON = globalVariable.getUserVenuesJSON();  // get from earlier reference
            PopulateListView(JSON);
        }
        //PopulateListView();

        return rootView;
    }

    public void PopulateListView(String JSON) {
        try {
            JSONArray jArray = new JSONArray(JSON);
            int n;
             GQLog.dObj(this, "Populating listview");
            String bma[] = new String[jArray.length()];
            String vn[] = new String[jArray.length()];
            for (n = 0; n < jArray.length(); n = n + 1) {
                JSONObject venue = jArray.getJSONObject(n);
                vn[n] = venue.getString("name");
                bma[n] = venue.getString("avatar");
            }
            PopulateListView(vn, bma);
        } catch (Exception e) {
             GQLog.dObj(this, "PopulateListView(JSON) exception=" + e.toString());
        }
    }

    public void PopulateListView(String sa[], String bma[]) {

        ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
        for (int n = 0; n < sa.length; n = n + 1) {
            mNavItems.add(new NavItem(bma[n], sa[n], R.drawable.circle_with_calendar, R.drawable.circle_with_hamburger, R.drawable.circle_with_check));
        }

        DrawerListAdapter adapter = new DrawerListAdapter(getActivity(), mNavItems);
        listview.setAdapter(adapter);
        listview.setBackgroundColor(Color.parseColor("#dcdcdc"));
    }

    class NavItem {
        String mURL;
        String mTitle;
        String mSubtitle;
        int mIconbg;
        int mIconmr;
        int mIconci;


        public NavItem(String URL, String title, int iconbg, int iconmr, int iconci) {
            mURL = URL;
            mTitle = title;
            //BmSubtitle = subtitle;
            mIconbg = iconbg;
            mIconmr = iconmr;
            mIconci = iconci;
             GQLog.dObj(this, "URL = " + URL);
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
                view = inflater.inflate(R.layout.drawer_getqd_staff_item, null);
            } else {
                view = convertView;
            }
            ImageView logoView = (ImageView) view.findViewById(R.id.logo);
            TextView titleView = (TextView) view.findViewById(R.id.title);
            //TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
            ImageButton iconViewbg = (ImageButton) view.findViewById(R.id.iconbg);
            ImageButton iconViewmr = (ImageButton) view.findViewById(R.id.iconmr);
            ImageButton iconViewci = (ImageButton) view.findViewById(R.id.iconci);

            if (logoView != null) {
                new ImageDownloaderTask(logoView).execute(mNavItems.get(position).mURL);
            }
            titleView.setText(mNavItems.get(position).mTitle);
            //subtitleView.setText( mNavItems.get(position).mSubtitle );
            iconViewbg.setImageResource(mNavItems.get(position).mIconbg);
            iconViewmr.setImageResource(mNavItems.get(position).mIconmr);
            iconViewci.setImageResource(mNavItems.get(position).mIconci);
            iconViewbg.setOnClickListener(BookGuestClickListener);
            iconViewmr.setOnClickListener(MyResosClickListener);
            iconViewci.setOnClickListener(CheckinClickListener);
            return view;
        }
    }

    private View.OnClickListener BookGuestClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);
             GQLog.dObj(this, "bookguestclicklistener +position=" + position);
            try {
                ((OnButtonClickedStaffBookguest) staff_activity).OnButtonClickedStaffBookguest(position);
            } catch (ClassCastException cce) {
                 GQLog.dObj(this, "BookGuestClickListener + cast problem");
            }

        }

    };
    private View.OnClickListener CheckinClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            //Log.d("checkinclicklistener","clicked");
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);
             GQLog.dObj(this, "CheckInclicklistener + position=" + position);
            try {
                ((OnButtonClickedStaffCheckin) staff_activity).OnButtonClickedStaffCheckin(position);
            } catch (ClassCastException cce) {
                 GQLog.dObj(this, "clickClickListener  +  cast problem");
            }
        }

    };
    private View.OnClickListener MyResosClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            // GQLog.d("MyResosclicklistener","clicked");
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);
             GQLog.dObj(this, "MyResoclicklistener + position=" + position);
            try {
                ((OnButtonClickedStaffMyreso) staff_activity).OnButtonClickedStaffMyreso(position);
            } catch (ClassCastException cce) {
                 GQLog.dObj(this, "staffBookGuestClickListener + cast problem");
            }
        }

    };
    private View.OnClickListener SalesClickListener = new View.OnClickListener() {

        public void onClick(View v) {
             GQLog.dObj(this, "Saleslistener+ Clicked");
            try {
                ((OnButtonClickedStaffSales) staff_activity).OnButtonClickedStaffSales();
            } catch (ClassCastException cce) {
                 GQLog.dObj(this, "StafftClickListener+cast problem");
            }
        }

    };
    private View.OnClickListener StatsClickListener = new View.OnClickListener() {

        public void onClick(View v) {
             GQLog.dObj(this, "Statslistener+Clicked");
            try {
                ((OnButtonClickedStaffStats) staff_activity).OnButtonClickedStaffStats();
            } catch (ClassCastException cce) {
                 GQLog.dObj(this, "statsClickListener + cast problem");
            }
        }

    };

    public interface OnButtonClickedStaffBookguest {
        public void OnButtonClickedStaffBookguest(int position);
    }

    public interface OnButtonClickedStaffCheckin {
        public void OnButtonClickedStaffCheckin(int position);
    }

    public interface OnButtonClickedStaffMyreso {
        public void OnButtonClickedStaffMyreso(int position);
    }

    public interface OnButtonClickedStaffSales {
        public void OnButtonClickedStaffSales();
    }

    public interface OnButtonClickedStaffStats {
        public void OnButtonClickedStaffStats();
    }

    public class MyBaseModel extends GQBaseModel {
        Context myContext = null;

        public MyBaseModel(Context context) {
            super(context);
            myContext = context;
        }


        public void updateView(Boolean retCode, int code, String JSON) {

            if (retCode == false) {
                 GQLog.dObj(this, "Unable to Access the Website");
                return;
            }

            if (code == 200) {
                try {
                    JSONArray jArray = new JSONArray(JSON);
                    int n;
                    for (n = 0; n < jArray.length(); n = n + 1) {
                        JSONObject venue = jArray.getJSONObject(n);
                         GQLog.dObj(this, "=======> Venue ID = " + Integer.toString(venue.getInt("id")) + " === Venue Name = " + venue.getString("name"));
                    }
                } catch (Exception e) {
                     GQLog.dObj(this, "Problems parsing JSON");
                }
                /*  Looks Like
                 JSON=[{"id": 15, "created": "2014-02-14T04:38:29.833Z", "updated": "2015-07-28T17:36:21.914Z", "slug": "ranchmans", "street_name": "39450
                 Bermont Road ", "city": "Punta Gorda", "province": "Fl", "postal_code": "33982", "position": "26.91040080805868,-81.95891827475594", "phone": 42,
                 "twilio_number": null, "category": 4, "avatar": "https://getqd-beta.s3.amazonaws.com/media/images/venues/ranchmans/avatars/countrylogo.PNG",
                 "background": "https://getqd-beta.s3.amazonaws.com/media/venues/backgrounds/countryavatar.PNG", "name": "Country Life ", "twitter":
                 "http://twitter.com/countrylifemusicflorida", "facebook": "http://facebook.com/countrylifemusicflorida",
                 "web_address": "http://florida.countrylifemusicfestival.com/", "description": "Countrylife Music Festival is committed to
                 creating a safe, comfortable and memorable experience for our guests. In order to provide an environment where families can
                  enjoy the festival, we have instituted a CODE OF CONDUCT for all areas;\r\n", "info": "Displacsfdsfasdfs",
                  "email": "RKetterman@FloridaTracksAndTrails.com", "advanced_settings": {"thursday_staff_bookings_limit": "3",
                  "tuesday_staff_bookings_limit": "3", "wednesday_web_bookings_limit": "5", "saturday_web_bookings_limit": "5",
                   "sunday_end": "18:45", "thursday_start": "18:45", "intervals": {"monday": [{"start": "18:45", "end": "18:45"}],
                   "tuesday": [{"start": "18:45", "end": "18:45"}], "friday": [{"start": "18:45", "end": "18:45"}],
                   "wednesday": [{"start": "18:45", "end": "18:45"}], "thursday": [{"start": "18:45", "end": "18:45"}],
                   "sunday": [{"start": "18:45", "end": "18:45"}], "saturday": [{"start": "18:45", "end": "18:45"}]},
                    "wednesday_start": "18:45", "sunday_web_bookings_limit": "5", "monday_staff_bookings_limit": "2",
                    "sunday_start": "18:45", "thursday_end": "18:45", "wednesday_end": "18:45", "tuesday_start": "18:45",
                     "friday_end": "18:45", "csrfmiddlewaretoken": "aTwxAxe0w1PRSDkpoIxb5jz4SffiAVdq", "thursday_web_bookings_limit": "5",
                     "monday_closed": "on", "monday_web_bookings_limit": "5", "saturday_end": "18:45", "widget_hours": "3",
                     "sunday_staff_bookings_limit": "2", "friday_staff_bookings_limit": "2", "saturday_staff_bookings_limit": "3", "monday_start": "18:45",
                     "wednesday_staff_bookings_limit": "2", "saturday_start": "18:45", "is_private": "on", "friday_web_bookings_limit": "5",
                     "staff_hours": "3", "tuesday_web_bookings_limit": "5", "friday_start": "18:45", "tuesday_end": "18:45", "monday_end": "18:45"},
                     "gst": "0.00", "date_change_time": "03:00:00", "thumbnail": "https://getqd-beta.s3.amazonaws.com/media/images/venues/ranchmans/avatars/countrylogo.PNG",
                     "position_coordinates": {"latitude": 26.91040080805868, "longitude": -81.95891827475594}, "schedule_enabled": {"monday": [{"start": "18:45", "end": "18:45"}], "tuesday": [{"start": "18:45", "end": "18:45"}], "friday": [{"start": "18:45", "end": "18:45"}],
                      "wednesday": [{"start": "18:45", "end": "18:45"}], "thursday": [{"start": "18:45", "end": "18:45"}], "sunday": [{"start": "18:45", "end": "18:45"}], "saturday": [{"start": "18:45", "end": "18:45"}]}}, {"id": 12, "created": "2014-02-10T06:50:29.425Z", "updated": "2015-07-28T17:36:45.165Z", "slug": "cowboys-nightclub", "street_name": "Olympic Ace",
                      "city": "Calgary", "province": "AB", "postal_code": "T2Y2D2", "position": "51.03944234466235,-114.05320885075679", "phone": 43, "twilio_number": null,
                      "category": 1, "avatar": "https://getqd-beta.s3.amazonaws.com/media/images/venues/cowboys-nightclub/avatars/cowboys-logo.png", "background": "https://getqd-beta.s3.amazonaws.com/media/images/venues/cowboys-nightclub/backgrounds/10426327_891793647551812_1979022967331963229_n.jpg", "name": "Cowboys ", "twitter": "", "facebook": "https://www.facebook.com/CowboysCalgary", "web_address": "",
                      "description": "\"The Most Fun You Can Have With Your Boots On!\"", "info": "", "email": "COWBOYS@COWBOYS.COM", "advanced_settings": {"thursday_staff_bookings_limit": "0", "tuesday_staff_bookings_limit": "0", "saturday_web_bookings_limit": "0", "sunday_end": "2:00",
                      "thursday_start": "21:00", "intervals": {"monda

                 */
                try {
                    GlobalClass globalVariable = (GlobalClass) myContext.getApplicationContext();
                    globalVariable.setUserVenuesJSON(JSON);  // save in global space
                    //Log.d("mainactivity" , "JSON="+JSON);

                    PopulateListView(JSON);
/*
                    JSONArray jArray = new JSONArray(JSON);
                    int n;

                    String bma[] = new String[jArray.length()];
                    String vn[] = new String[jArray.length()];
                    for (n = 0; n < jArray.length(); n = n + 1) {
                        JSONObject venue = jArray.getJSONObject(n);
                        vn[n] = venue.getString("name");
                        bma[n] = venue.getString("avatar");
                    }
                    PopulateListView(vn,bma);
                    */
                } catch (Exception e) {
                     GQLog.dObj(this, "Problem parsing Json" + e.toString());
                }
            } else if (code == 400) {
                 GQLog.dObj(this, "Getting 400  Error from the website");
                // Username or password false, display and an error

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(staff_activity);

                dlgAlert.setMessage(JSON);
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
                GQLog.d("GQMain_staff_fragment", "Getting Error From Website =  " + code);


                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(staff_activity);

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
    }


}
