package com.app.upincode.getqd.networking.parsers.publik;

import android.text.TextUtils;

import com.app.upincode.getqd.networking.parsers.BaseParser;
import com.app.upincode.getqd.networking.parsers.generic.VenueScheduleEnabledParser;

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
    public VenueScheduleEnabledParser schedule_enabled;

    /**
     * @return whether or not the venue is currently open
     */
    public boolean getIsOpen() {
        if (this.schedule_enabled != null && this.timezone != null) {
            return this.schedule_enabled.isVenueOpen(this.timezone);
        }
        return false;
    }

    public String getAddress() {
        return TextUtils.join(" ", new String[]{street_name, city, province});
    }
}
