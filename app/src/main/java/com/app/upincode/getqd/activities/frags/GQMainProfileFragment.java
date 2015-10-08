package com.app.upincode.getqd.activities.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.upincode.getqd.activities.GQActivityUtils;
import com.app.upincode.getqd.databinding.GqmainFragmentProfileBinding;
import com.app.upincode.getqd.errors.GQVolleyErrorHandler;
import com.app.upincode.getqd.networking.GQNetworkQueue;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.generic.CurrentUserParser;
import com.app.upincode.getqd.networking.requests.root.UpdateCurrentUserRequest;


public class GQMainProfileFragment extends MenuActionFragment {
    CurrentUserParser user;
    Button btnUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        GqmainFragmentProfileBinding binding = GqmainFragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        user = getGlobalClass().getCurrentUser();
        binding.setUser(user);

        btnUpdate = (Button) view.findViewById(com.app.upincode.getqd.R.id.DoneProfile);

        // Save user when button is pressed
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });

        return view;
    }

    public void saveUser() {
        UpdateCurrentUserRequest request = new UpdateCurrentUserRequest(
                user, GQNetworkUtils.getRequestHeaders(getContext()),
                new Response.Listener<CurrentUserParser>() {
                    @Override
                    public void onResponse(CurrentUserParser json) {
                        getGlobalClass().setCurrentUser(json);
                        GQActivityUtils.showToast(getContext(), "User info updated succesfully!");
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
    }
}