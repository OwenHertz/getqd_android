package com.app.upincode.getqd.networking.parsers.generic;

import com.app.upincode.getqd.config.GQConstants;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONObject;

public class VenueScheduleEnabledParser extends JSONObject {
    /**
     * Indicates whether or not the schedule shows that the venue is currently open
     *
     * @param tz the venue's timezone
     * @return whether or not the venue is open
     */
    public boolean isVenueOpen(DateTimeZone tz) {
        return true; //TODO remove

//        try {
//            DateTime c1 = new DateTime(tz);
//            int dayOfWeek = c1.getDayOfWeek();
//            int hourOfDay = c1.getHourOfDay();
//            int minute = c1.getMinuteOfHour();
//
//            String DOW = GQConstants.DAYS_OF_WEEK[dayOfWeek - 1];
//
//            JSONArray day = this.getJSONArray(DOW);
//
//            for (int m = 0; m < day.length(); m = m + 1) {
//                JSONObject dat = day.getJSONObject(m);
//                String startTime = dat.getString("start");
//                String endTime = dat.getString("end");
//
//                String startH = startTime.substring(0, 2);
//                String startM = startTime.substring(3, 5);
//                String endH = endTime.substring(0, 2);
//                String endM = endTime.substring(3, 5);
//
//                int currentTime = (hourOfDay * 60) + minute;
//                int startTimetotal = (Integer.parseInt(startH) * 60) + Integer.parseInt(startM);
//                int endTimetotal = (Integer.parseInt(endH) * 60) + Integer.parseInt(endH);
//
//                if (startTimetotal < currentTime) {
//                    if (currentTime < endTimetotal) {
//                        return true;
//                    }
//                }
//
//            }
//        } catch (Exception e) {
//            // Something went wrong!
//            e.printStackTrace();
//        }
//        return false;
    }
}
