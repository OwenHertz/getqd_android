package com.app.upincode.getqd.databinding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.widget.EditText;
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
}
