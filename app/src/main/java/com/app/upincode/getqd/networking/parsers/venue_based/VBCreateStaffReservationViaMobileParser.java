package com.app.upincode.getqd.networking.parsers.venue_based;

import android.text.Editable;

import com.app.upincode.getqd.databinding.SimpleTextWatcher;
import com.app.upincode.getqd.networking.parsers.BaseParser;

import org.joda.time.DateTime;

/**
 * Created by jpnauta on 15-09-18.
 */
public class VBCreateStaffReservationViaMobileParser extends BaseParser {
    public Integer num_of_people = 1;
    public Integer type;
    public String details;
    public String first_name;
    public String last_name;
    public String email;
    public String phone_number;
    public DateTime arrival_date = new DateTime(); //tz should be set in application

    public SimpleTextWatcher details_watcher = new SimpleTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            details = s.toString();
        }
    };

    public SimpleTextWatcher first_name_watcher = new SimpleTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            first_name = s.toString();
        }
    };

    public SimpleTextWatcher last_name_watcher = new SimpleTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            last_name = s.toString();
        }
    };

    public SimpleTextWatcher email_watcher = new SimpleTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            email = s.toString();
        }
    };

    public SimpleTextWatcher phone_number_watcher = new SimpleTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            phone_number = s.toString();
        }
    };
}
