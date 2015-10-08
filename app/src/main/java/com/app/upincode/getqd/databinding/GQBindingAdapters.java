package com.app.upincode.getqd.databinding;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.app.upincode.getqd.networking.GQNetworkQueue;

public class GQBindingAdapters {
    /**
     * Binding adapter to load images
     */
    @BindingAdapter(value = {"android:src"})
    public static void setImageUrl(final ImageView view, String url) {
        ImageLoader imageLoader = GQNetworkQueue.getInstance(view.getContext()).getImageLoader();

        imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                view.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    /**
     * Binding adapter to load images
     */
    @BindingAdapter(value = {"android:background"})
    public static void setBGImageUrl(final View view, String url) {
        ImageLoader imageLoader = GQNetworkQueue.getInstance(view.getContext()).getImageLoader();

        imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Drawable d = new BitmapDrawable(view.getResources(), response.getBitmap());
                view.setBackground(d);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    /**
     * Allow binding {@link Bitmap} to {@link ImageView}
     */
    @BindingAdapter(value = {"android:src"})
    public static void setImageBitmap(final ImageView view, Bitmap bm) {
        view.setImageBitmap(bm);
    }

    /**
     * Allow binding {@link Bitmap} to background of {@link Layout}
     */
    @BindingAdapter(value = {"android:background"})
    public static void setBGImageBitmap(final View view, Bitmap bm) {
        Drawable d = new BitmapDrawable(view.getResources(), bm);
        view.setBackground(d);
    }
}
