package com.app.upincode.getqd.logging;

import android.util.Log;

import com.app.upincode.getqd.config.GQConfig;

public final class GQLog {
    /**
     * log debug message
     */
    public static void d(String tag, String msg) {
        if (GQConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    /**
     * log debug message for object
     */
    public static void dObj(Object obj, String msg) {
        if (GQConfig.DEBUG) {
            Log.d(obj.getClass().getName(), msg);
        }
    }

    /**
     * log error message
     */
    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    /**
     * log error message for object
     */
    public static void eObj(Object obj, String msg) {
        Log.e(obj.getClass().getName(), msg);
    }

    /**
     * log warning message
     */
    public static void w(String tag, String msg) {
        Log.e(tag, msg);
    }

    /**
     * log warning message for object
     */
    public static void wObj(Object obj, String msg) {
        Log.e(obj.getClass().getName(), msg);
    }
}
