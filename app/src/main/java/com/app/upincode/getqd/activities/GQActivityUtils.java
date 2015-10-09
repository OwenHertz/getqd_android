package com.app.upincode.getqd.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import com.app.upincode.getqd.GlobalClass;

/**
 * Created by jpnauta on 15-09-17.
 */
public class GQActivityUtils {
    /**
     * Shows an alert dialog to the user
     *
     * @param activity the activity to display for
     * @param title    the title to display
     * @param message  the message to display
     */
    public static void showAlert(Context context, String title, String message) {

        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);

        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static GlobalClass getGlobalClass(Context context) {
        return (GlobalClass) context.getApplicationContext();
    }
}
