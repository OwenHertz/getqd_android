package com.app.upincode.getqd.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.logging.GQLog;

import org.json.JSONArray;
import org.json.JSONObject;


public class GQStatisticsDisplayActivity extends GQBaseActivity {
    //TODO clean up!!!
    public static final String EVENT_STAT = "event_stat";

    ViewFlipper vf = null;
    int thePostion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_statistics);  // we are GOING TO BUILD PROGRAMMATICALLY BELOW. NO XML.


        // this gives us the position in the listview (same as position in json) we will access json this way.
        Intent intent = getIntent();
        String target = intent.getStringExtra(EVENT_STAT);
        thePostion = Integer.parseInt(target);


        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        //Set name and email in global/application context
        String analyticsJSON = globalVariable.getAnalyticsEventStats();
        if (analyticsJSON == null) {
             GQLog.dObj(this, "sales stats missing is null - should not happen");
        } else {
            String theCount = getSoldCount(analyticsJSON, thePostion);
            PopulateView(analyticsJSON, thePostion, theCount);// throw it on the screen
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

    public static int target_v = 0;

    public void PopulateView(String JSON, int target, String Number_Sold) {
        target_v = target;
        // row array is only need to construct the table (MAX 20 ROWS)
        String row[] = {"Row1", "Row2", "Row3", "Row4", "Row5", "Row6", "Row7", "Row8", "Row9", "Row10", "Row11",
                "Row12", "Row13", "Row14", "Row15", "Row16", "Row17", "Row18", "Row19", "Row20",};


        try {
            JSONObject loginJSONObject = new JSONObject(JSON);
            GQLog.dObj(this, "Parse count = " + loginJSONObject.getString("count"));
            int v = target;
            JSONArray jArray = loginJSONObject.getJSONArray("results");

            try {
                GQLog.dObj(this, "building viewer target = " + target);

                JSONObject venue = jArray.getJSONObject(target);
                JSONObject event = venue.getJSONObject("event");

                GQLog.dObj(this, "    list Item " + v + " Parse id " + Integer.toString(event.getInt("id")));
                 GQLog.dObj(this, "    list Item " + v + " Parse name " + event.getString("name"));
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
//=======================
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


                Button bprev = new Button(this);
                bprev.setBackgroundColor(0xFF00CCCC);
                bprev.setText("       <Return          ");
                bprev.setGravity(Gravity.LEFT);
                top.addView(bprev);

//====================================== Below deal with outertop
                outertop.addView(top);

                TextView t2 = new TextView(this);
                t2.setText(event.getString("name"));
                t2.setGravity(Gravity.CENTER_HORIZONTAL);
                t2.setTypeface(null, Typeface.BOLD);
                t2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                outertop.addView(t2);

                TextView t4 = new TextView(this);
                t4.setText("Sold:" + Number_Sold);
                t4.setGravity(Gravity.CENTER_HORIZONTAL);
                t4.setBackgroundColor(Color.BLACK);
                t4.setTextColor(Color.WHITE);
                outertop.addView(t4);


                JSONArray tt = venue.getJSONArray("tt_stats");
                String column[] = {"Credit", "Debit", "Cash", "Cash"};
                int rl = tt.length() + 2; // this is the number of rows not including header and total (for a total 2).
                int cl = column.length;
                 GQLog.dObj(this, "R-Length--" + rl + "   " + "C-Length--" + cl);
                TableLayout tableLayout = createTableLayout(row, column, rl, cl);
                int ca = 0, cr = 0, de = 0, co = 0;
                double caf = 0, crf = 0, def = 0, cof = 0;
                int x;
                for (x = 0; x < tt.length(); x = x + 1) {
                    JSONObject tt_target = tt.getJSONObject(x);
                    JSONObject tType = tt_target.getJSONObject("ticket_type");
                    GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount id " + Integer.toString(tType.getInt("id")));
                    GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount name " + tType.getString("name"));
                    GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse number ticket sold " + Integer.toString(tt_target.getInt("num_tickets_sold")));
                    GQLog.dObj(this, "    tt(total_ticket) " + x + " Parse amount sold " + tt_target.getString("amount_sold"));
                    setHeaderTitle(tableLayout, x + 1, 0, tType.getString("name") + "\r\n" +
                                    "(Sold:" + Integer.toString(tt_target.getInt("num_tickets_sold")) + ")" +
                                    "(Total:$" + tt_target.getInt("amount_sold") + ")"
                    );
                    double amount_sold = Double.parseDouble(tt_target.getString("amount_sold"));
                    double ticket_price = amount_sold / tt_target.getInt("num_tickets_sold");
                    GQLog.dObj(this, "Ticket Price =" + String.format("%.2f", ticket_price));

                    JSONObject breakdown = tt_target.getJSONObject("tickets_sold_breakdown");
                    String cash = new String("0");
                    String credit = new String("0");
                    String debit = new String("0");
                    try {
                        cash = Integer.toString(breakdown.getInt("1"));
                        caf = caf + (breakdown.getInt("1") * ticket_price);
                    } catch (Exception e) {
                        GQLog.dObj(this, "problem getting cash - skipping making it 0");
                    }
                    setHeaderTitle(tableLayout, x + 1, 3, cash + "\r\n    ");
                    try {
                        credit = Integer.toString(breakdown.getInt("2"));
                        crf = crf + (breakdown.getInt("2") * ticket_price);
                    } catch (Exception e) {
                        GQLog.dObj(this, "problem getting credit- skipping making it 0");
                    }
                    setHeaderTitle(tableLayout, x + 1, 1, credit + "\r\n    ");
                    try {
                        debit = Integer.toString(breakdown.getInt("3"));
                        def = def + (breakdown.getInt("3") * ticket_price);
                    } catch (Exception e) {
                        GQLog.dObj(this, "problem getting debit - skipping making it 0");
                    }
                    setHeaderTitle(tableLayout, x + 1, 2, debit + "\r\n    ");
                }
                setHeaderTitle(tableLayout, x + 1, 0, "Total");
                setHeaderTitle(tableLayout, x + 1, 1, "$" + String.format("%.2f", crf));
                setHeaderTitle(tableLayout, x + 1, 2, "$" + String.format("%.2f", def));
                setHeaderTitle(tableLayout, x + 1, 3, "$" + String.format("%.2f", caf));


              /*
                    Cash = 1,
                    CreditCard = 2,
                    Interac = 3,
                    Complimentary = 4,
                    Free = 5,
              */

                outertop.addView(tableLayout);


//====================================================
                setContentView(outertop);
                bprev.setOnClickListener(PrevStatsClickListener);

            } catch (Exception e) {
                 GQLog.dObj(this, " exception parsing number 1" + e.toString());
            }


        } catch (Exception e) {
             GQLog.dObj(this, " exception parsing number 2" + e.toString());
        }
    }

    public String getSoldCount(String JSON, int target) {
        String total_sold_count = new String("0");
        target_v = target;

        String row[] = {"Reserve Sec1\r\n (Sold 4,Total:3)", "Reserve Sec2\r\n (Sold 4,Total:3)", "General\r\n(Sold 5.Total $100",
                "Reserved Sec3\r\n(sold 5 total $400)", "VIP \r\n (sold 1,total 1)", "Total", "Total"};

        int amount_sold = 0;
        try {
            JSONObject loginJSONObject = new JSONObject(JSON);
            target_v = target_v + 1;  // next build will build the next view.  we only build one.
            GQLog.dObj(this, "==================GETSOLDCOUNT Parse count = " + loginJSONObject.getString("count"));
            int v = target;
            JSONArray jArray = loginJSONObject.getJSONArray("results");

            try {


                JSONObject venue = jArray.getJSONObject(target);
                JSONObject event = venue.getJSONObject("event");

                JSONArray tt = venue.getJSONArray("tt_stats");
                int x;
                for (x = 0; x < tt.length(); x = x + 1) {
                    JSONObject tt_target = tt.getJSONObject(x);
                    JSONObject tType = tt_target.getJSONObject("ticket_type");
                    amount_sold = amount_sold + tt_target.getInt("num_tickets_sold");
                    GQLog.dObj(this, "amount sold =" + amount_sold);
                }

            } catch (Exception e) {
                GQLog.dObj(this, "amount sold exception " + e.toString());
            }
            total_sold_count = Integer.toString(amount_sold);  // add to the total sold
            return (total_sold_count);
        } catch (Exception e) {
             GQLog.dObj(this, " exception parsing" + e.toString());
        }
        return (total_sold_count);
    }

    public void makeCellEmpty(TableLayout tableLayout, int rowIndex, int columnIndex) {
        // get row from table with rowIndex
        TableRow tableRow = (TableRow) tableLayout.getChildAt(rowIndex);

        // get cell from row with columnIndex
        TextView textView = (TextView) tableRow.getChildAt(columnIndex);

        // make it black
        textView.setBackgroundColor(Color.BLACK);
    }

    public void setHeaderTitle(TableLayout tableLayout, int rowIndex, int columnIndex, String newString) {

        // get row from table with rowIndex
        TableRow tableRow = (TableRow) tableLayout.getChildAt(rowIndex);

        // get cell from row with columnIndex
        TextView textView = (TextView) tableRow.getChildAt(columnIndex);

        textView.setText(newString);
    }

    private TableLayout createTableLayout(String[] rv, String[] cv, int rowCount, int columnCount) {
        String[][] tableContent = new String[][]{
                {"dummy", "dummy", "dummy", "dummy"},
                {"dummy", "0", "0", "2"},
                {"dymmy", "0", "0", "1"},
                {"dummy", "1", "0", "6"},
                {"dummy", "0", "0", "15"},
                {"dummy", "0", "0", "5"},
                {"dummy", "$0", "$0", "$1,495"}
        };
        // 1) Create a tableLayout and its params
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setBackgroundColor(Color.BLACK);

        // 2) create tableRow params
        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
        tableRowParams.setMargins(1, 1, 1, 1);
        tableRowParams.weight = 1;

        for (int i = 0; i < rowCount; i++) {
            // 3) create tableRow
            TableRow tableRow = new TableRow(this);
            tableRow.setBackgroundColor(Color.BLACK);

            for (int j = 0; j < columnCount; j++) {
                // 4) create textView
                TextView textView = new TextView(this);
                //  textView.setText(String.valueOf(j));
                if ((i == 0) || ((i + 1) == rowCount)) {  // For the first and last row Change colore
                    textView.setBackgroundColor(0xFF00CCCC);  //00CCCC Color.RED
                    textView.setTextColor(Color.WHITE);
                } else {
                    textView.setBackgroundColor(Color.WHITE);
                }
                textView.setGravity(Gravity.CENTER);

                String s1 = Integer.toString(i);
                String s2 = Integer.toString(j);
                String s3 = s1 + s2;
                int id = Integer.parseInt(s3);
                //Log.d("TAG", "-___>"+id);
                if (i == 0 && j == 0) {
                    textView.setText("Ticket Type");
                } else if (i == 0) {
                    //Log.d("TAAG", "set Column Headers = " + cv[j-1]);
                    textView.setText(cv[j - 1]);
                } else if (j == 0) {
                    //Log.d("TAAG", "Set Row Headers = " + rv[i-1]);
                    textView.setText(rv[i - 1]);
                } else {
                    //Log.d("TAAG", "Set content j=" +j + " i=" + i + " content = " + tableContent[i][j]);
                    textView.setText(tableContent[i][j]);
                }

                // 5) add textView to tableRow
                tableRow.addView(textView, tableRowParams);
            }

            // 6) add tableRow to tableLayout
            tableLayout.addView(tableRow, tableLayoutParams);
        }

        return tableLayout;
    }

    private View.OnClickListener NextStatsClickListener = new View.OnClickListener() {

        public void onClick(View v) {
             GQLog.dObj(this, "goto next no used");
            vf.showNext();
        }

    };
    private View.OnClickListener PrevStatsClickListener = new View.OnClickListener() {

        public void onClick(View v) {
             GQLog.dObj(this, "going back to stats listview");
            Intent intent = new Intent(getApplicationContext(), GQStatisticsActivity.class);
            startActivity(intent);
        }

    };

}
