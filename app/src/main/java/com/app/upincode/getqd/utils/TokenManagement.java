package com.app.upincode.getqd.utils;

import android.content.Context;
import android.util.Log;

import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.models.GQBaseModel;

import org.json.JSONObject;

/**
 * Created by herbert on 7/29/2015.
 */
public class TokenManagement {
    final Context context;

    public TokenManagement(Context context) {
        this.context = context;
    }

    public void getToken() {
        String URL_LoginAuthFresh = new String("api/auth/jwt/refresh/");
        String URL_LoginAuth = new String("api/auth/jwt/");
        String URL_Login = new String("api/login/");
        MyBaseModel mybm = new MyBaseModel(context);
        mybm.execute("Post", URL_LoginAuthFresh, "0");// Instantiate the custom HttpClient
    }


    public class MyBaseModel extends GQBaseModel {

        public MyBaseModel(Context context) {
            super(context);
        }


        public void updateView(Boolean retCode, int code, String JSON) {
            if (retCode == false) {
                GQLog.d("TokenManagement", "Unable to Access the Website");
                return;
            }
            if (code == 200) {  //  JSON Looks like the following
                setupTokenAccessFromJson(JSON);
            }
        }
    }

    public void setupTokenAccessFromJson(String JSON) {
        try {
            JSONObject resobj = new JSONObject(JSON);
            JSONObject loginJSONObject = new JSONObject(JSON);
            GQLog.d("TokenManagement", "Parsing the JSON return from login");
            final GlobalClass globalVariable = (GlobalClass) context;
            String Token = loginJSONObject.getString("token");
            globalVariable.setToken(Token);
            GQLog.d("TokenManagement", "new token=" + Token);
        } catch (Exception e1) {
            GQLog.d("TokenManagement", e1.toString());
        }
        return;
    }
}
