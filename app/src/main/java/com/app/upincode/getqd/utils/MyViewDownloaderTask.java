package com.app.upincode.getqd.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.app.upincode.getqd.logging.GQLog;

import org.apache.http.HttpStatus;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by herbert on 8/15/2015.
 */
public class MyViewDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    boolean debug = true;
    private final WeakReference<View> ViewReference;

    public MyViewDownloaderTask(View view) {
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
                    Drawable dr = new BitmapDrawable(bitmap);
                    theView.setBackground(dr);
                    //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    //theView.setImageBitmap(bitmap);


                } else {
                    GQLog.d("sdf", "trouble with bitmap");
                    //Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.tickets_icon);
                    //imageView.setImageDrawable(placeholder);
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

