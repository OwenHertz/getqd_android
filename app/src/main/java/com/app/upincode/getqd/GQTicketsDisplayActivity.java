package com.app.upincode.getqd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.utils.ISO8601;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.*;

import java.util.EnumMap;
import java.util.Map;

public class GQTicketsDisplayActivity extends ActionBarActivity {
    ViewFlipper vf = null;
    int thePostion = 0;
    Button nextB = null;
    Button prevB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.displaytickets);  // we are GOING TO BUILD PROGRAMMATICALLY BELOW. NO XML.


        // this gives us the position in the listview (same as position in json) we will access json this way.
        Intent intenty = getIntent();
        String target = intenty.getStringExtra("position");
        thePostion = Integer.parseInt(target);

        prevB = (Button) findViewById(com.app.upincode.getqd.R.id.prevButton);
        prevB.setOnClickListener(PrevStatsClickListener);
        nextB = (Button) findViewById(com.app.upincode.getqd.R.id.nextButton);
        nextB.setOnClickListener(NextStatsClickListener);

        vf = (ViewFlipper) findViewById(com.app.upincode.getqd.R.id.viewFlipper);

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        //Set name and email in global/application context
        String Tickets = globalVariable.getTicketsTicketGroup();
        if (Tickets == null) {
             GQLog.dObj(this, "Tickets information missing is null - should not happen");
        } else {
            PopulateView(Tickets, thePostion);// throw it on the screen
        }
        Button btyes = (Button) findViewById(com.app.upincode.getqd.R.id.ResoReturnButton);
        btyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GQLog.dObj(this, "CANCEL: onclick yes try to call the activity");
                try {
                    Intent intenty = new Intent(getApplicationContext(), GQMainActivity.class);
                    intenty.putExtra("target", getString(com.app.upincode.getqd.R.string.Frag_Target_Tickets)); // start with the Staff fragment
                    startActivity(intenty);
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

    public void PopulateView(String JSON, int target) {
        target_v = target;
        // row array is only need to construct the table (MAX 20 ROWS)
        String row[] = {"Row1", "Row2", "Row3", "Row4", "Row5", "Row6", "Row7", "Row8", "Row9", "Row10", "Row11",
                "Row12", "Row13", "Row14", "Row15", "Row16", "Row17", "Row18", "Row19", "Row20",};
        String TargetTimeZone = new String("US/Mountain");  // the default is calgary time.

        try {
            //  GQLog.dObj(this,"JSON="+JSON);
            JSONObject loginJSONObject = new JSONObject(JSON);
            GQLog.dObj(this, "Parse count = " + loginJSONObject.getString("count"));
            int v = target;
            JSONArray jArray = loginJSONObject.getJSONArray("results");

            try {
                GQLog.dObj(this, "building viewer target = " + target);


                int n;


                JSONObject event = jArray.getJSONObject(thePostion);
                JSONObject event2 = jArray.getJSONObject(thePostion);
                n = thePostion;

                 GQLog.dObj(this, "    list Item " + n + " Parse id " + Integer.toString(event2.getInt("id")));
                JSONObject type = event2.getJSONObject("type");
                String Type_Name = type.getString("name");

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
                for (int c = 0; c < tt.length(); c = c + 1) {
                    JSONObject tt_target = tt.getJSONObject(c);

                     GQLog.dObj(this, "    tt(total_ticket) " + c + " Parse bar_code " + tt_target.getString("barcode_string"));  //pkpass
                     GQLog.dObj(this, "    tt(total_ticket) " + c + " Parse bar_code " + tt_target.getString("pkpass"));
                    //++++++++++++++++++++++++++++++++++++++++++++
                    //=======================
                    LinearLayout.LayoutParams outerbgparams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

                    LinearLayout outertop = new LinearLayout(this);
                    LinearLayout.LayoutParams outertopparams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

                    outertop.setOrientation(LinearLayout.VERTICAL);
                    outertop.setLayoutParams(outertopparams);
                    outertop.setBackgroundColor(Color.TRANSPARENT);
                    outertop.setGravity(Gravity.CENTER_HORIZONTAL);
                    outertop.setWeightSum(1.0f);


                    //=======================  deal with top
                    LinearLayout.LayoutParams bgparams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

                    LinearLayout top = new LinearLayout(this);
                    LinearLayout.LayoutParams topparams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);

                    top.setOrientation(LinearLayout.HORIZONTAL);
                    top.setLayoutParams(topparams);
                    top.setBackgroundColor(Color.TRANSPARENT);
                    top.setGravity(Gravity.LEFT);


                    //====================================== Below deal with outertop
                    outertop.addView(top);

                    TextView t1 = new TextView(this);
                    t1.setText(Integer.toString(c + 1) + " OF " + Integer.toString(tt.length()));
                    t1.setBackgroundColor(0xFF00CCCC);
                    t1.setGravity(Gravity.CENTER_HORIZONTAL);
                    outertop.addView(t1);

                    TextView t2 = new TextView(this);
                    t2.setText(name);
                    t2.setGravity(Gravity.CENTER_HORIZONTAL);
                    outertop.addView(t2);

                    TextView t3 = new TextView(this);
                    t3.setText(theDate);
                    t3.setGravity(Gravity.CENTER_HORIZONTAL);
                    outertop.addView(t3);

                    TextView t4 = new TextView(this);
                    t4.setText(theTime);
                    t4.setGravity(Gravity.CENTER_HORIZONTAL);
                    outertop.addView(t4);


                    TextView t4a = new TextView(this);
                    t4a.setText(Type_Name);
                    t4a.setGravity(Gravity.CENTER_HORIZONTAL);
                    outertop.addView(t4a);

                    TextView t5 = new TextView(this);
                    t5.setText(tt_target.getString("barcode_string"));
                    t5.setGravity(Gravity.CENTER_HORIZONTAL);
                    outertop.addView(t5);

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;

                    // get the pixels per inch
                    float ppi;
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    ppi = metrics.xdpi;

                    //Graphics g = this.CreateGraphics();
                     GQLog.dObj(this, " width = " + width + " ppi = " + ppi);

                    float widthPts = (width * 72) / ppi;
                    //onbarcode.com  AndroidBarcodeView aView = new AndroidBarcodeView(this,tt_target.getString("barcode_string"),width);
                    //=================
                    // barcode data
                    String barcode_data = tt_target.getString("barcode_string");

                    // barcode image
                    Bitmap bitmap = null;
                    ImageView iv = new ImageView(this);

                    try {

                        bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.CODE_39, 600, 300);
                        iv.setImageBitmap(bitmap);

                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }

                    //l.addView(iv);
                    //======================

                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    iv.setLayoutParams(lp);
                    outertop.addView(iv);

                    // Params for the ImageView
                    final RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

                    final ViewFlipper.LayoutParams frameLayoutLayoutParams = new ViewFlipper.LayoutParams(
                            ViewFlipper.LayoutParams.MATCH_PARENT, ViewFlipper.LayoutParams.MATCH_PARENT);

                    vf.addView(outertop);
                }
                //====================================================


            } catch (Exception e) {
                 GQLog.dObj(this, " exception parsing number 1" + e.toString());
            }


        } catch (Exception e) {
             GQLog.dObj(this, " exception parsing number 2" + e.toString());
        }
    }

    /**************************************************************
     * getting from com.google.zxing.client.android.encode.QRCodeEncoder
     * <p/>
     * See the sites below
     * http://code.google.com/p/zxing/
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/EncodeActivity.java
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/QRCodeEncoder.java
     */

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    private View.OnClickListener NextStatsClickListener = new View.OnClickListener() {

        public void onClick(View v) {
             GQLog.dObj(this, "goto next ");
            vf.showPrevious();
        }

    };
    private View.OnClickListener PrevStatsClickListener = new View.OnClickListener() {

        public void onClick(View v) {
             GQLog.dObj(this, "going back to stats listview");
            vf.showNext();
        }

    };

}
