package com.app.upincode.getqd.activities.frags;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.activities.GQVenueActivity;
import com.app.upincode.getqd.activities.inputs.GenericArrayAdapter;
import com.app.upincode.getqd.databinding.DrawerGetqdVenueItemBinding;
import com.app.upincode.getqd.errors.GQVolleyErrorHandler;
import com.app.upincode.getqd.networking.GQNetworkQueue;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.publik.PublicVenueParser;
import com.app.upincode.getqd.networking.requests.publik.PublicListVenueRequest;

import java.util.Arrays;
import java.util.List;


public class GQMainVenueFragment extends MenuActionFragment {
    ListView listView;
    List<PublicVenueParser> venues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gqmain_fragment_venue, container, false);

        listView = (ListView) rootView.findViewById(R.id.listViewVenue);

        // Start venue detail activity when item is selected
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), GQVenueActivity.class);
                intent.putExtra(GQVenueActivity.VENUE, venues.get(position).toString());
                startActivity(intent);
            }

        });

        //Load list of public venues
        PublicListVenueRequest request = new PublicListVenueRequest(
                GQNetworkUtils.getRequestHeaders(getContext()),
                new Response.Listener<PublicVenueParser[]>() {
                    @Override
                    public void onResponse(PublicVenueParser[] json) {
                        //Success!
                        venues = Arrays.asList(json);
                        populateVenues();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure!
                        new GQVolleyErrorHandler(error).handle(getContext());
                    }
                });
        GQNetworkQueue.getInstance(getContext()).addToRequestQueue(request);

        return rootView;
    }

    /**
     * Initializes the view's list of venues
     */
    public void populateVenues() {
        ListAdapter adapter = new VenueDrawerListAdapter(getActivity(), venues);
        listView.setAdapter(adapter);
    }

    class VenueDrawerListAdapter extends GenericArrayAdapter<PublicVenueParser> {
        public VenueDrawerListAdapter(Context context, List<PublicVenueParser> objects) {
            super(context, objects);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final PublicVenueParser venue = getItem(position);

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                DrawerGetqdVenueItemBinding binding = DrawerGetqdVenueItemBinding.inflate(inflater);
                binding.setVenue(venue);
                view = binding.getRoot();
            }

            return view;
        }
    }
}