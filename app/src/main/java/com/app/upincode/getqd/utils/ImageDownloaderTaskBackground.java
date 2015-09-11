package com.app.upincode.getqd.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.app.upincode.getqd.R;
import com.app.upincode.getqd.logging.GQLog;

import org.apache.http.HttpStatus;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by herbert on 7/29/2015.
 */
public class ImageDownloaderTaskBackground extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;

    public ImageDownloaderTaskBackground(ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
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

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {

                if (bitmap != null) {
                    final int maxSize = 960;
                    int outWidth = imageView.getWidth();
                    int outHeight = imageView.getHeight();
                    outHeight = outHeight / 2;  // make it only half the size
                    int inWidth = bitmap.getWidth();
                    int inHeight = bitmap.getHeight();
                    /*
                    if(inWidth > inHeight){
                        outWidth = maxSize;
                        outHeight = (inHeight * maxSize) / inWidth;
                    } else {
                        outHeight = maxSize;
                        outWidth = (inWidth * maxSize) / inHeight;
                    }
                    */
                    GQLog.d("Imagedownloader  ===>", " outWidth=" + outWidth +
                                    " outHeight=" + outHeight +
                                    " inWidth=" + inWidth +
                                    " inHeight=" + inHeight
                    );
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
                    GQLog.d("Imagedownloader  ===>", " New Width=" + resizedBitmap.getWidth() + " New Height=" + resizedBitmap.getHeight());
                    imageView.getLayoutParams().width = resizedBitmap.getWidth();
                    imageView.getLayoutParams().height = resizedBitmap.getHeight();
                    /*
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int newWidth=imageView.getWidth();
                    int newHeight = imageView.getHeight();

                    float scaleWidth = ((float) newWidth)/width;
                    float scaleHeight = ((float) newHeight)/height;

                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth,scaleHeight);

                    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
                    BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);
                    //imageView.setImageBitmap(bmd);
                    */

                    //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    //bitmap = Bitmap.createScaledBitmap(bitmap,800,100,true);
                    Drawable dr = new BitmapDrawable(resizedBitmap);
                    //BitmapDrawable ob = new BitmapDrawable()
                    imageView.setBackground(dr);


                } else {
                    Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.tickets_icon);
                    imageView.setImageDrawable(placeholder);
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
            Log.w("GQMain_staff_fragment.ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}

