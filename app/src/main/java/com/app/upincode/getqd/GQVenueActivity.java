package com.app.upincode.getqd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.models.GQBaseModel;


public class GQVenueActivity extends ActionBarActivity {
    TextView tvName = null;
    TextView tvEmail = null;
    Button btnChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.activity_venue);

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();


        btnChat = (Button) findViewById(com.app.upincode.getqd.R.id.Chats);
        String styledText = "<big> <font color='#008000'>"
                + "Chats" + "</font> </big>" + "<br />"
                + "<small>" + "0" + "</small>";
        //btnChat.setText(Html.fromHtml(styledText));
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 GQLog.dObj(this, "chat button pressed");

                //SelectDate();   // we need to put up the date dialog. So user can select the date
            }
        });
        // we go back to the staff upon pressing return.
        Button btyes = (Button) findViewById(com.app.upincode.getqd.R.id.VenueCancelButton);
        btyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GQLog.dObj(this, "Return to main activity: onclick yes try to call the activity");
                try {
                    Intent intenty = new Intent(getApplicationContext(), GQMainActivity.class);
                    intenty.putExtra("target", getString(com.app.upincode.getqd.R.string.Frag_Target_Venues)); // start with the Staff fragment
                    startActivity(intenty);
                } catch (ClassCastException cce) {
                    GQLog.dObj(this, "CANCEL RESERVATION cast problem");
                }

            }
        });

        String URL_Login = new String("api/browse-venues/12/");
        MyBaseModel mybm = new MyBaseModel((getApplicationContext()));
        mybm.execute("Get", URL_Login, "0");// Instantiate the custom HttpClient
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
                     GQLog.dObj(this, "Printing then checking for queue available JSON=" + JSON);

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
                    globalVariable.setIAMI(JSON);  // save in global space
                     GQLog.dObj(this, "JSON=" + JSON);

                    // PopulateListView( JSON);
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

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQVenueActivity.this);

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
                if (code == 404) return;// ignore for now
                GQLog.d("GQMain_staff_fragment", "Getting Error From Website =  " + code);


                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQVenueActivity.this);

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
