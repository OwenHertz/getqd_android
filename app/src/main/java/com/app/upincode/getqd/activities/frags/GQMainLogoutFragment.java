package com.app.upincode.getqd.activities.frags;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.app.upincode.getqd.activities.GQConfirmLogoutActivity;


public class GQMainLogoutFragment extends MenuActionFragment {
    public void performChange(AppCompatActivity activity, FragmentManager fragmentManager, int containerBody) {
        //Instead of showing fragment, change to confirm logout intent
        Intent intent = new Intent(activity, GQConfirmLogoutActivity.class);
        activity.startActivity(intent);
    }
}