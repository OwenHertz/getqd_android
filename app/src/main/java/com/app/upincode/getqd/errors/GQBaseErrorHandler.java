package com.app.upincode.getqd.errors;

import android.content.Context;

import com.app.upincode.getqd.activities.GQActivityUtils;

/**
 * Created by jpnauta on 15-09-17.
 */
public abstract class GQBaseErrorHandler {
    /**
     * Performs the necessary actions for the error that has occurred
     * @param activity the current activity
     */
    public abstract void handle(Context context);

    public abstract String getMessage();

    /**
     * Shows an alert dialog to the user
     * @param activity the activity to display for
     * @param title the title to display
     * @param message the message to display
     */
    public void showAlert(Context context, String title, String message) {
        GQActivityUtils.showAlert(context, title, message);
    }
}
