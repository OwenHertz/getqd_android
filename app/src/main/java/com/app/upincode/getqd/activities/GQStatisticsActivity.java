package com.app.upincode.getqd.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.activities.inputs.GenericArrayAdapter;
import com.app.upincode.getqd.databinding.SalesStatsItemBinding;
import com.app.upincode.getqd.errors.GQVolleyErrorHandler;
import com.app.upincode.getqd.networking.GQNetworkQueue;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.user_based.UBEventStatParser;
import com.app.upincode.getqd.networking.requests.user_based.UBPaginatedListEventStatsRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class GQStatisticsActivity extends GQBaseActivity {
    ListView listView;
    List<UBEventStatParser> eventStatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.activity_statistics);

        setTitle(getString(R.string.stats_summary_label));
        initBackButtonToolbar();


        listView = (ListView) findViewById(com.app.upincode.getqd.R.id.SalesStatsListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), GQStatisticsDisplayActivity.class);
                intent.putExtra(GQStatisticsDisplayActivity.EVENT_STAT, eventStatList.get(position).toString());
                startActivity(intent);
            }

        });

        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("page_size", "20");
        queryParams.put("ordering", "starts_on");

        //Load event stat objects
        UBPaginatedListEventStatsRequest request = new UBPaginatedListEventStatsRequest(
                GQNetworkUtils.getRequestHeaders(this), queryParams,
                new Response.Listener<UBEventStatParser.PaginationParser>() {
                    @Override
                    public void onResponse(UBEventStatParser.PaginationParser json) {
                        eventStatList = Arrays.asList(json.results);
                        populateEventStatList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure!
                        new GQVolleyErrorHandler(error).handle(GQStatisticsActivity.this);
                    }
                });
        GQNetworkQueue.getInstance(this).addToRequestQueue(request);
    }

    public void populateEventStatList() {
        EventStatListAdapter adapter = new EventStatListAdapter(this, eventStatList);
        listView.setAdapter(adapter);
    }

    class EventStatListAdapter extends GenericArrayAdapter<UBEventStatParser> {
        public EventStatListAdapter(Context context, List<UBEventStatParser> objects) {
            super(context, objects);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final UBEventStatParser eventStat = getItem(position);

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                SalesStatsItemBinding binding = SalesStatsItemBinding.inflate(inflater);
                binding.setEventStat(eventStat);
                view = binding.getRoot();
            }

            return view;
        }
    }
}
