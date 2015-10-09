package com.app.upincode.getqd.utils;

public class StringUtils {
    public static String asCurrency(Double value) {
        if (value != null) {
            String s;
            if (Math.round(value) != value) {
                s = String.format("%.2f", value);
            } else {
                s = String.format("%.0f", value);
            }
            return "$" + s;
        }
        return null;
    }
}
