package com.app.upincode.getqd.activities.frags;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.app.upincode.getqd.GlobalClass;

public class MenuActionFragment extends Fragment {
    public GlobalClass getGlobalClass() {
        return (GlobalClass) getContext().getApplicationContext();
    }

    /**
     * Performs the action when the fragment is selected. Default action is to display this fragment,
     * but can be overridden to do other actions (e.g. change intent)
     */
    public void performChange(AppCompatActivity activity, FragmentManager fragmentManager, int containerBody) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerBody, this);
        fragmentTransaction.commit();
    }
}
