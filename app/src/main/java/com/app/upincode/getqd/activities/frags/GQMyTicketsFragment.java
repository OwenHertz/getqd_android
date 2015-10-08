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
import com.app.upincode.getqd.activities.GQTicketsDisplayActivity;
import com.app.upincode.getqd.activities.inputs.GenericArrayAdapter;
import com.app.upincode.getqd.databinding.TicketGroupListItemBinding;
import com.app.upincode.getqd.errors.GQVolleyErrorHandler;
import com.app.upincode.getqd.networking.GQNetworkQueue;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.user_based.UBTicketGroupParser;
import com.app.upincode.getqd.networking.requests.user_based.UBPaginatedListTicketGroupRequest;
import com.app.upincode.getqd.utils.DateTimeUtils;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class GQMyTicketsFragment extends MenuActionFragment {
    ListView listView;
    List<UBTicketGroupParser> ticketGroups;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gqmain_fragment_my_tickets, container, false);

        listView = (ListView) rootView.findViewById(R.id.listViewTickets);

        // Start tickets display activity when ticket group is selected
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), GQTicketsDisplayActivity.class);
                intent.putExtra(GQTicketsDisplayActivity.TICKET_GROUP, ticketGroups.get(position).toString());
                startActivity(intent);
            }

        });

        //TODO paginate results
        // Retrieve ticket groups
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("starts_on__gte", DateTimeUtils.toIsoString(new DateTime()));
        queryParams.put("page_size", "20");
        queryParams.put("ordering", "starts_on");
        UBPaginatedListTicketGroupRequest request = new UBPaginatedListTicketGroupRequest(
                GQNetworkUtils.getRequestHeaders(getContext()), queryParams,
                new Response.Listener<UBTicketGroupParser.PaginationParser>() {
                    @Override
                    public void onResponse(UBTicketGroupParser.PaginationParser json) {
                        //Success!
                        ticketGroups = Arrays.asList(json.results);
                        populateTicketGroupList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure!
                        new GQVolleyErrorHandler(error).handle(getContext());
                    }
                });
        // Add the request to the RequestQueue.
        GQNetworkQueue.getInstance(getContext()).addToRequestQueue(request);

        return rootView;
    }

    /**
     * Initializes the view's ticket group list
     */
    public void populateTicketGroupList() {
        ListAdapter adapter = new TicketGroupDrawerListAdapter(getActivity(), ticketGroups);
        listView.setAdapter(adapter);
    }

    class TicketGroupDrawerListAdapter extends GenericArrayAdapter<UBTicketGroupParser> {
        public TicketGroupDrawerListAdapter(Context context, List<UBTicketGroupParser> objects) {
            super(context, objects);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final UBTicketGroupParser ticketGroup = getItem(position);

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                TicketGroupListItemBinding binding = TicketGroupListItemBinding.inflate(inflater);
                binding.setTicketGroup(ticketGroup);
                view = binding.getRoot();
            }

            return view;
        }
    }
}