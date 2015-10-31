/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.client.zxing.google.com.myscanningapplication;
//import android.client.zxing.google.com.myscanningapplication.CaptureActivity;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.activities.CaptureActivity;
import android.content.ActivityNotFoundException;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.provider.Browser;

import com.app.upincode.getqd.activities.GQActivityUtils;
import com.app.upincode.getqd.activities.GQScanEventsActivity;
import com.app.upincode.getqd.errors.GQUnexpectedErrorHandler;
import com.app.upincode.getqd.errors.GQVolleyErrorHandler;
import com.app.upincode.getqd.networking.GQNetworkQueue;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.HttpStatus;
import com.app.upincode.getqd.networking.parsers.venue_based.VBEventsCheckInScanParser;
import com.app.upincode.getqd.networking.parsers.venue_based.VBEventsTicketHistoryParser;
import com.app.upincode.getqd.networking.requests.venue_based.VBEventsCheckInScanRequest;
import com.app.upincode.getqd.utils.IntentIntegrator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import android.client.zxing.google.com.myscanningapplication.camera.CameraManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.Map;

import com.app.upincode.getqd.R;

import org.json.JSONException;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {

  private static final String TAG = CaptureActivityHandler.class.getSimpleName();

  private final CaptureActivity activity;
  private final DecodeThread decodeThread;
  private State state;
  private final CameraManager cameraManager;

  private enum State {
    PREVIEW,
    SUCCESS,
    DONE
  }

  public CaptureActivityHandler(CaptureActivity activity,
                         Collection<BarcodeFormat> decodeFormats,
                         Map<DecodeHintType,?> baseHints,
                         String characterSet,
                         CameraManager cameraManager) {
    this.activity = activity;
    decodeThread = new DecodeThread(activity, decodeFormats, baseHints, characterSet,
        new ViewfinderResultPointCallback(activity.getViewfinderView()));
    decodeThread.start();
    state = State.SUCCESS;

    // Start ourselves capturing previews and decoding.
    this.cameraManager = cameraManager;
    cameraManager.startPreview();
    restartPreviewAndDecode();
  }

  @Override
  public void handleMessage(Message message) {



    switch (message.what) {
      case R.id.restart_preview:
        restartPreviewAndDecode();
        break;
      case R.id.decode_succeeded:
        state = State.SUCCESS;
        Bundle bundle = message.getData();
        Bitmap barcode = null;
        float scaleFactor = 1.0f;
        if (bundle != null) {
          byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
          if (compressedBitmap != null) {
            barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
            // Mutable copy:
            barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
          }
          scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
        }
        final GlobalClass globalVariable = GQActivityUtils.getGlobalClass(activity);
        String VenueID = globalVariable.getScanVenueID();
        String EventID = globalVariable.getScanEventID();
        String BarCode = message.obj.toString();

        Log.d("Herb", "VenID=" +VenueID+ " EventID="+EventID+ "  BarCode=" + BarCode);
        ScanIt(VenueID,EventID,BarCode);
       // activity.onHerbResume();  // reinit the camera
        activity.handleDecode((Result) message.obj, barcode, scaleFactor);
        break;
      case R.id.decode_failed:
        // We're decoding as fast as possible, so when one decode fails, start another.
        state = State.PREVIEW;
        cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
        break;
      case R.id.return_scan_result:
        activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
        activity.finish();
        break;
      case R.id.launch_product_query:
        String url = (String) message.obj;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setData(Uri.parse(url));

        ResolveInfo resolveInfo =
            activity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String browserPackageName = null;
        if (resolveInfo != null && resolveInfo.activityInfo != null) {
          browserPackageName = resolveInfo.activityInfo.packageName;
          Log.d(TAG, "Using browser in package " + browserPackageName);
        }

        // Needed for default Android browser / Chrome only apparently
        if ("com.android.browser".equals(browserPackageName) || "com.android.chrome".equals(browserPackageName)) {
          intent.setPackage(browserPackageName);
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          intent.putExtra(Browser.EXTRA_APPLICATION_ID, browserPackageName);
        }

        try {
          activity.startActivity(intent);
        } catch (ActivityNotFoundException ignored) {
          Log.w(TAG, "Can't find anything to handle VIEW of URI " + url);
        }
        break;
    }
  }
  VBEventsCheckInScanRequest request = null;
  public void ScanIt(String VenueID, String EventID, String BarCode ) {
    Integer[] eventArray = new Integer[1];
    eventArray[0] = new Integer(EventID);
    VBEventsCheckInScanParser parser = new VBEventsCheckInScanParser(BarCode, eventArray);
    int venueid = Integer.parseInt(VenueID);
    // Perform request

    request = new VBEventsCheckInScanRequest(
            venueid, parser, GQNetworkUtils.getRequestHeaders(activity),
            new Response.Listener<VBEventsTicketHistoryParser>() {
              @Override
              public void onResponse(VBEventsTicketHistoryParser json) {
                //If this is called, server returned 2xx response!
                int status=request.networkResponse.statusCode;
                if(request == null ) {
                  Log.d("Herb Internal","null json");
                } else if(request.networkResponse == null) {
                  Log.d("Herb Internal","null json.networkResponse");
                } else if(request.networkResponse.statusCode == 0) {
                  Log.d("Herb Internal","0 json.networkResponse.statusCode");
                } else {
                  status =request.networkResponse.statusCode;
                }
                ;
                // Ring a bell or something
                //VolleyError theError = Response.

                if (status == HttpStatus.SC_CREATED) {
                  // Ticket successfully scanned!
                  Toast toast = Toast.makeText(activity.getApplicationContext(), "Ticket is Good!", Toast.LENGTH_LONG);
                  TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                  v.setTextColor(Color.GREEN);
                  v.setTextSize(30);
                  toast.show();
                  ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                  toneG.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING, 200);

                } else if (status == HttpStatus.SC_ACCEPTED) {
                  // No ticket found/scanned

                  Toast toast = Toast.makeText(activity.getApplicationContext(), "Ticket not found!", Toast.LENGTH_LONG);
                  TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                  v.setTextColor(Color.RED);
                  v.setTextSize(30);
                  toast.show();
                  ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                  toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);

                } else if (status == 0) {
                  Toast toast = Toast.makeText(activity.getApplicationContext(), "Testing Toast Here", Toast.LENGTH_LONG);
                  TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                  v.setTextColor(Color.GREEN);
                  v.setTextSize(30);
                  toast.show();
                  ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                  toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                }
              }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (GQNetworkUtils.isServerValidationError(error)) {
                        //Something went wrong! Server returned 4xx or 5xx error
                        try {
                            // Parse message from error response
                            String message = GQNetworkUtils.parseGQJsonErrorMessage(error.networkResponse);

                            //Show message to user
                            Toast toast = Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            v.setTextColor(Color.RED);
                            v.setTextSize(30);
                            toast.show();
                            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);

                        } catch (JSONException e) {
                            //Unexpected error!
                            new GQUnexpectedErrorHandler(e).handle(activity);
                        }
                    } else {
                        //Unexpected error!
                        new GQVolleyErrorHandler(error).handle(activity);
                    }
                }
            });
    // Add the request to the RequestQueue.
    GQNetworkQueue.getInstance(activity).addToRequestQueue(request);
  }
  public void quitSynchronously() {
    state = State.DONE;
    cameraManager.stopPreview();
    Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
    quit.sendToTarget();
    try {
      // Wait at most half a second; should be enough time, and onPause() will timeout quickly
      decodeThread.join(500L);
    } catch (InterruptedException e) {
      // continue
    }

    // Be absolutely sure we don't send any queued up messages
    removeMessages(R.id.decode_succeeded);
    removeMessages(R.id.decode_failed);
  }

  private void restartPreviewAndDecode() {
    if (state == State.SUCCESS) {
      state = State.PREVIEW;
      cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
      activity.drawViewfinder();
    }
  }

}
