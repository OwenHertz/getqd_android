package com.app.upincode.getqd.models;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.logging.GQLog;
import com.app.upincode.getqd.utils.GQHttpsClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by herbert on 7/22/2015.
 */
public class GQBaseModel extends AsyncTask<String, Void, Boolean> {
    int statusCode = 0;
    String JSONString = new String("");
    String strError = new String("no Error");
    boolean debug = true;

    final Context context;
    //GlobalClass globalVariable = null;
    //ProgressDialog dialog;
    DefaultHttpClient client = null;

    public GQBaseModel(Context context) {
        super();
        this.context = context;
        client = new GQHttpsClient(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... URLS) {
        Boolean retCode = false;
        boolean doPost = false;
        boolean doGet = false;
        boolean doPut = false;


        client = new GQHttpsClient(context);  // build new Client

        try {
            String BaseURL = new String("https://beta.getqd.com/");
            GlobalClass globalVariable = (GlobalClass) context.getApplicationContext();
            String AUTH = new String("JWT " + globalVariable.getToken());

             GQLog.dObj(this, "URL Looks like =" + BaseURL + URLS[1]);

            if (URLS[0].equals("Post")) doPost = true;
            if (URLS[0].equals("Get")) doGet = true;
            if (URLS[0].equals("Put")) doPut = true;

            if (doPost == true) {
                GQLog.d("GQBaseModel", "Doing Post");
                HttpPost httppostreq = new HttpPost(BaseURL + URLS[1]);

                JSONObject jsonobj = new JSONObject();
                int i = Integer.parseInt(URLS[2]);   // i is the number of  keys and values passed in the URLS arrry
                for (int n = 0; n < i; n = n + 2) {
                    jsonobj.put(URLS[n + 3], URLS[n + 4]);
                    GQLog.dObj(this, "POST SIDE adding key =" + URLS[n + 3] + " value = " + URLS[n + 4]);
                }
                GQLog.dObj(this, "Post sending json =" + jsonobj.toString());


                StringEntity se = null;
                se = new StringEntity(jsonobj.toString());
                se.setContentType("application/json;charset=UTF-8");
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                httppostreq.setEntity(se);

                if ((!(URLS[1].equals("api/login/")))) {  // If this is NOT the initial login we need to setup cookies
                    //dumpCookieStore(globalVariable.getCStore());
                    client.setCookieStore(globalVariable.getCStore());

                    String csrfToken1 = globalVariable.getCsrfToken();
                    if (csrfToken1 != null) {
                        //Log.d("GQBaseMODEl- CSRFTOKEN=", csrfToken1);
                        httppostreq.setHeader("Referer", "https://beta.getqd.me");
                        httppostreq.setHeader("Accept", "application/json");
                        httppostreq.setHeader("X-CSRFToken", csrfToken1);
                        httppostreq.setHeader("User-Agent", "GetQD/300.0,0,4 (IPhone Simulator; IOS 8.4; Scale/2.00)");
                        httppostreq.setHeader("Accept-Language", "en;q=1, fr;q=0.9, de;q=0.8, zh-Hans;g=0.7, zh-Hant;g=0.6,ja=q=0.5");
                    } else {
                        GQLog.dObj(this, " CSRFTOKEN Error  This is big problem.");
                    }
                }
                GQLog.dObj(this, "Doing Post");
                HttpResponse getResponse = client.execute(httppostreq);   // This should do the trick
                GQLog.dObj(this, "Post Status Line: " + getResponse.getStatusLine());

                statusCode = getResponse.getStatusLine().getStatusCode();

                if (statusCode == 400) {
                     GQLog.dObj(this, "getReasonPhrase" + getResponse.getStatusLine().getReasonPhrase());
                     GQLog.dObj(this, "gettoString" + getResponse.getStatusLine().toString());
                }


                if ((statusCode == 200) || (statusCode == 400)) {
                    retCode = true;
                }
                HttpEntity responseEntity = getResponse.getEntity();
                JSONString = EntityUtils.toString(responseEntity);  // put the data in the JSON String

                // NOW Save the Cookies and get the CSRF TOKEN. Then we will process the JSON
                if (URLS[1].equals("api/login/")) {  // Lets get Cookie Store and get the CSRF Token.
                    //dumpCookieStore(client.getCookieStore());
                    globalVariable.setCStoreToken(client.getCookieStore());
                    String csrfToken = null;

                    String CSRFURL = new String("api/csrf/");
                    DefaultHttpClient csrfClient = new GQHttpsClient(context);
                    HttpGet httpcsrfgetreq = new HttpGet(BaseURL + CSRFURL);
                    try {
                        HttpResponse getCsrfResponse = null;
                        GQLog.dObj(this, "BaseModel -Doing CSRF  with" + httpcsrfgetreq.toString());
                        getCsrfResponse = client.execute(httpcsrfgetreq);
                        int statusCodeCSRF = getCsrfResponse.getStatusLine().getStatusCode();
                        if (statusCodeCSRF == 200) {
                            HttpEntity newresponseEntity = getCsrfResponse.getEntity();
                            String JSONStringCSRF = EntityUtils.toString(newresponseEntity);
                            globalVariable.setCsrfToken(JSONStringCSRF);
                        } else {
                             GQLog.dObj(this, "statusCodeCSRF=" + statusCodeCSRF);
                        }
                        GQLog.dObj(this, "statusCodeCSRF=" + statusCodeCSRF);
                    } catch (Exception e) {
                        GQLog.dObj(this, "CSRF failed" + e.toString());
                    }
                }

                retCode = true;
            } else if (doGet == true) {
                GQLog.dObj(this, "Doing Get");
                HttpGet httpgetreq = new HttpGet(BaseURL + URLS[1]);
                JSONObject jsonobj = new JSONObject();
                StringBuffer sb = new StringBuffer();
                int i = Integer.parseInt(URLS[2]);   // i is the number of  keys and values passed in the URLS arrry
                GQLog.dObj(this, "Get Number of Variables is = " + i);
                if (i != 0) {
                    sb.append("?");
                    for (int n = 0; n < i; n = n + 2) {
                        sb.append(URLS[n + 3] + "=" + URLS[n + 4]);
                        GQLog.d("GQBaseModel", " GET SIDE adding key =" + URLS[n + 3] + " value = " + URLS[n + 4]);
                        if (((n + 2) < i)) {
                            sb.append("&");
                        }
                    }
                    GQLog.dObj(this, "GET endcoding user values paramers using URL =" + BaseURL + URLS[1] + sb.toString());
                    httpgetreq = new HttpGet(BaseURL + URLS[1] + sb.toString());
                }
                 GQLog.dObj(this, "GET URL Looks like =" + BaseURL + URLS[1] + sb.toString());
                try {
                    //dumpCookieStore(globalVariable.getCStore());
                    client.setCookieStore(globalVariable.getCStore());

                    HttpResponse getResponse = null;
                    GQLog.dObj(this, "Doing  GET URL as  " + httpgetreq.toString());
                    getResponse = client.execute(httpgetreq);
                    GQLog.dObj(this, "Status Line: " + getResponse.getStatusLine());

                    statusCode = getResponse.getStatusLine().getStatusCode();
                    if ((statusCode == 200) || (statusCode == 201) || (statusCode == 400)) {
                        retCode = true;
                    }
                    int statusCode = getResponse.getStatusLine().getStatusCode();
                    if ((statusCode == 200) || (statusCode == 201)) {
                        HttpEntity responseEntity = getResponse.getEntity();
                        JSONString = EntityUtils.toString(responseEntity);
                    } else {
                        GQLog.dObj(this, "statusCode=" + statusCode);
                    }
                    GQLog.dObj(this, "statusCode=" + statusCode);
                } catch (Exception e) {
                    GQLog.dObj(this, "GET failed" + e.toString());
                }
                retCode = true;
            } else if (doPut == true) {
                GQLog.dObj(this, "Doing Put");
                HttpPut httpputreq = new HttpPut(BaseURL + URLS[1]);
                 GQLog.dObj(this, "PUT URL Looks like =" + BaseURL + URLS[1]);
                JSONObject jsonobj = new JSONObject();
                int i = Integer.parseInt(URLS[2]);   // i is the number of  keys and values passed in the URLS arrry
                for (int n = 0; n < i; n = n + 2) {
                    jsonobj.put(URLS[n + 3], URLS[n + 4]);
                    GQLog.dObj(this, "PUT SIDE adding key to JSON =" + URLS[n + 3] + " value = " + URLS[n + 4]);
                }
                GQLog.dObj(this, "PUT sending json =" + jsonobj.toString());
                StringEntity se = null;
                se = new StringEntity(jsonobj.toString());
                se.setContentType("application/json;charset=UTF-8");
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                httpputreq.setEntity(se);

                if ((!(URLS[1].equals("api/login/")))) {  // If this is NOT the initial login we need to setup cookies
                    //dumpCookieStore(globalVariable.getCStore());
                    client.setCookieStore(globalVariable.getCStore());

                    String csrfToken1 = globalVariable.getCsrfToken();
                    if (csrfToken1 != null) {
                        httpputreq.setHeader("Referer", "https://beta.getqd.me");
                        httpputreq.setHeader("Accept", "application/json");
                        httpputreq.setHeader("X-CSRFToken", csrfToken1);
                        httpputreq.setHeader("User-Agent", "GetQD/300.0,0,4 (IPhone Simulator; IOS 8.4; Scale/2.00)");
                        httpputreq.setHeader("Accept-Language", "en;q=1, fr;q=0.9, de;q=0.8, zh-Hans;g=0.7, zh-Hant;g=0.6,ja=q=0.5");
                    } else {
                        GQLog.dObj(this, " CSRFTOKEN PUT Error  This is big problem.");
                    }
                } else {
                    GQLog.dObj(this, "Problem  put");
                }
                GQLog.dObj(this, "Doing Put");
                HttpResponse getResponse = client.execute(httpputreq);   // This should do the trick
                GQLog.dObj(this, "Status: " + getResponse.getStatusLine());


                statusCode = getResponse.getStatusLine().getStatusCode();
                if ((statusCode == 200) || (statusCode == 400)) {
                    retCode = true;
                }
                HttpEntity responseEntity = getResponse.getEntity();
                JSONString = EntityUtils.toString(responseEntity);  // put the data in the JSON String
                retCode = true;
            }
        } catch (Exception e) {
            GQLog.dObj(this, "background threw main exception =" + e.toString());
        }
        return retCode;
    }

    protected Boolean doInBackgroundOLD(String... URLS) {
        Boolean retCode = false;
        boolean doPost = false;

        try {
            String BaseURL = context.getString(R.string.GQBase_URL);
            GlobalClass globalVariable = (GlobalClass) context.getApplicationContext();
            String AUTH = new String("JWT " + globalVariable.getToken());


            GQLog.d("GQBaseModel", "URL Looks like =" + BaseURL + URLS[1]);

            if (URLS[0].equals("Post")) doPost = true;


            HttpPost httppostreq = new HttpPost(BaseURL + URLS[1]);
            HttpGet httpgetreq = new HttpGet(BaseURL + URLS[1]);

            //Setup the Authorization Token per Jeremy

            //Log.d("TOKEN=",AUTH);
            // httppostreq.setHeader("Authorization",AUTH);
            // httppostreq.setHeader("X-CSRFToken",csrfToken);
            //  httpgetreq.setHeader("Authorization",AUTH);
            if ((!(URLS[1].equals("api/login/")))) {  // If this is NOT the initial login we need to up cookies
                GQLog.d("basemodel.java", "Setting up the cookiesStore for the httpclient");
                dumpCookieStore(globalVariable.getCStore());
                client.setCookieStore(globalVariable.getCStore());  //if static we don;t need to set this up
                if (doPost == true) {
                    GQLog.d("basemodel.java", "Putting in the Csrf Tokens");
                    String csrfToken = globalVariable.getCsrfToken();
                    if (csrfToken != null) {
                        httppostreq.setHeader("X-CSRFToken", csrfToken);
                    } else {
                        GQLog.d("basemodel.java", "What the heck no csrf token");
                    }
                }
            }
            //If there are key values pairs in the URLS array - get them and send them as a string entity
            JSONObject jsonobj = new JSONObject();
            int i = Integer.parseInt(URLS[2]);   // i is the number of  keys and values passed in the URLS arrry
            for (int n = 0; n < i; n = n + 2) {
                jsonobj.put(URLS[n + 3], URLS[n + 4]);
                GQLog.d("GQBaseModel", " adding key =" + URLS[n + 3] + " value = " + URLS[n + 4]);
            }
            GQLog.d("basemodel.java", "sending=" + jsonobj.toString());
            StringEntity se = null;
            se = new StringEntity(jsonobj.toString());
            se.setContentType("application/json;charset=UTF-8");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
            httppostreq.setEntity(se);    // Only add to the post

            // Execute the GET call and obtain the response
            try {
                HttpResponse getResponse = null;
                if (doPost == true) {
                    GQLog.d("Doing Post with", httppostreq.toString());
                    getResponse = client.execute(httppostreq);

                } else {
                    GQLog.d("Doing Get with", httpgetreq.toString());
                    getResponse = client.execute(httpgetreq);

                }
                if (URLS[1].equals("api/login/")) {  // Lets get information about if on initial call
                    dumpCookieStore(client.getCookieStore());
                    globalVariable.setCStoreToken(client.getCookieStore());
                }
                HttpEntity entity = getResponse.getEntity();

                GQLog.d("GQBaseModel", "Login form get: " + getResponse.getStatusLine());
                if (entity != null) {
                    entity.consumeContent();
                }

                statusCode = getResponse.getStatusLine().getStatusCode();
                GQLog.d("GQBaseModel", "statusCode=" + statusCode);
                GQLog.d("BASEMODE", "Checking for api/login1 getResponse=    " + getResponse.toString());
                if ((statusCode == 200) || (statusCode == 400)) {
                    retCode = true;
                    GQLog.d("BASEMODE", "Checking for api/login2");
                    HttpEntity responseEntity = getResponse.getEntity();
                    GQLog.d("BASEMODE", "Checking for api/login3");
                    if (responseEntity != null) {
                        GQLog.d("BASEMODE", "Checking for api/login4");
                        try {
                            InputStream is = responseEntity.getContent();
                            JSONString = convertStreamToString(is);
                            // JSONString = EntityUtils.toString(responseEntity);
                        } catch (Exception e) {
                            GQLog.d("BASEMODE", "Trouble getting JSON String ignore it");
                        }
                        GQLog.d("BASEMODE", "Checking for api/login5");
                    }
                } else {
                    GQLog.d("GQBaseModel", "statusCode=" + statusCode);
                }
                GQLog.d("BASEMODE", "Checking for api/login6");
                /* IF We JUST LOGGED IN  lets get info about myself and get the information about the CSRFTOKEN*/
                if (URLS[1].equals("api/login/")) {  // Lets get information about
                    GQLog.d("BASEMODE", "Attempting to do users me");
                    String MEURL = new String("api/users/me");
                    //String MEURL = new String("api/user/venues");
                    HttpGet httpmegetreq = new HttpGet(BaseURL + MEURL);
                    //httpcsrfgetreq.setHeader("Authorization",AUTH);
                    try {
                        HttpResponse getMeResponse = null;
                        GQLog.d("Doing USERSME GET URL as  ", httpmegetreq.toString());
                        getMeResponse = client.execute(httpmegetreq);
                        int statusCode = getMeResponse.getStatusLine().getStatusCode();
                        if (statusCode == 200) {
                            HttpEntity responseEntity = getMeResponse.getEntity();
                            String MeString = EntityUtils.toString(responseEntity);
                            GQLog.d("ME JSON", MeString);
                            JSONString = MeString;  // pass this to the fragment or activity for parsing
                            globalVariable.setloginJSON(JSONString);
                        } else {
                            GQLog.d("GQBaseModelME", "statusCode=" + statusCode);
                        }
                        GQLog.d("GQBaseModelME", "statusCode=" + statusCode);
                    } catch (Exception e) {
                        GQLog.d("ME failed", e.toString());
                    }
                    //String MEURL = new String("api/users/me");
                    MEURL = new String("api/user/venues");
                    httpmegetreq = new HttpGet(BaseURL + MEURL);
                    //httpcsrfgetreq.setHeader("Authorization",AUTH);
                    try {
                        GQHttpsClient newclient = new GQHttpsClient(context);
                        dumpCookieStore(globalVariable.getCStore());
                        newclient.setCookieStore(globalVariable.getCStore());

                        HttpResponse getMeResponse = null;
                        GQLog.d("Doing USERSME GET URL as  ", httpmegetreq.toString());
                        getMeResponse = newclient.execute(httpmegetreq);
                        int statusCode = getMeResponse.getStatusLine().getStatusCode();
                        if (statusCode == 200) {
                            HttpEntity responseEntity = getMeResponse.getEntity();
                            String MeString = EntityUtils.toString(responseEntity);
                            GQLog.d("USER Venues JSON", MeString);
                            globalVariable.setUserVenuesJSON(MeString);
                        } else {
                            GQLog.d("GQBaseModelME", "statusCode=" + statusCode);
                        }
                        GQLog.d("GQBaseModelME", "statusCode=" + statusCode);
                    } catch (Exception e) {
                        GQLog.d("ME failed", e.toString());
                    }

                    GQLog.d("Base me", "Attempt to set up the csrf token");
                    String csrfToken = null;
                    String CSRFURL = new String("api/csrf/");
                    DefaultHttpClient csrfClient = new GQHttpsClient(context);
                    HttpGet httpcsrfgetreq = new HttpGet(BaseURL + CSRFURL);
                    try {
                        HttpResponse getCsrfResponse = null;
                        GQLog.d("Doing CSRF GET with", httpcsrfgetreq.toString());
                        getCsrfResponse = client.execute(httpcsrfgetreq);
                        int statusCode = getCsrfResponse.getStatusLine().getStatusCode();
                        if (statusCode == 200) {
                            HttpEntity responseEntity = getCsrfResponse.getEntity();
                            String JSONStringCSRF = EntityUtils.toString(responseEntity);
                            globalVariable.setCsrfToken(JSONStringCSRF);
                            csrfToken = JSONStringCSRF;
                            GQLog.d("CsRF Token", csrfToken);
                        } else {
                            GQLog.d("GQBaseModelCSRF", "statusCode=" + statusCode);
                        }
                        GQLog.d("GQBaseModel", "statusCode=" + statusCode);
                    } catch (Exception e) {
                        GQLog.d("CSRF failed", e.toString());
                    }
                    DefaultHttpClient BGClient = new GQHttpsClient(context);
                    HttpPost httpBGgetreq = new HttpPost(BaseURL + "api/reservations/new-staff-reservation-via-mobile/12/");
                    GQLog.d("TryingtoPost to =", BaseURL + "api/reservations/new-staff-reservation-via-mobile/12/");
                    try {
                        HttpResponse getBGResponse = null;
                        GQLog.d("Doing book guest Post with", httpBGgetreq.toString());
                        StringEntity sebg = null;
                        String bookitJSON = new String("{\"booked_by\":\"12\",\"num_of_people\":\"4\",\"email\":\"hrush@yahoo.com\",\"details\":\"here are the dirty details\",\"arrival_date\":\"2015-08-05T02:00:00.000Z\",\"phone_number\":\"9784075822\",\"location\":\"\",\"type\":\"74\",\"time\":\"21:00\",\"first_name\":\"For Lucas\",\"last_name\":\"Rush\"}");
                        sebg = new StringEntity(bookitJSON);
                        GQLog.d("JSON Heading up = ", bookitJSON);
                        sebg.setContentType("application/json;charset=UTF-8");
                        sebg.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                        httpBGgetreq.setEntity(sebg);    // Only add to the post
                        String csrfToken1 = globalVariable.getCsrfToken();
                        if (csrfToken1 != null) {
                            GQLog.d("CSRFTOKEN=", csrfToken1);
                            //String bassicAuth = "Basic" + new String(Base64.encode)
                            httpBGgetreq.setHeader("Referer", "https://beta.getqd.me");
                            httpBGgetreq.setHeader("Accept", "application/json");
                            httpBGgetreq.setHeader("X-CSRFToken", csrfToken1);
                            httpBGgetreq.setHeader("User-Agent", "GetQD/300.0,0,4 (IPhone Simulator; IOS 8.4; Scale/2.00)");
                            httpBGgetreq.setHeader("Accept-Language", "en;q=1, fr;q=0.9, de;q=0.8, zh-Hans;g=0.7, zh-Hant;g=0.6,ja=q=0.5");
                        } else {
                            GQLog.d("babooke", "What the heck no csrf token for book guest");
                        }
                        String credentials = URLS[4] + ":" + URLS[6];
                        GQLog.d("Credentials=", credentials);
                        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

                        //httpBGgetreq.setHeader("Authorization", "Basic " + base64EncodedCredentials);

                        getBGResponse = client.execute(httpBGgetreq);  // bookem

                        int statusCode = getBGResponse.getStatusLine().getStatusCode();
                        if (statusCode == 200) {
                            HttpEntity responseEntity = getBGResponse.getEntity();
                            String JSONStringBG = EntityUtils.toString(responseEntity);
                            GQLog.d("Bookguest response", JSONStringBG);
                        } else {
                            GQLog.d("GbookguestF", "statusCode=" + statusCode);
                        }
                        GQLog.d("GQbookgues", "statusCode=" + statusCode);
                    } catch (Exception e) {
                        GQLog.d("BOOK GUEST failed", e.toString());
                    }
                } else {
                    GQLog.d("GQBaseModel", "Not calling users/me with " + URLS[1]);
                }

            } catch (Exception e) {
                GQLog.d("BaseModel.java", "base model problems is in networking " + e.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            GQLog.d("BaseModel.java", "More Problems " + e.toString());
        }
        return retCode;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public void dumpCookieStore(CookieStore cStore) {
        GQLog.d("GQBaseModel", "dumping cookies  start====================================");
        List<Cookie> cookies = cStore.getCookies();
        if (cookies.isEmpty()) {
            GQLog.d("GQBaseModel", "No Cookies");
        } else {
            for (int j = 0; j < cookies.size(); j++) {
                System.out.println("- " + cookies.get(j).toString());
            }
        }
        GQLog.d("GQBaseModel", "dumping cookies end=====================================");
    }

    @Override
    protected void onPostExecute(Boolean retCode) {  // pass this to activity or fragment
        updateView(retCode, statusCode, JSONString);
    }

    public void updateView(Boolean retCode, int code, String JSON) {
        GQLog.d("GQBaseModel", "activity did not overload the updateview");
        Toast.makeText(context, "GQBaseModel: activity did not overload the updateview", Toast.LENGTH_LONG).show();
    }
}
