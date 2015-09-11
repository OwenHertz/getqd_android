package com.app.upincode.getqd.frags;


/**
 * Created by herbert on 7/23/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.app.upincode.getqd.GQMainActivity;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.models.GQBaseModel;

public class GQMain_logout_fragment extends Fragment {
    private OnButtonClickedForLogout mlistener;
    private OnButtonClickedForNOLogout mlistenerNo;
    Activity logout_activty;
    boolean debug = true;

    boolean smallMode = false;

    public static GQMain_logout_fragment newInstance() {
        GQMain_logout_fragment fragment = new GQMain_logout_fragment();
        return fragment;
    }

    public GQMain_logout_fragment() {


    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        logout_activty = activity;
        if (smallMode == false) {
            ((GQMainActivity) activity).onSectionAttached(6);
        } else {
            ((GQMainActivity) activity).onSectionAttached(3);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.gqmain_fragment_logout, container, false);
        Button btyes = (Button) rootView.findViewById(R.id.onClickFragment_logout_buttonYes);
        btyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 GQLog.dObj(this, "onclick yes (Send website api/auth/logout/");
                try {
                    ((OnButtonClickedForLogout) logout_activty).OnButtonClickedForLogout("Damn it might work");
                    // establish a model and request login.
                    String URL_Logout = new String("api/auth/logout/");
                    MyBaseModel mybm = new MyBaseModel(logout_activty);
                    mybm.execute("Post", URL_Logout, "0");// Instantiate the custom HttpClient
                } catch (ClassCastException cce) {
                     GQLog.dObj(this, "cast problem 1");
                }

            }
        });
        Button btno = (Button) rootView.findViewById(R.id.onClickFragment_logout_buttonNo);
        btno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 GQLog.dObj(this, "onclick no");
                try {
                    ((OnButtonClickedForNOLogout) logout_activty).OnButtonClickedForNOLogout("Damn it might work");
                } catch (ClassCastException cce) {
                     GQLog.dObj(this, "cast problem 2");
                }
            }
        });
        return rootView;
    }

    public interface OnButtonClickedForLogout {
        public void OnButtonClickedForLogout(String herb);
    }

    public interface OnButtonClickedForNOLogout {
        public void OnButtonClickedForNOLogout(String herb);
    }

    public class MyBaseModel extends GQBaseModel {

        public MyBaseModel(Context context) {
            super(context);
        }


        public void updateView(Boolean retCode, int code, String JSON) {
             GQLog.dObj(this, "doing updateView retcode = " + retCode + "Code = " + code);
            if (retCode == false) {
                 GQLog.dObj(this, "Unable to Access the Website");
                return;
            }
            if (code == 200) {  //  JSON Looks like the following
                try {
                    Toast.makeText(logout_activty, JSON, Toast.LENGTH_LONG).show();
                     GQLog.dObj(this, "=========> Received= " + JSON);
                } catch (Exception e) {
                     GQLog.dObj(this, "Trouble parsing the JSON data");
                }
            } else {
                 GQLog.dObj(this, "Getting Error From Website =  " + code);
            }

        }
    }
}
