package com.app.upincode.getqd.utils;

import android.content.Context;
import android.text.TextUtils;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateFormatUtils {
    protected static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static String getFormat(DateTime dt, DateFormat df) {
        if (dt != null) {
            return df.format(dt.toDate());
        }
        return null;
    }

    public static String getFormat(DateTime dt , String format) {
        return getFormat(dt, new SimpleDateFormat(format));
    }

    public static String getLongDateFormat(DateTime dt) {
        DateFormat df = android.text.format.DateFormat.getLongDateFormat(mContext);
        return getFormat(dt, df);
    }

    public static String getMediumDateFormat(DateTime dt) {
        DateFormat df = android.text.format.DateFormat.getMediumDateFormat(mContext);
        return getFormat(dt, df);
    }

    public static String getDateFormat(DateTime dt) {
        DateFormat df = android.text.format.DateFormat.getDateFormat(mContext);
        return getFormat(dt, df);
    }

    public static String getTimeFormat(DateTime dt) {
        DateFormat df = android.text.format.DateFormat.getTimeFormat(mContext);
        return getFormat(dt, df);
    }

    public static String getLongDateTimeFormat(DateTime dt) {
        return TextUtils.join(" ", new String[]{getLongDateFormat(dt), getTimeFormat(dt)});
    }

    public static String getMediumDateTimeFormat(DateTime dt) {
        return TextUtils.join(" ", new String[]{getMediumDateFormat(dt), getTimeFormat(dt)});
    }
}
