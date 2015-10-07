package com.app.upincode.getqd.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.app.upincode.getqd.logging.GQLog;

import com.app.upincode.getqd.networking.HttpStatus;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by herbert on 8/15/2015.
 */
//TODO remove?
public class MyViewDownloaderTaskSalesActivity extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<View> ViewReference;

    public MyViewDownloaderTaskSalesActivity(View view) {
        ViewReference = new WeakReference<View>(view);
        //Log.d("GQMain_staff_fragment.ImageDownloader", "construction");
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (ViewReference != null) {
            View theView = ViewReference.get();
            if (theView != null) {
                if (bitmap != null) {
                    // we get the width and Height of the View.
                    int outWidth = theView.getWidth();
                    int outHeight = theView.getHeight();
                     GQLog.dObj(this, "View Height is " + outHeight);
                     GQLog.dObj(this, "View outWidth is " + outWidth);
                    outHeight = (outHeight * 100) / 50;
                     GQLog.dObj(this, "Adjusting View outHeight to " + outHeight);

                    int inWidth = bitmap.getWidth();
                    int inHeight = bitmap.getHeight();
                    Bitmap resizedBitmap = null;
                     GQLog.dObj(this, " inHeight is " + inHeight);
                     GQLog.dObj(this, " inWidth is " + inWidth);
                    // if the bitmap is smaller than the view (we just simple resize it to the view)
                    if ((outHeight > inHeight) && (outWidth > inWidth)) {
                        resizedBitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
                        // if the bitmap Height is greater the View Height AND the bitmap width is less than the view width
                    } else if ((inHeight >= outHeight) && (outWidth > inWidth)) {
                        Bitmap DestBitmap = Bitmap.createBitmap(bitmap,
                                0,
                                (inHeight - outHeight) / 2,
                                inWidth,
                                outHeight
                        );
                        resizedBitmap = Bitmap.createScaledBitmap(DestBitmap, outWidth, outHeight, false);
                    } else if ((outHeight > inHeight) && (inWidth >= outWidth)) {
                        Bitmap DestBitmap = Bitmap.createBitmap(bitmap,
                                (inWidth - outWidth) / 2,
                                0,
                                outWidth,
                                inHeight
                        );
                        resizedBitmap = Bitmap.createScaledBitmap(DestBitmap, outWidth, outHeight, false);
                    } else { // bitmap is bigger than view we cut to the center
                        int maxSize = 5;
                        if ((inWidth / outWidth) < maxSize) {
                            resizedBitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
                        } else {// get the middle 3rd
                            int factor = 3;
                            Bitmap DestBitmap = Bitmap.createBitmap(bitmap,
                                    (inWidth / factor),
                                    (inHeight / factor),
                                    inWidth / factor,
                                    inHeight / factor

                            );
                            resizedBitmap = Bitmap.createScaledBitmap(DestBitmap, outWidth, outHeight, false);
                        }
                    }

                     GQLog.dObj(this, "Imagedownloader  ===>" + " New Width=" + resizedBitmap.getWidth() + " New Height=" + resizedBitmap.getHeight());
                    theView.getLayoutParams().width = resizedBitmap.getWidth();
                    theView.getLayoutParams().height = resizedBitmap.getHeight();

                    Drawable dr = new BitmapDrawable(resizedBitmap);
                    theView.setBackground(dr);

                } else {
                     GQLog.dObj(this, "trouble with bitmap transformation");
                }
            }

        }
    }

    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        //Log.d("GQMain_staff_fragment.ImageDownloader", "Down loadinging image from " + url);
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
             GQLog.wObj(this, "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}

