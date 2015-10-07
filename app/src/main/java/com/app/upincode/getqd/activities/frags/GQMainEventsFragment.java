package com.app.upincode.getqd.activities.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.app.upincode.getqd.R;


public class GQMainEventsFragment extends MenuActionFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gqmain_fragment_event, container, false);

        return rootView;
    }
}