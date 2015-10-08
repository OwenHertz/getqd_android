package com.app.upincode.getqd.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.utils.ISO8601;
import com.app.upincode.getqd.utils.MyViewDownloaderTaskSelectTicketsActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class GQSelectTicketActivity extends GQBaseActivity {
    public static final String EVENT_ACCESS = "event_access";

    ListView listview = null;
    ImageView imageView = null;
    String CurrentJSON = null;
    boolean debug = true;
    int thePosition = 0;
    int[] basketArray = null;
    int[] savedBasketArray = null;
    TextView tvTotal = null;
    TextView tvTotalWithFees = null;
    Spinner PayWith = null;
    Spinner Delivery_Method = null;
    String basketid = null;
    String theSlug = null;
    TextView tvTitle = null;
    boolean paymentTypeCash = false;
    boolean paymentTypeComplementary = false;
    Spinner[] spinnerArray = null;
    int spinnerArrayCount = 0;

    int[] payment_array = new int[]{0, 0, 0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.activity_select_tickets);

        // this gives us the position in the listview (same as position in json) we will access json this way.
        Intent intent = getIntent();
        String target = intent.getStringExtra("position");
        thePosition = Integer.parseInt(target);

        tvTotal = (TextView) findViewById(com.app.upincode.getqd.R.id.totalprice);
        tvTotalWithFees = (TextView) findViewById(com.app.upincode.getqd.R.id.totalpricewithfees);
        PayWith = (Spinner) findViewById(com.app.upincode.getqd.R.id.PayWith);
        Delivery_Method = (Spinner) findViewById(com.app.upincode.getqd.R.id.Delivery_Method);

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        globalVariable.setBasketInfo(null);  // if we had a basket before forget it.
        String SlugInfo = globalVariable.getBoxOfficeEventAccessesCurrentSlug();  // save the JSON using ticketing info inside
        String Slug = null;

        // This tvTitle textview is going to get a background image of the SLUG and we will some text over it
        tvTitle = (TextView) findViewById(com.app.upincode.getqd.R.id.textViewTitle);

        String BOEAJSON = globalVariable.getBoxOfficeEventAccesses();
        JSONArray jArray = null;
        try {
            jArray = new JSONArray(BOEAJSON);

            JSONObject event = jArray.getJSONObject(thePosition);
            Slug = event.getString("slug");
            theSlug = Slug;   // save this for later


            JSONObject event_specifics = event.getJSONObject("event");
            String eventName = event_specifics.getString("name");
            JSONObject venue = event.getJSONObject("venue");
            String name = event_specifics.getString("name");

            GQLog.dObj(this, "slug=" + Slug);
            GQLog.dObj(this, "Parse event name is = " + event_specifics.getString("name"));
            GQLog.dObj(this, "Parse event start time is = " + event_specifics.getString("starts_on"));
            GQLog.dObj(this, "Parse event venue name is = " + venue.getString("name"));

            if (name.length() > 45) {
                name = new String(name.substring(0, 45) + new String("  ..."));
            }
            String result = new String(name + "\n" + venue.getString("name"));
            tvTitle.setText(result);
            tvTitle.setTextColor(Color.WHITE);  // make it green!!!

            new MyViewDownloaderTaskSelectTicketsActivity(tvTitle).execute(event_specifics.getString("image"));
            // if (imageView != null) {
            // new MyViewDownloaderTaskSelectTicketsActivity(imageView).execute(event_specifics.getString("image"));
            // new ImageDownloaderTaskBackground(imageView).execute(event_specifics.getString("image"));
            //}
        } catch (Exception e) {
             GQLog.dObj(this, "problem finding the slug");
        }
        listview = (ListView) findViewById(com.app.upincode.getqd.R.id.listViewStaff);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GQLog.dObj(this, "Sales event Selected Clicked postion = " + position);
                //Intent intenty = new Intent(getApplicationContext(),GQStatisticsDisplayActivity.class);
                //intenty.putExtra("position",Integer.toString(position)); // start with the display statistics activity
                // startActivity(intenty);
            }

        });
        Button btyes = (Button) findViewById(com.app.upincode.getqd.R.id.StaffCancelReservationButton);
        btyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GQLog.dObj(this, "CANCEL RESERVATION: onclick yes try to call the activity");
                try {
                    final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                    String role = globalVariable.getSellingRole();
                    if (role.equals("Seller")) {
                        Intent intent = new Intent(getApplicationContext(), GQSalesActivity.class);
                        //intenty.putExtra("target", getString(R.string.Frag_Target_Staff)); // start with the Staff fragment
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), GQMainActivity.class);
                        intent.putExtra("target", getString(com.app.upincode.getqd.R.string.Frag_Target_Events)); // start with the Staff fragment
                        startActivity(intent);
                    }
                } catch (ClassCastException cce) {
                    GQLog.dObj(this, "CANCEL RESERVATION cast problem");
                }

            }
        });
        Button btyes2 = (Button) findViewById(com.app.upincode.getqd.R.id.ContinueButton);
        btyes2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GQLog.dObj(this, "Continue: onclick yes try to call the activity");
                try {
                    // before we do anything lets check with the PayWith Spinner.
                    if (PayWith.getSelectedItemPosition() == 0) {
                        ErrorInBooking("Please Select Payment Type");
                        return;
                    }
                    int type = payment_array[PayWith.getSelectedItemPosition() - 1];  // cash =1, CC=2, interact=3,complemntary=4
                    if (type == 3) {
                        Intent intent = new Intent(getApplicationContext(), GQInteracActivity.class);
                        intent.putExtra("position", Integer.toString(thePosition));
                        intent.putExtra("basketid", basketid);
                        intent.putExtra("slug", theSlug);
                        startActivity(intent);
                    }
                    if (type == 2) {
                        Intent intent = new Intent(getApplicationContext(), GQCardioActivity.class);
                        intent.putExtra("position", Integer.toString(thePosition));
                        intent.putExtra("basketid", basketid);
                        intent.putExtra("slug", theSlug);
                        startActivity(intent);
                    }
                    if (type == 1) {
                        paymentTypeCash = true;
                        Toast.makeText(getApplicationContext(), "Using Cash to pay", Toast.LENGTH_LONG).show();
                    }
                    if (type == 4) {
                        paymentTypeComplementary = true;
                        // Toast.makeText(getApplicationContext(),"Complementary",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), GQCompActivity.class);
                        intent.putExtra("position", Integer.toString(thePosition));
                        intent.putExtra("basketid", basketid);
                        intent.putExtra("slug", theSlug);
                        startActivity(intent);
                    }
                } catch (ClassCastException cce) {
                    GQLog.dObj(this, "Continue cast problem");
                }

            }
        });
        // If we already have the info do not reGET it.
        SlugInfo = null;  // Just testing to make sure we
        if (SlugInfo == null) {
            //Slug = new String("eat-sleep-rave-repea");
            String URL = new String("api/events/box-office/event-accesses/" + Slug);

            //    /api/events/box-office/event-accesses/
            //TODO fix
//            MyBaseModel mybm = new MyBaseModel(getApplicationContext());
//            mybm.execute("Get", URL, "0");// Instantiate the custom HttpClient

            // THis is a chained model first Slug then create basket.
        } else {
            PopulateWithJSON(SlugInfo);
            CreateBasket();
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

    public void CalculateNewTotal() {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        String JSON = globalVariable.getBoxOfficeEventAccessesCurrentSlug();  // save the JSON using ticketing info inside
        try {
            String sresult = new String("$0.00");
            String sresultwithfees = new String("$0.00");
            JSONObject jobj = new JSONObject(JSON);
            int n;
            JSONArray jArrayIn = jobj.getJSONArray("inventories");
            float grandtotal = 0;
            float grandtotalwithfees = 0;

            for (n = 0; n < jArrayIn.length(); n = n + 1) {
                JSONObject inventorie = jArrayIn.getJSONObject(n);
                JSONObject tt = inventorie.getJSONObject("ticket_type");
                String ttprice = tt.getString("price");

                float total = Float.parseFloat(ttprice) * basketArray[n];
                grandtotal = grandtotal + total;

                String sservice_charges = tt.getString("service_charges");
                float service_charges = Float.parseFloat(sservice_charges) * basketArray[n];
                grandtotalwithfees = grandtotal + service_charges;
                String staxes = tt.getString("addtional costs");

                GQLog.dObj(this, "Ticket Price is = " + ttprice + "number of tickets =" + basketArray[n]);
                GQLog.dObj(this, "service_charges is = " + sservice_charges + "number of tickets =" + basketArray[n]);
                GQLog.dObj(this, "additional costs is = " + staxes + "number of tickets =" + basketArray[n]);

            }
            sresult = new String("$" + String.format("%.2f", grandtotal));
            sresultwithfees = new String("$" + String.format("%.2f", grandtotalwithfees));
            tvTotal.setText(sresult);
            tvTotalWithFees.setText(sresultwithfees);
        } catch (Exception e) {
             GQLog.dObj(this, "Problem parsing json" + e.toString());
        }
    }

    public void setTotals(String sresult, String sresultwithfees) {
        tvTotal.setText(sresult);
        tvTotalWithFees.setText(sresultwithfees);
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

    public void PopulateWithJSON(String JSON) {
        ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
        try {
            JSONObject jobj = new JSONObject(JSON);

            int n;
            int ccash = 0, ccc = 0, cinteract = 0, ccomp = 0;
            JSONArray jArrayIn = jobj.getJSONArray("inventories");
            basketArray = new int[jArrayIn.length()];
            spinnerArray = new Spinner[jArrayIn.length()];
            spinnerArrayCount = 0;
            for (n = 0; n < jArrayIn.length(); n = n + 1) {
                basketArray[n] = 0;  // we are keeping count how many items are being used in each of the inventories

                JSONObject inventorie = jArrayIn.getJSONObject(n);
                String id = Integer.toString(inventorie.getInt("id"));

                String name = inventorie.getString("name");
                String limit_count = Integer.toString(inventorie.getInt("limit_count"));

                JSONObject tt = inventorie.getJSONObject("ticket_type");
                String ttname = tt.getString("name");             // this is what goes on the screen
                String ttprice = tt.getString("price");            // this is what goes on the screen
                String ttshipping_type = tt.getString("shipping_type");    // ***************************** the only type of delivery we support is type 2
                String ttPurchase_Limit = tt.getString("purchase_limit");
                String ttSale_Ends_On = tt.getString("sale_ends_on");  //= "2016-02-04T07:00:00Z";


                GQLog.dObj(this, "Parse event venue id is = " + id);
                GQLog.dObj(this, "Parse event venue name is = " + name);
                GQLog.dObj(this, "Parse event venue limit_count is = " + limit_count);

                GQLog.dObj(this, "Parse ticket type name is = " + ttname);
                GQLog.dObj(this, "Parse ticket type price is = " + ttprice);
                GQLog.dObj(this, "Parse ticket type purchase limie is = " + ttPurchase_Limit);
                GQLog.dObj(this, "Parse ticket type sale ends on= " + ttSale_Ends_On);
                GQLog.dObj(this, "Parse ticket type Shipping type = " + ttshipping_type); // we may need to change the spinner below when we have more than one shipping type.
                /*
                The following information is used to make the payment type spinner array.
                 */
                Boolean comp = jobj.getBoolean("complimentary_enabled");
                JSONObject owners_permission = inventorie.getJSONObject("owner_permission");
                Boolean cash_enabled = owners_permission.getBoolean("cash_enabled");
                Boolean cc_enabled = owners_permission.getBoolean("cc_enabled");
                Boolean interac_enabled = owners_permission.getBoolean("interac_enabled");

                if (comp == true) ccomp = ccomp + 1;
                if (cash_enabled == true) ccash = ccash + 1;
                if (interac_enabled == true) cinteract = cinteract + 1;
                if (cc_enabled == true) ccc = ccc + 1;
                GQLog.dObj(this, "Parse comp is = " + comp);
                GQLog.dObj(this, "Parse cash_enabled is = " + cash_enabled);
                GQLog.dObj(this, "Parse cc_enabled is = " + cc_enabled);
                GQLog.dObj(this, "Parse interact_enabled is = " + interac_enabled);

                ISO8601 targetDate = new ISO8601();
                Calendar targetDateCal = targetDate.toCalendar(ttSale_Ends_On);
                Calendar currDate = new GregorianCalendar();

                if (targetDateCal.getTimeInMillis() > currDate.getTimeInMillis()) { // if the target is not in the future - its in the past don't show this ticket if in the past

                    mNavItems.add(new NavItem(ttname, "$" + ttprice, ttPurchase_Limit, " "));
                } else {
                    mNavItems.add(new NavItem(ttname, "$" + ttprice, "0", "(NA)"));
                     GQLog.dObj(this, "Flagging old  ticket type");

                }

            }
            /*
                    <item>Payment Type</item>
                    <item>Pay with Interac</item>
                    <item>Pay with Credit</item>
                    <item>Complimentary</item>
                    <item>Pay with Cash!</item>
             */
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            String role = globalVariable.getSellingRole();

            // Clear out the payment array.  we are going to build a structure that enables to offer the customer or Seller an
            // choice of options in which they can pay.
            for (int c = 0; c < payment_array.length; c = c + 1) {
                payment_array[c] = 0;
            }
            // cash =1, CC=2, interact=3,complemntary=4
            int index = 0;
            GQLog.dObj(this, "updating Paywith Spinner");
            ArrayList<String> spinnerArray = new ArrayList<String>();
            spinnerArray.add("Payment Type");

            if ((cinteract != 0) && (role.equals("Seller"))) {
                spinnerArray.add("Pay with Interac");
                payment_array[index] = 3;
                index = index + 1;
            }      //Buyers can't use interact
            if (ccc != 0) {
                spinnerArray.add("Pay with Credit");
                payment_array[index] = 2;
                index = index + 1;
            }
            if (ccomp != 0) {
                spinnerArray.add("Complimentary");
                payment_array[index] = 4;
                index = index + 1;
            }
            if ((ccash != 0) && (role.equals("Seller"))) {
                spinnerArray.add("Pay with Cash");
                payment_array[index] = 1;
                index = index + 1;
            }          //Buyers can't buy with cash


            ArrayAdapter dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerArray);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            PayWith.setAdapter(dataAdapter);


            GQLog.dObj(this, "updating Delivery Method Spinner");
            ArrayList<String> spinnerArray1 = new ArrayList<String>();
            spinnerArray1.add("Delivery Method");
            spinnerArray1.add("Print at Home");

            ArrayAdapter dataAdapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerArray1);
            dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_item);
            Delivery_Method.setAdapter(dataAdapter1);


            GQLog.dObj(this, "updating finished with Paywith Spinner");


        } catch (Exception e) {
             GQLog.dObj(this, "Problem parsing json" + e.toString());
        }

        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        listview.setAdapter(adapter);

    }

    class NavItem {
        String mTicketType;
        String mPrice;
        String mTicketLimit;
        String mSuffix;

        public NavItem(String TicketType, String Price, String TicketLimit, String Suffix) {
            mTicketType = "  " + TicketType;  // image URL
            mPrice = Price + "  ";
            mTicketLimit = TicketLimit;
            mSuffix = Suffix;
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
                view = inflater.inflate(com.app.upincode.getqd.R.layout.sales_ticket_item, null);
            } else {
                view = convertView;
            }
            Spinner numtickets = (Spinner) view.findViewById(com.app.upincode.getqd.R.id.spinner1);
            TextView ticketprice = (TextView) view.findViewById(com.app.upincode.getqd.R.id.ticketPrice);
            TextView tickettype = (TextView) view.findViewById(com.app.upincode.getqd.R.id.tickettype);
            TextView suffixtype = (TextView) view.findViewById(com.app.upincode.getqd.R.id.suffix);


            ticketprice.setText(mNavItems.get(position).mPrice);
            tickettype.setText(mNavItems.get(position).mTicketType);
            suffixtype.setText(mNavItems.get(position).mSuffix);
            String sLimit = mNavItems.get(position).mTicketLimit;

            spinnerArray[spinnerArrayCount] = numtickets;
            spinnerArrayCount = spinnerArrayCount + 1;

            ArrayList<String> alist = new ArrayList<String>();
            int limit = Integer.parseInt(sLimit);
            for (int c = 0; c <= limit; c = c + 1) {
                alist.add(Integer.toString(c));
            }
            // Build the array and insert into the the view.
            ArrayAdapter dataAdapter = new ArrayAdapter(GQSelectTicketActivity.this, android.R.layout.simple_spinner_item, alist);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            numtickets.setAdapter(dataAdapter);

            // add spinner and listener to spinner here
            numtickets.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            savedBasketArray = Arrays.copyOf(basketArray, basketArray.length);     // make a copy of the basket array in case things get messed on the update basket. like no tickets.

                            int spinner_position = listview.getPositionForView(view);  // get the correct spinner
                            int oldposition = basketArray[spinner_position];      // get the previous value in the basket_array
                            basketArray[spinner_position] = position;                           // put the new value in the basket_array

                            int change = position - oldposition;
                            if (change == 0)
                                return;                                                 // if there is no change don't bother


                            GQLog.dObj(this, "spinner: position/number of tickets =" + position + " id = " + id);
                            GQLog.dObj(this, "spinner: index to spinner position =" + spinner_position + " id = " + id);
                            GQLog.dObj(this, "spinner: index to basketArray[spinner_position] =" + basketArray[spinner_position] + " id = " + id);

                            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                            String JSON = globalVariable.getBasketInfo();  // save the JSON using ticketing info inside
                            try {
                                JSONObject bi = new JSONObject(JSON);
                                String BasketID = Integer.toString(bi.getInt("id"));
                                 GQLog.dObj(this, "UPDATE BASKET: BasketID = " + BasketID);

                                int n;
                                JSON = globalVariable.getBoxOfficeEventAccessesCurrentSlug();  // use the JSON we saved to fish out stuff need to change basket contents
                                JSONObject jobj = new JSONObject(JSON);
                                String Slug = jobj.getString("slug");

                                JSONArray jArrayIn = jobj.getJSONArray("inventories");
                                JSONObject inventorie = jArrayIn.getJSONObject(spinner_position);
                                String InventoryID = Integer.toString(inventorie.getInt("id"));

                                JSONObject tt = inventorie.getJSONObject("ticket_type");
                                String ttid = Integer.toString(tt.getInt("id"));

                                GQLog.dObj(this, "UPDATE BASKET: Event Access = " + Slug);
                                GQLog.dObj(this, "UPDATE BASKET: Inventory ID = " + InventoryID);
                                GQLog.dObj(this, "UPDATE BASKET: Type Type ID = " + ttid);
                                GQLog.dObj(this, "UPDATE BASKET: Change = " + Integer.toString(change));

                                // we are trying to adjust the count in the basket for this ticket type.  Increase or decrease what we want in basket.
                                String URL = new String("api/events/box-office/baskets/" + BasketID + "/request/");

                                //    /api/events/box-office/event-accesses/

                                //TODO fix
//                                MyBaseModelUpdateBasket mybmub = new MyBaseModelUpdateBasket(getApplicationContext());
//                                mybmub.execute("Post", URL, "8",
//                                        "event_access", Slug,
//                                        "type", ttid,
//                                        "change", Integer.toString(change),
//                                        "inventory", InventoryID
//
//                                );// Instantiate the custom HttpClient

                            } catch (Exception e) {
                                 GQLog.eObj(this, "trouble with send an updated basket count");
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                             GQLog.dObj(this, "Spinner not selected in listview");
                        }
                    });
            return view;
        }
    }

    public void ErrorOnUpdateBasket() {
        boolean dump = false;
        if (dump == true) {
             GQLog.dObj(this, "Trying to unwind from error on update basket. saved basket:");
            for (int a = 0; a < basketArray.length; a = a + 1) {
                 GQLog.dObj(this, "   Index is " + Integer.toString(a) + "  " + savedBasketArray[a]);
            }
             GQLog.dObj(this, "Trying to unwind from error on update basket.  basket:");
            for (int b = 0; b < basketArray.length; b = b + 1) {
                 GQLog.dObj(this, "   Index is " + Integer.toString(b) + "  " + basketArray[b]);
            }
        }
        basketArray = Arrays.copyOf(savedBasketArray, savedBasketArray.length);     // make a copy of the basket array in case things get messed on the update basket. like no tickets.
        for (int n = 0; n < basketArray.length; n = n + 1) {
            spinnerArray[n].setSelection(savedBasketArray[n]);
        }
    }

    public void CreateBasket() {   // This creates a basket on the getqd Website.

        // NOW FOR PART TWO MAKING THE BASKET
        String URL2 = new String("api/events/box-office/baskets/get-or-create/");

        //    Go Create Basket
        //TODO fix
//        MyBaseModelCreateBasket mybmcb = new MyBaseModelCreateBasket(GQSelectTicketActivity.this);
//        mybmcb.execute("Post", URL2, "0");// Instantiate the custom HttpClient
    }

    /*
    private View.OnItemClickListener NumberOfTicketsClickListener = new View.OnItemClickListener() {

        public void onClick(View v) {
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);
             GQLog.dObj(this, "NumberOfTickets +position=" + position);
        }
    };
    */
    private View.OnClickListener GetTicketsClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);
             GQLog.dObj(this, "position in listview=" + position);

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
//             GQLog.dObj(this, "doing updateView retcode = " + retCode + "Code = " + code);
//            if (retCode == false) {
//                 GQLog.dObj(this, "Unable to Access the Website");
//                return;
//            }
//            if ((code == 200) || (code == 201)) {  //  JSON Looks like the following
//                final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
//                globalVariable.setBoxOfficeEventAccessesCurrentSlug(JSON);  // save the JSON using ticketing info inside
//                PopulateWithJSON(JSON);
//                //printJSON(JSON);  // Looks Like the following
//                CreateBasket();
///*
//                {"count": 385, "next": "http://beta.getqd.me/api/venue/reservations/all/?page=2", "previous": null, "next_page_number": 2, "previous_page_number": null, "page_number": 1, "num_pages": 20, "next_list": [2, 3, 4, 5, 6, 7, 8, 9, 10, 11], "previous_list": [], "results": [{"id": 2311, "name": "Milan GetQd Pecov", "num_of_people": 10, "details": "Corporate function", "sms_data": null, "spinnerReasons": 1, "booked_by": null, "is_approved": false, "arrival_date": "2014-06-22T04:00:00Z", "location": null, "type": 49, "customer": {"id": 2, "first_name": "Milan GetQd", "last_name": "Pecov", "username": "Milan GetQd Pecov", "email": "mpecov@yahoo.ca", "gravatar": "https://gravatar.com/avatar/d54f492c90d72fc63b716859a11bcb89", "gravatar_small": "https://gravatar.com/avatar/d54f492c90d72fc63b716859a11bcb89", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2633, "name": "Lorriane Haston", "num_of_people": 3, "details": "Corporate function", "sms_data": null, "spinnerReasons": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T13:33:07.077Z", "location": null, "type": 49, "customer": {"id": 167, "first_name": "Lorriane", "last_name": "Haston", "username": "Lorriane Haston", "email": "soksfa3@afasfd.caa", "gravatar": "https://gravatar.com/avatar/78750f518874cf6dfbe6e936911e405d", "gravatar_small": "https://gravatar.com/avatar/78750f518874cf6dfbe6e936911e405d", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2621, "name": "Cordell Roscoe", "num_of_people": 6, "details": "Corporate function", "sms_data": null, "spinnerReasons": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T15:13:07.077Z", "location": null, "type": 49, "customer": {"id": 169, "first_name": "Cordell", "last_name": "Roscoe", "username": "Cordell Roscoe", "email": "soksfa5@afasfd.caa", "gravatar": "https://gravatar.com/avatar/1d245263d1555d7e5d7c61e6191cbb22", "gravatar_small": "https://gravatar.com/avatar/1d245263d1555d7e5d7c61e6191cbb22", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2619, "name": "Oswaldo Hartman", "num_of_people": 5, "details": "Stag/Stagette", "sms_data": null, "spinnerReasons": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T13:23:07.077Z", "location": null, "type": 49, "customer": {"id": 166, "first_name": "Oswaldo", "last_name": "Hartman", "username": "Oswaldo Hartman", "email": "soksfa2@afasfd.caa", "gravatar": "https://gravatar.com/avatar/d3297ba5336906f5a1c4545f767332bb", "gravatar_small": "https://gravatar.com/avatar/d3297ba5336906f5a1c4545f767332bb", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2657, "name": "Oswaldo Hartman", "num_of_people": 3, "details": "Birthday", "sms_data": null, "spinnerReasons": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T14:53:07.077Z", "location": null, "type": 49, "customer": {"id": 166, "first_name": "Oswaldo", "last_name": "Hartman", "username": "Oswaldo Hartman", "email": "soksfa2@afasfd.caa", "gravatar": "https://gravatar.com/avatar/d3297ba5336906f5a1c4545f767332bb", "gravatar_small": "https://gravatar.com/avatar/d3297ba5336906f5a1c4545f767332bb", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2614, "name": "Cordell Roscoe", "num_of_people": 6, "details": "Bottle service", "sms_data": null, "spinnerReasons": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T14:13:07.077Z", "location": null, "type": 49, "customer": {"id": 169, "first_name": "Cordell", "last_name": "Roscoe", "username": "Cordell Roscoe", "email": "soksfa5@afasfd.caa", "gravatar": "https://gravatar.com/avatar/1d245263d1555d7e5d7c61e6191cbb22", "gravatar_small": "https://gravatar.com/avatar/1d245263d1555d7e5d7c61e6191cbb22", "phone_number": null}, "checkin_set": [], "type_text": "Other"}, {"id": 2645, "name": "Dwayne Cessna", "num_of_people": 3, "details": "General", "sms_data": null, "spinnerReasons": 1, "booked_by": 2, "is_approved": true, "arrival_date": "2014-10-17T15:13:07.077Z", "location": null, "type": 49, "custom
//*/
//
//            } else if (code == 400) {
//                 GQLog.dObj(this, "Getting 400  Error from the website");
//                // Username or password false, display and an error
//
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQSelectTicketActivity.this);
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
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQSelectTicketActivity.this);
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
//           JSON Should look something Like
//           {
//   "complimentary_enabled" = 1;
//   event =     {
//       created = "2015-07-26T20:44:08.350Z";
//       description = "<snipped>";
//       "ends_on" = "2016-02-05T07:00:00Z";
//       "facebook_id" = "<null>";
//       "getqd_fee" = "0.05";
//       "getqd_fee_added" = "1.00";
//       id = 49;
//       image = "https://getqd-beta.s3.amazonaws.com/media/images/events/cowboys-nightclub/images/11270601_975032489195971_3854382511811500659_o.jpg";
//       location = 12;
//       name = "EAT. SLEEP. RAVE. REPEAT - VOL 3 - ENTER THE ELECTRIC RODEO";
//       "opens_at" = "<null>";
//       slug = "eat-sleep-rave-repea";
//       "starts_on" = "2016-02-04T07:00:00Z";
//       subtitle = "<null>";
//       terms = "<null>";
//       thumbnail = "https://getqd-beta.s3.amazonaws.com/media/images/events/cowboys-nightclub/thumbnails/11270601_975032489195971_3854382511811500659_o.jpg";
//       updated = "2015-07-26T20:44:08.350Z";
//       venue = 12;
//   };
//   "frontend_share_url" = "https://beta.getqd.me/events/eat-sleep-rave-repea/usr/12/";
//   "given_by" =     {
//       id = 12;
//       name = "Cowboys ";
//       slug = "cowboys-nightclub";
//       thumbnail = "https://getqd-beta.s3.amazonaws.com/media/images/venues/cowboys-nightclub/avatars/cowboys-logo.png";
//   };
//   inventories =     (
//               {
//           id = 38;
//           "limit_count" = 122;
//           name = Main;
//           "owner_permission" =             {
//               "cash_enabled" = 0;
//               "cc_enabled" = 1;
//               creator = 12;
//               id = 45;
//               "interac_enabled" = 1;
//               "sales_limit" = "<null>";
//               tti = 38;
//               venue =                 {
//                   id = 12;
//                   name = "Cowboys ";
//                   slug = "cowboys-nightclub";
//                   thumbnail = "https://getqd-beta.s3.amazonaws.com/media/images/venues/cowboys-nightclub/avatars/cowboys-logo.png";
//               };
//               visibility = 1;
//           };
//           "ticket_type" =             {
//               "base_inventory" = 38;
//               created = "2015-07-26T20:44:08.612Z";
//               description = "<null>";
//               event = 49;
//               id = 50;
//               name = General;
//               price = "1.00";
//               "purchase_limit" = 20;
//               "sale_ends_on" = "2016-02-04T07:00:00Z";
//               "sale_starts_on" = "2015-07-26T20:44:08.612Z";
//               "service_charges" = "1.05";
//               "shipping_type" =                 (
//                   2
//               );
//               "sold_out" = 0;
//               taxes = "0.00";
//               "total_price" = "2.05";
//               updated = "2015-08-11T18:22:01.095Z";
//           };
//       }
//   );
//   slug = "eat-sleep-rave-repeat-vol-3-enter-the-electric-rod";
//   venue =     {
//       id = 12;
//       name = "Cowboys ";
//       slug = "cowboys-nightclub";
//       thumbnail = "https://getqd-beta.s3.amazonaws.com/media/images/venues/cowboys-nightclub/avatars/cowboys-logo.png";
//   };
//}
//
//             */
//            try {
//                /*
//  {"count": 385, "next": "http://beta.getqd.me/api/venue/reservations/all/?page=2", "previous": null, "next_page_number": 2, "previous_page_number": null, "page_number": 1, "num_pages": 20, "next_list": [2, 3, 4, 5, 6, 7, 8, 9, 10, 11], "previous_list": [], "results": [
//                 */
//                 GQLog.dObj(this, "Trying to print JSON. For ticket inventories");
//
//                JSONObject jobj = new JSONObject(JSON);
//                int n;
//                JSONArray jArrayIn = jobj.getJSONArray("inventories");
//                for (n = 0; n < jArrayIn.length(); n = n + 1) {
//
//                    JSONObject inventorie = jArrayIn.getJSONObject(n);
//                    String id = Integer.toString(inventorie.getInt("id"));
//                     GQLog.dObj(this, "Parse event venue id is = " + id);
//                    String name = inventorie.getString("name");
//                     GQLog.dObj(this, "Parse event venue name is = " + name);
//                    String limit_count = Integer.toString(inventorie.getInt("limit_count"));
//                     GQLog.dObj(this, "Parse event venue limit_count is = " + limit_count);
//
//                    Boolean comp = jobj.getBoolean("complimentary_enabled");
//                     GQLog.dObj(this, "Parse comp is = " + comp);
//                    JSONObject owners_permission = inventorie.getJSONObject("owner_permission");
//                    Boolean cash_enabled = owners_permission.getBoolean("cash_enabled");
//                     GQLog.dObj(this, "Parse cash_enabled is = " + cash_enabled);
//                    Boolean cc_enabled = owners_permission.getBoolean("cc_enabled");
//                     GQLog.dObj(this, "Parse cc_enabled is = " + cc_enabled);
//                    Boolean interac_enabled = owners_permission.getBoolean("interac_enabled");
//                     GQLog.dObj(this, "Parse interact_enabled is = " + interac_enabled);
//
///*
//                                  "cash_enabled" = 0;
//               "cc_enabled" = 1;
//               creator = 12;
//               id = 45;
//               "interac_enabled" = 1;
//                    */
//
//                    JSONObject tt = inventorie.getJSONObject("ticket_type");
//                    String ttname = tt.getString("name");
//                     GQLog.dObj(this, "Parse ticket type name is = " + ttname);
//                    String ttprice = tt.getString("price");
//                     GQLog.dObj(this, "Parse ticket type name is = " + ttprice);
//
//                }
//
//                /*
//                JSONObject loginJSONObject = new JSONObject(JSON);
//                GQLog.d("QMyResoActiviy","Parse count = " + loginJSONObject.getString("count"));
//                GQLog.d("QMyResoActiviy","Parse next = " + loginJSONObject.getString("next"));
//                GQLog.d("QMyResoActiviy","Parse previous = " + loginJSONObject.getString("previous"));
//                GQLog.d("QMyResoActiviy","Parse next_page_number = " + loginJSONObject.getString("next_page_number"));
//                GQLog.d("QMyResoActiviy","Parse prev_page_number = " + loginJSONObject.getString("previous_page_number"));
//                GQLog.d("QMyResoActiviy","Parse num_pages = " + loginJSONObject.getString("num_pages"));
//                GQLog.d("QMyResoActiviy","Parse next_list = " + loginJSONObject.getString("next_list"));
//                GQLog.d("QMyResoActiviy","Parse previous_list " + loginJSONObject.getString("previous_list"));
//                JSONArray jArray = loginJSONObject.getJSONArray("results");
//                int n;
//                for(n=0;n<jArray.length();n=n+1) {
//                    JSONObject venue = jArray.getJSONObject(n);
//
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse created " + venue.getString("id"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse name " + venue.getString("name"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse num_of_people " + venue.getString("num_of_people"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse details " + venue.getString("details"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse sms_data " + venue.getString("sms_data"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse spinnerReasons " + venue.getString("spinnerReasons"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse booked_by " + venue.getString("booked_by"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse is_approved " + venue.getString("is_approved"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse arrival_date " + venue.getString("arrival_date"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse location " + venue.getString("location"));
//                    JSONObject jo = venue.getJSONObject("customer");
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse customer.id " + jo.getString("id"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse customer.first_name " + jo.getString("first_name"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse customer.last_name " + jo.getString("last_name"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse customer.username"+ jo.getString("username"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse customer.email " + jo.getString("email"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse customer.gravatar " + jo.getString("gravatar"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse customer.gravatar_small " + jo.getString("gravatar_small"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse customer.phone_number " + jo.getString("phone_number"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse customer.checkin_set " + venue.getString("checkin_set"));
//                    GQLog.d("QMyResoActiviy", "    reservation " + n + " Parse customer.type_text " + venue.getString("type_text"));
//
//                    GQLog.d("QMyResoActiviy", "======================================================");
//                }
//                */
//            } catch (Exception e) {
//                GQLog.d("QMyResoActiviy", "Problem Printing" + e.toString());
//            }
//        }
//    }

    //TODO remove
//    public class MyBaseModelCreateBasket extends GQBaseModel {
//
//        public MyBaseModelCreateBasket(Context context) {
//            super(context);
//        }
//
//
//        public void updateView(Boolean retCode, int code, String JSON) {
//             GQLog.dObj(this, "doing updateView retcode = " + retCode + "Code = " + code);
//            if (retCode == false) {
//                 GQLog.dObj(this, "Unable to Access the Website");
//                return;
//            }
//            if ((code == 200) || (code == 201)) {  //  JSON Looks like the following
//                final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
//                globalVariable.setBasketInfo(JSON);  // save the JSON for future usage
//                try {
//                    JSONObject basket = new JSONObject(JSON);
//                    basketid = Integer.toString(basket.getInt("id"));
//                } catch (Exception e) {
//                     GQLog.dObj(this, "Error getting basket id in create basket response");
//                }
//
//                // PopulateWithJSON(JSON);
//                // printJSON(JSON);  // Looks Like the following
//
//
//            } else if (code == 400) {
//                 GQLog.dObj(this, "Getting 400  Error from the website");
//                // Username or password false, display and an error
//
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQSelectTicketActivity.this);
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
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQSelectTicketActivity.this);
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
//  JSON ={"id": 1329, "shipping_type": 2, "payment_type": 2, "groups": [], "discount": null, "expiry_date": "2015-08-17T18:33:24.046Z", "full_price": 0, "additional_costs": [[1, "Savings", 0, null], [3, "GST", 0, null]], "subtotal": 0, "status": 1, "ticket_basket_product_groups": [], "referred_by": null, "referred_link": null, "payment_gateways": [], "payment_venue": null}               */
//                 GQLog.dObj(this, "Trying to print JSON. For Create Basket");
//                JSONObject basket = new JSONObject(JSON);
//                 GQLog.dObj(this, "====>   basket ID = " + Integer.toString(basket.getInt("id")));
//
///*
//                JSONArray jArray = new JSONArray(JSON);
//
//                int n;
//                for (n = 0; n < jArray.length(); n = n + 1) {
//                    JSONObject event = jArray.getJSONObject(n);
//                    JSONObject venue = event.getJSONObject("venue");
//                     GQLog.dObj(this,"Parse event venue name is = " + venue.getString("name"));
//                    JSONObject eventinfo = event.getJSONObject("event");
//                     GQLog.dObj(this, "Parse event name is = " + eventinfo.getString("name"));
//                     GQLog.dObj(this, "Parse event start time is = " + eventinfo.getString("starts_on"));
//                }
//
//                /*
//
//                */
//            } catch (Exception e) {
//                GQLog.d("QMyResoActiviy", "Problem basket parse Printing" + e.toString());
//            }
//        }
//    }


    //TODO remove
//    public class MyBaseModelUpdateBasket extends GQBaseModel {
//
//        public MyBaseModelUpdateBasket(Context context) {
//            super(context);
//        }
//
//
//        public void updateView(Boolean retCode, int code, String JSON) {
//             GQLog.dObj(this, "doing updateView retcode = " + retCode + "Code = " + code);
//            if (retCode == false) {
//                 GQLog.dObj(this, "Unable to Access the Website");
//                return;
//            }
//            if ((code == 200) || (code == 201)) {  //  JSON Looks like the following
//                final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
//                //globalVariable.setBasketInfo(JSON);  // save the JSON for future usage
//
//                // PopulateWithJSON(JSON);
//                UpdateScreen(JSON);  // Looks Like the following
//
//
//            } else if (code == 400) {
//                GQLog.dObj(this, "Getting 400  Error from the website");
//                // Username or password false, display and an error
//                ErrorOnUpdateBasket();  // fix the basket counts and spinners to right position of old/saved counts.
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQSelectTicketActivity.this);
//                String target = null;
//                try {
//                    JSONObject loginJSONObject = new JSONObject(JSON);
//                    GQLog.dObj(this, "detail = " + loginJSONObject.getString("detail"));
//                    target = loginJSONObject.getString("detail");
//                } catch (Exception e) {
//                    target = JSON;
//                }
//                dlgAlert.setMessage(target);
//                dlgAlert.setTitle(code + " ERROR");
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
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GQSelectTicketActivity.this);
//                String target = null;
//                try {
//                    JSONObject loginJSONObject = new JSONObject(JSON);
//                     GQLog.dObj(this, "Parse id = " + loginJSONObject.getString("detail"));
//                    target = loginJSONObject.getString("detail");
//                } catch (Exception e) {
//                    target = JSON;
//                }
//                dlgAlert.setMessage("Error Message " + code);
//
//                dlgAlert.setTitle(target);
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
//        public void UpdateScreen(String JSON) {
//            String results = new String("0.00");
//            String resultswithfees = new String("0.00");
//            Float totalCost = new Float(0.0);
//            Float serviceCharges = new Float(0.0);
//            try {
//                /*  this is the code from the iphone
//    double totalCost = 0.0;
//    double serviceCharges = 0.0;
//    for(int i=0;i<self.ticketTypes.count;i++){
//        NSDictionary * ticketType = [self.ticketTypes[i] objectForKey:@"ticket_type"];
//        NSNumber * numberOfTicketsForRow = self.ticketNumbersForRows[i];
//        totalCost += [ticketType[@"price"] doubleValue] * [numberOfTicketsForRow doubleValue];
//        if(self.selectedPaymentType == Cash){
//            serviceCharges += [ticketType[@"taxes"] doubleValue] * [numberOfTicketsForRow doubleValue];
//        } else {
//            serviceCharges += ([ticketType[@"service_charges"] doubleValue] + [ticketType[@"taxes"] doubleValue]) * [numberOfTicketsForRow doubleValue];
//        }
//    }
//
//    if(self.selectedPaymentType == Complimentary){
//        self.ticketsCostLabel.text = @"$0.00";
//        self.ticketFeesLabel.text = @"$0.00";
//    } else {
//        self.ticketsCostLabel.text = [NSString stringWithFormat:@"$%.02f", totalCost];
//        self.ticketFeesLabel.text = [NSString stringWithFormat:@"$%.02f", serviceCharges];
//    }
//}
//
//                 */
//                /*
//  JSON ={"id": 1329, "shipping_type": 2, "payment_type": 2, "groups": [], "discount": null, "expiry_date": "2015-08-17T18:33:24.046Z", "full_price": 0, "additional_costs": [[1, "Savings", 0, null], [3, "GST", 0, null]], "subtotal": 0, "status": 1, "ticket_basket_product_groups": [], "referred_by": null, "referred_link": null, "payment_gateways": [], "payment_venue": null}               */
//                JSONObject event = new JSONObject(JSON);
//                JSONArray groupsArray = event.getJSONArray("groups");
//                GQLog.dObj(this, "=================Handling the update basket request.  Number of Groups = " + groupsArray.length());
//                int n;
//                for (n = 0; n < groupsArray.length(); n = n + 1) {
//                    JSONObject group = groupsArray.getJSONObject(n);
//                    results = event.getString("subtotal");
//                    resultswithfees = event.getString("full_price");
//
//                    JSONObject type = group.getJSONObject("type");
//
//
//                    JSONArray tickets_array = group.getJSONArray("tickets");
//                    GQLog.dObj(this, "=================Handling the update basket request.  Number of Tickets = " + tickets_array.length());
//                    int numofticks = tickets_array.length();
//                    String price = type.getString("price");
//                    totalCost = totalCost + (Float.parseFloat(price) * numofticks);
//
//
//                    String sservice_charges = type.getString("service_charges");
//                    String staxes = type.getString("taxes");
//                    GQLog.dObj(this, "service_charges is = " + sservice_charges + "number of tickets =" + numofticks);
//
//                    if (paymentTypeCash == false) {
//                        serviceCharges = serviceCharges + ((Float.parseFloat(sservice_charges)) * numofticks) +
//                                ((Float.parseFloat(staxes)) * numofticks);      // adding in taxes taxes here.
//                    } else {  // if cash no taxes calculated
//                        serviceCharges = serviceCharges + ((Float.parseFloat(sservice_charges)) * numofticks);
//                    }
//
//                    if (paymentTypeComplementary == true) {
//                        totalCost = new Float(0.0);
//                        serviceCharges = new Float(0.0);
//                    }
//                    results = String.getFormat("%.2f", totalCost);
//                    resultswithfees = String.getFormat("%.2f", serviceCharges);
//                }
//                setTotals("$" + results, "$" + resultswithfees);
//
//            } catch (Exception e) {
//                 GQLog.dObj(this, "Problem basket parse Printing" + e.toString());
//                setTotals("$" + results, "$" + resultswithfees);
//            }
//        }
//    }


}
