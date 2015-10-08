package com.app.upincode.getqd.activities;

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
import com.app.upincode.getqd.activities.inputs.GenericArrayAdapter;
import com.app.upincode.getqd.databinding.SellTicketsSalesItemBinding;
import com.app.upincode.getqd.errors.GQVolleyErrorHandler;
import com.app.upincode.getqd.networking.GQNetworkQueue;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.box_office.BOEventAccessParser;
import com.app.upincode.getqd.networking.requests.box_office.BOListEventAccessRequest;

import java.util.Arrays;
import java.util.List;


public class GQSalesActivity extends GQBaseActivity {
    ListView listView;
    private List<BOEventAccessParser> eventAccesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        setTitle("Events");
        initBackButtonToolbar();

        listView = (ListView) findViewById(R.id.listViewStaff);

        // Start venue event activity when item is selected
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), GQSelectTicketActivity.class);
                intent.putExtra(GQSelectTicketActivity.EVENT_ACCESS, eventAccesses.get(position).toString());
                startActivity(intent);
            }

        });

        //Load list of event accesses
        BOListEventAccessRequest request = new BOListEventAccessRequest(
                GQNetworkUtils.getRequestHeaders(this),
                new Response.Listener<BOEventAccessParser[]>() {
                    @Override
                    public void onResponse(BOEventAccessParser[] json) {
                        //Success!
                        eventAccesses = Arrays.asList(json);
                        populateEventAccesses();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure!
                        new GQVolleyErrorHandler(error).handle(getApplicationContext());
                    }
                });
        GQNetworkQueue.getInstance(this).addToRequestQueue(request);
    }

    public void populateEventAccesses() {
        ListAdapter adapter = new EventAccessDrawerListAdapter(this, eventAccesses);
        listView.setAdapter(adapter);
    }

    class EventAccessDrawerListAdapter extends GenericArrayAdapter<BOEventAccessParser> {
        public EventAccessDrawerListAdapter(Context context, List<BOEventAccessParser> objects) {
            super(context, objects);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final BOEventAccessParser venue = getItem(position);

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                SellTicketsSalesItemBinding binding = SellTicketsSalesItemBinding.inflate(inflater);
                binding.setEventAccess(venue);
                view = binding.getRoot();
            }

            return view;
        }
    }
}
