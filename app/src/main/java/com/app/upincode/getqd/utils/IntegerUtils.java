package com.app.upincode.getqd.utils;

public class IntegerUtils {
    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

    public static Integer[] getRange(int start, int stop) {
        Integer[] result = new Integer[stop - start];

        for (int i = 0; i < stop - start; i++)
            result[i] = start + i;

        return result;
    }
}
