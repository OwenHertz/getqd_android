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
import com.app.upincode.getqd.activities.GQSelectTicketActivity;
import com.app.upincode.getqd.activities.GQVenueActivity;
import com.app.upincode.getqd.activities.inputs.GenericArrayAdapter;
import com.app.upincode.getqd.databinding.SalesItemBinding;
import com.app.upincode.getqd.errors.GQVolleyErrorHandler;
import com.app.upincode.getqd.networking.GQNetworkQueue;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.publik.PublicEventParser;
import com.app.upincode.getqd.networking.requests.publik.PublicListEventRequest;

import java.util.Arrays;
import java.util.List;


public class GQMainEventsFragment extends MenuActionFragment {
    ListView listView;
    private List<PublicEventParser> events;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gqmain_fragment_event, container, false);

        listView = (ListView) rootView.findViewById(R.id.listViewEvents);


        // Start venue event activity when item is selected
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), GQSelectTicketActivity.class);
                intent.putExtra(GQVenueActivity.VENUE, events.get(position).toString());
                startActivity(intent);
            }

        });

        //Load list of public events
        PublicListEventRequest request = new PublicListEventRequest(
                GQNetworkUtils.getRequestHeaders(getContext()),
                new Response.Listener<PublicEventParser[]>() {
                    @Override
                    public void onResponse(PublicEventParser[] json) {
                        //Success!
                        events = Arrays.asList(json);
                        populateEvents();
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
     * Initializes the view's list of events
     */
    public void populateEvents() {
        ListAdapter adapter = new EventDrawerListAdapter(getActivity(), events);
        listView.setAdapter(adapter);
    }

    class EventDrawerListAdapter extends GenericArrayAdapter<PublicEventParser> {
        public EventDrawerListAdapter(Context context, List<PublicEventParser> objects) {
            super(context, objects);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final PublicEventParser venue = getItem(position);

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                SalesItemBinding binding = SalesItemBinding.inflate(inflater);
                binding.setEvent(venue);
                view = binding.getRoot();
            }

            return view;
        }
    }
}