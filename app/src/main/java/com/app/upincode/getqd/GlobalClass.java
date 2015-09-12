package com.app.upincode.getqd;

/**
 * Created by herbert on 7/21/2015.
 */

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.app.upincode.getqd.config.GQConfig;
import com.app.upincode.getqd.logging.GQLog;

import org.apache.http.client.CookieStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class GlobalClass extends Application {
    private String name = null;
    private String email = null;
    private String password = null;
    private String token = null;
    private String csrfToken = null;
    private String UserVenuesJSON = null;
    private String LoginJSON = null;
    private CookieStore cStore = null;
    private String AnalyticsEventStats = null;
    private String BoxOfficeEventAccesses = null;
    private String BoxOfficeEventAccessesCurrentSlug = null;
    private String BasketInfo = null;
    private String PropertiesFileName = null;
    private String TicketsTicketGroup = null;
    private String IAMI = null;
    private String SellingRole = null;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize preferences
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();

        try {
            GQConfig.initialize(assetManager);
        } catch (IOException e) {
            //Couldn't initialize settings for some reason! Close app
            System.err.println("Could not initialize app settings!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /*  These are the profile fields for the User.
    LoginID
    UserName
    Email
    MobilePhone
    Password
    PostalCode
    City
    CC1
    CC2

    ---Addtionally
    MajorProblemID
    LoggedIn
    StartTarget
     */
    public void setProfileValue(Context ctxt, String targetProperty, String Value) {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        String fileName = globalVariable.getPropertiesFileName();
        Properties prop = new Properties();
        OutputStream output = null;
        try {
             GQLog.dObj(this, "Set Propertie=" + targetProperty + " to value= " + Value);
            File propertiesFile = new File(ctxt.getExternalFilesDir(null), fileName);
            if (!propertiesFile.exists()) {
                 GQLog.dObj(this, "making a getqd.properties file in Set");
                propertiesFile.createNewFile();
            }
            FileInputStream fis = new FileInputStream(propertiesFile);
            prop.load(fis);

            // set the properties value
            prop.setProperty(targetProperty, Value);
            // save properties to project root folder

            output = new FileOutputStream(propertiesFile);
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public String getProfileValue(Context ctxt, String targetProperty) {
        String Value = null;
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        String fileName = globalVariable.getPropertiesFileName();
        Properties prop = new Properties();
        OutputStream output = null;
        try {
             GQLog.dObj(this, "Get Propertie=" + targetProperty);

            File propertiesFile = new File(ctxt.getExternalFilesDir(null), fileName);
            if (!propertiesFile.exists()) {
                 GQLog.dObj(this, "making a getqd.properties file in Get");
                propertiesFile.createNewFile();
                return (Value);
            }
            FileInputStream fis = new FileInputStream(propertiesFile);
            prop.load(fis);
            Value = prop.getProperty(targetProperty);
            return (Value);
        } catch (FileNotFoundException io) {
            io.printStackTrace();
            return (Value);
        } catch (IOException io) {
            io.printStackTrace();
            return (Value);

        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return (Value);
        }
    }

    public String getName() {

        return name;
    }

    public void setName(String aName) {

        name = aName;

    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String newEmail) {
        GQLog.d("GlobalClass", "Setting the Email=" + newEmail);
        email = newEmail;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String newPassword) {
        GQLog.d("GlobalClass", "Setting the Password=" + newPassword);
        password = newPassword;
    }

    public String getToken() {

        return token;
    }

    public void setToken(String aName) {

        token = aName;

    }


    public String getUserVenuesJSON() {

        return UserVenuesJSON;
    }

    public void setUserVenuesJSON(String aName) {

        UserVenuesJSON = aName;

    }

    public String getloginJSON() {

        return LoginJSON;
    }

    public void setloginJSON(String aName) {

        LoginJSON = aName;

    }

    public CookieStore getCStore() {

        return cStore;
    }

    public void setCStoreToken(CookieStore aName) {


        cStore = aName;

    }

    public String getCsrfToken() {

        return csrfToken;
    }

    public void setCsrfToken(String aName) {
        //Log.d("GlobalClass", "SetCsrfToken to" + aName);

        csrfToken = aName;

    }

    public String getAnalyticsEventStats() {

        return AnalyticsEventStats;
    }

    public void setAnalyticsEventStats(String aName) {
        //Log.d("GlobalClass", "SetCsrfToken to" + aName);

        AnalyticsEventStats = aName;

    }

    public String getBoxOfficeEventAccesses() {

        return BoxOfficeEventAccesses;
    }

    public void setBoxOfficeEventAccesses(String aName) {
        //Log.d("GlobalClass", "SetCsrfToken to" + aName);

        BoxOfficeEventAccesses = aName;

    }

    public String getBoxOfficeEventAccessesCurrentSlug() {

        return BoxOfficeEventAccessesCurrentSlug;
    }

    public void setBoxOfficeEventAccessesCurrentSlug(String aName) {
        //Log.d("GlobalClass", "SetCsrfToken to" + aName);

        BoxOfficeEventAccessesCurrentSlug = aName;


    }

    public void setBasketInfo(String aName) {
        //Log.d("GlobalClass", "SetCsrfToken to" + aName);

        BasketInfo = aName;

    }

    public String getBasketInfo() {

        return BasketInfo;
    }

    public void setPropertiesFileName(String aName) {
        //Log.d("GlobalClass", "SetCsrfToken to" + aName);

        PropertiesFileName = aName;

    }

    public String getPropertiesFileName() {

        return PropertiesFileName;
    }

    public void setTicketsTicketGroup(String aName) {
        //Log.d("GlobalClass", "SetCsrfToken to" + aName);

        TicketsTicketGroup = aName;

    }


    public String getTicketsTicketGroup() {

        return TicketsTicketGroup;
    }

    public void setIAMI(String aName) {
        //Log.d("GlobalClass", "SetCsrfToken to" + aName);

        IAMI = aName;

    }


    public String getIAMI() {

        return IAMI;
    }

    public void setSellingRole(String aName) {
        //Log.d("GlobalClass", "SetCsrfToken to" + aName);

        SellingRole = aName;

    }


    public String getSellingRole() {

        return SellingRole;
    }
}
