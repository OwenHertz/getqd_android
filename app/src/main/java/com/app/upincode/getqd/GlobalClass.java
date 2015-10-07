package com.app.upincode.getqd;

/**
 * Created by herbert on 7/21/2015.
 */

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.app.upincode.getqd.activities.GQActivityUtils;
import com.app.upincode.getqd.config.GQConfig;
import com.app.upincode.getqd.config.GQConstants;
import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.networking.parsers.user_based.UBVenueParser;
import com.app.upincode.getqd.networking.parsers.generic.CurrentUserParser;
import com.app.upincode.getqd.utils.DateFormatUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class GlobalClass extends Application {
    private String token = null;
    private String csrfToken = null;
    private String UserVenuesJSON = null;
    private String LoginJSON = null;
    private String AnalyticsEventStats = null;
    private String BoxOfficeEventAccesses = null;
    private String BoxOfficeEventAccessesCurrentSlug = null;
    private String BasketInfo = null;
    private String TicketsTicketGroup = null;
    private String IAMI = null;
    private String SellingRole = null;
    private UBVenueParser[] userVenues;
    private CurrentUserParser currentUser;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize preferences
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();

        DateFormatUtils.init(this);

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
        //TODO remove
        throw new RuntimeException("DONT USE THIS");

//        final GlobalClass globalVariable = GQActivityUtils.getGlobalClass(this);
//        String fileName = globalVariable.getPropertiesFileName();
//        Properties prop = new Properties();
//        OutputStream output = null;
//        try {
//             GQLog.dObj(this, "Set Propertie=" + targetProperty + " to value= " + Value);
//            File propertiesFile = new File(ctxt.getExternalFilesDir(null), fileName);
//            if (!propertiesFile.exists()) {
//                 GQLog.dObj(this, "making a getqd.properties file in Set");
//                propertiesFile.createNewFile();
//            }
//            FileInputStream fis = new FileInputStream(propertiesFile);
//            prop.load(fis);
//
//            // set the properties value
//            prop.setProperty(targetProperty, Value);
//            // save properties to project root folder
//
//            output = new FileOutputStream(propertiesFile);
//            prop.store(output, null);
//
//        } catch (IOException io) {
//            io.printStackTrace();
//        } finally {
//            if (output != null) {
//                try {
//                    output.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
    }

    public String getProfileValue(Context ctxt, String targetProperty) {
        String Value = null;
        final GlobalClass globalVariable = GQActivityUtils.getGlobalClass(this);
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

    /**
     * @deprecated use GQConstants.APP_NAME instead.
     */
    @Deprecated
    public String getName() {

        return GQConstants.APP_NAME;
    }

    /**
     * @deprecated use GQConstants.APP_NAME instead.
     */
    @Deprecated
    public void setName(String aName) {

    }

    //TODO check usage
    @Deprecated
    public String getToken() {
        return token;
    }

    //TODO check usage
    @Deprecated
    public void setToken(String aName) {
        token = aName;
    }

    //TODO check usage
    @Deprecated
    public String getUserVenuesJSON() {


        return UserVenuesJSON;
    }

    //TODO check usage
    @Deprecated
    public void setUserVenuesJSON(String aName) {

        UserVenuesJSON = aName;

    }

    //TODO check usage
    @Deprecated
    public String getloginJSON() {

        return LoginJSON;
    }

    //TODO check usage
    @Deprecated
    public void setloginJSON(String aName) {

        LoginJSON = aName;

    }

    //TODO check usage
    @Deprecated
    public String getCsrfToken() {

        return csrfToken;
    }

    //TODO check usage
    @Deprecated
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

    /**
     * @deprecated use GQConstants.PROPERTIES_FILE_NAME instead.
     */
    @Deprecated
    public void setPropertiesFileName(String aName) {
    }

    /**
     * @deprecated use GQConstants.PROPERTIES_FILE_NAME instead.
     */
    @Deprecated
    public String getPropertiesFileName() {
        return GQConstants.PROPERTIES_FILE_NAME;
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

    public void setUserVenues(UBVenueParser[] userVenues) {
        this.userVenues = userVenues;
    }

    public UBVenueParser[] getUserVenues() {
        return userVenues;
    }

    public void setCurrentUser(CurrentUserParser currentUser) {
        this.currentUser = currentUser;
    }

    public CurrentUserParser getCurrentUser() {
        return this.currentUser;
    }
}
