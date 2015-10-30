package com.app.upincode.getqd.activities.frags;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.activities.GQActivityUtils;
import com.app.upincode.getqd.activities.GQBookGuestActivity;
import com.app.upincode.getqd.activities.GQMyResoActivity;
import com.app.upincode.getqd.activities.GQSalesActivity;
import com.app.upincode.getqd.activities.GQScanEventsActivity;
import com.app.upincode.getqd.activities.GQStatisticsActivity;
import com.app.upincode.getqd.activities.inputs.GenericArrayAdapter;
import com.app.upincode.getqd.databinding.DrawerGetqdStaffItemBinding;
import com.app.upincode.getqd.networking.parsers.user_based.UBVenueParser;
import com.app.upincode.getqd.activities.CaptureActivity;
import com.app.upincode.getqd.networking.parsers.venue_based.VBEventsCheckInScanParser;
import com.app.upincode.getqd.utils.ImageDownloaderTask;

import java.util.Arrays;
import java.util.List;


public class GQMainHomeFragment extends MenuActionFragment {
    ListView listView;
    List<UBVenueParser> venues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gqmain_fragment_home, container, false);
        getActivity().setTitle("Home");
        GlobalClass globalClass = (GlobalClass) getContext().getApplicationContext();

        listView = (ListView) rootView.findViewById(R.id.listViewStaff);
        venues = Arrays.asList(globalClass.getUserVenues());

        Button buttonGetStats = (Button) rootView.findViewById(R.id.buttonStaffGetStats);
        buttonGetStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GQStatisticsActivity.class);
                startActivity(intent);
            }
        });
        Button buttonStaffSellTickets = (Button) rootView.findViewById(R.id.buttonStaffSellTickets);
        buttonStaffSellTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GQSalesActivity.class);
                startActivity(intent);
            }
        });


        this.populateListView();

        return rootView;
    }

    public void populateListView() {
        ListAdapter adapter = new VenueDrawerListAdapter(getActivity(), venues);
        listView.setAdapter(adapter);
    }

    class VenueDrawerListAdapter extends GenericArrayAdapter<UBVenueParser> {
        public VenueDrawerListAdapter(Context context, List<UBVenueParser> objects) {
            super(context, objects);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final UBVenueParser venue = getItem(position);

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                DrawerGetqdStaffItemBinding binding = DrawerGetqdStaffItemBinding.inflate(inflater);
                binding.setVenue(venue);
                view = binding.getRoot();
            }
            ImageView logoView = (ImageView) view.findViewById(R.id.logo);
            if (logoView != null) {
                //new ImageDownloaderTask(logoView).execute(mNavItems.get(position).mURL);
                new ImageDownloaderTask(logoView).execute("https://getqd-beta.s3.amazonaws.com/media/images/venues/ranchmans/avatars/countrylogo.PNG");
            }

            ImageButton iconViewbg = (ImageButton) view.findViewById(R.id.iconbg);
            iconViewbg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Start
                    Intent intent = new Intent(getActivity(), GQBookGuestActivity.class);
                    intent.putExtra(GQBookGuestActivity.VENUE, venue.toString()); // start with the Staff fragment
                    startActivity(intent);
                }
            });

            ImageButton iconViewmr = (ImageButton) view.findViewById(R.id.iconmr);
            iconViewmr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Start
                    Intent intent = new Intent(getActivity(), GQMyResoActivity.class);
                    intent.putExtra(GQMyResoActivity.VENUE, venue.toString()); // start with the Staff fragment
                    startActivity(intent);

                }
            });

            ImageButton iconViewci = (ImageButton) view.findViewById(R.id.iconci);
            iconViewci.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        // Venue looks like JSON
                        GlobalClass globalClass = (GlobalClass) getContext().getApplicationContext();
                        UBVenueParser newVenue = UBVenueParser.fromString(UBVenueParser.class, venue.toString());
                        int venID = newVenue.id;
                        globalClass.setScanVenueID(Integer.toString(venID));   // Currently scanning this venue ID.  We will need later. Somewhere.

                        Intent intent = new Intent(getActivity(), GQScanEventsActivity.class);
                        intent.putExtra(GQMyResoActivity.VENUE, venue.toString()); // start with the Staff fragment
                        startActivity(intent);

                    } catch (Exception e) {
                        Log.d("Herb", "Problem with Camera");
                    }
                }
            });
            return view;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            // requestcode should contain the bar code (need to check the result code first.  BTW,OnActivityResult still not working yet.
            // GOTO THE GETQD WEBSITE WITH VENUE ID and BARCODE and VALIDATE THE Ticket
            GlobalClass globalClass = (GlobalClass) getContext().getApplicationContext();
            String venueID = globalClass.getScanVenueID();
            //  VBEventsCheckInScanParser parser =  new VBEventsCheckInScanParser(Integer.toString(requestCode),Integer.parseInt(venueID));
            Log.d("Herb", "RequestCode3 = " + requestCode);
            Log.d("Herb", "ResultCode3 = " + resultCode);

            //Create request data sent to server
            String VenueID = new String("Cowboys");
            Integer[] venueArray = new Integer[]{1, 2, 3, 4};
            VBEventsCheckInScanParser parser = new VBEventsCheckInScanParser(VenueID, venueArray);

            // Perform request
/*
        VBEventsCheckInScanRequest request = new VBEventsCheckInScanRequest(
                this.venue.id, parser, GQNetworkUtils.getRequestHeaders(this),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(VBEventsTicketHistoryParser json) {
                        //If this is called, server returned 2xx response!

                        int status = json.networkResponse.statusCode;

                        if (status == HttpStatus.SC_CREATED) {
                            // Ticket successfully scanned!
                        } else if (status == HttpStatus.SC_ACCEPTED) {
                            // No ticket found/scanned
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Something went wrong! Server returned 4xx or 5xx error


                         //You may want to handle 'Cannot perform action' errors differently
                        // than other request failures. If so, do something like this:
                       //  if (error.networkResponse != null && error.networkResponse.statusCode == HttpStatus.SC_BAD_REQUEST) {
                         //Special error handler
                       //  }
                       //  else {
                         // Regular response handler
                       //  new GQVolleyErrorHandler(error).handle(GQBookGuestActivity.this);
                      //   }


                        //Use generic error handler to tell the user that something went wrong
                        new GQVolleyErrorHandler(error).handle(GQBookGuestActivity.this);
                    }
                });
        // Add the request to the RequestQueue.
        GQNetworkQueue.getInstance(this).addToRequestQueue(request);
*/
        }
    }
}

