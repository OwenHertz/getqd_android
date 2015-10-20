package com.app.upincode.getqd.activities.frags;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.activities.GQActivityUtils;
import com.app.upincode.getqd.activities.GQBookGuestActivity;
import com.app.upincode.getqd.activities.GQMyResoActivity;
import com.app.upincode.getqd.activities.GQSalesActivity;
import com.app.upincode.getqd.activities.GQStatisticsActivity;
import com.app.upincode.getqd.activities.inputs.GenericArrayAdapter;
import com.app.upincode.getqd.databinding.DrawerGetqdStaffItemBinding;
import com.app.upincode.getqd.networking.parsers.user_based.UBVenueParser;
import com.app.upincode.getqd.activities.CaptureActivity;

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
                    //GQActivityUtils.showAlert(getActivity(),
                      //      "Not Supported", "Scan Check In is not supported at this time");
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    intent.putExtra(GQMyResoActivity.VENUE, venue.toString()); // start with the Staff fragment
                    startActivity(intent);
                }
            });
            return view;
        }
    }
}