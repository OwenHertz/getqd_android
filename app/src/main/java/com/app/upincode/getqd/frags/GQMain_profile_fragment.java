package com.app.upincode.getqd.frags;


/**
 * Created by herbert on 7/23/2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

//import com.app.upincode.getqd.frags.dummy.DummyContent;

import com.app.upincode.getqd.GQMainActivity;
import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.logging.GQLog;

public class GQMain_profile_fragment extends Fragment {
    private OnButtonClickedForProfileCancel mlistenerProfileCancel;
    private OnButtonClickedForProfileChange mlistenerProfileChange;
    TextView tvName = null;
    TextView tvEmail = null;
    TextView tvPassword = null;
    Activity staff_activity = null;
    boolean smallMode = true;
    Activity profile_activty = null;


    public static GQMain_profile_fragment newInstance() {
        GQMain_profile_fragment fragment = new GQMain_profile_fragment();
        return fragment;
    }

    public GQMain_profile_fragment() {

    }


    GlobalClass globalVariable = null;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        profile_activty = activity;
        ((GQMainActivity) activity).onSectionAttached(1);

        globalVariable = (GlobalClass) activity.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gqmain_fragment_profile, container, false);


        String UserName = globalVariable.getProfileValue(profile_activty.getApplicationContext(), "UserName");
        String Email = globalVariable.getProfileValue(profile_activty.getApplicationContext(), "Email");
        String Password = globalVariable.getProfileValue(profile_activty.getApplicationContext(), "Password");

        tvName = (TextView) rootView.findViewById(R.id.ProfileFirstLastName);
        tvName.setText(UserName);

        tvEmail = (TextView) rootView.findViewById(R.id.ProfileEmail);
        tvEmail.setText(Email);

        tvPassword = (TextView) rootView.findViewById(R.id.ProfilePassword);
        tvPassword.setText(Password);


        final Button button2 = (Button) rootView.findViewById(R.id.DoneProfile);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 GQLog.dObj(this, "Push Button pushed");
                ((OnButtonClickedForProfileChange) profile_activty).OnButtonClickedForProfileChange("Damn it might work");
                // Intent intenty = new Intent(activity.getApplicationContext(),GQStartActivity.class);
                // startActivity(intenty);
            }
        });
        return rootView;
    }

    public interface OnButtonClickedForProfileCancel {
        public void OnButtonClickedForProfileCancel(String herb);
    }

    public interface OnButtonClickedForProfileChange {
        public void OnButtonClickedForProfileChange(String herb);
    }

}
