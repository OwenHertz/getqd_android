package com.app.upincode.getqd.networking.parsers.publik;

import android.text.TextUtils;

import com.app.upincode.getqd.config.GQConstants;
import com.app.upincode.getqd.networking.parsers.BaseParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class PublicVenueParser extends BaseParser {
    public Integer id;
    public String name;
    public String avatar;
    public String background;
    public String phone;
    public String email;
    public DateTimeZone timezone;
    public DateTime created;
    public DateTime updated;

    public String street_name;
    public String city;
    public String province;
    public String postal_code;
    public JsonObject schedule_enabled;

    /**
     * @return whether or not the venue is currently open
     */
    public boolean getIsOpen() {
        if (this.schedule_enabled != null && this.timezone != null) {
            return this.getIsOpen(this.timezone);
        }
        return false;
    }

    public boolean getIsOpen(DateTimeZone tz) {
        try {
            DateTime c1 = new DateTime(tz);
            int dayOfWeek = c1.getDayOfWeek();
            int hourOfDay = c1.getHourOfDay();
            int minute = c1.getMinuteOfHour();

            String DOW = GQConstants.DAYS_OF_WEEK[dayOfWeek - 1];

            JsonArray day = this.schedule_enabled.get(DOW).getAsJsonArray();

            for (int m = 0; m < day.size(); m = m + 1) {
                JsonObject dat = day.get(m).getAsJsonObject();
                String startTime = dat.get("start").getAsString();
                String endTime = dat.get("end").getAsString();

                String startH = startTime.substring(0, 2);
                String startM = startTime.substring(3, 5);
                String endH = endTime.substring(0, 2);
                String endM = endTime.substring(3, 5);

                int currentTime = (hourOfDay * 60) + minute;
                int startTimetotal = (Integer.parseInt(startH) * 60) + Integer.parseInt(startM);
                int endTimetotal = (Integer.parseInt(endH) * 60) + Integer.parseInt(endH);

                if (startTimetotal < currentTime) {
                    if (currentTime < endTimetotal) {
                        return true;
                    }
                }

            }
        } catch (Exception e) {
            // Something went wrong!
            e.printStackTrace();
        }
        return false;
    }

    public String getAddress() {
        return TextUtils.join(" ", new String[]{street_name, city, province});
    }
}
