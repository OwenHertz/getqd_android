package com.app.upincode.getqd.networking.parsers.generic;

import android.text.Editable;

import com.app.upincode.getqd.databinding.SimpleTextWatcher;
import com.app.upincode.getqd.networking.parsers.BaseParser;

import org.joda.time.DateTime;

/**
 * Created by jpnauta on 15-09-18.
 */
public class CurrentUserParser extends BaseParser {
    public Integer id;
    public String username;
    public String email;
    public String first_name;
    public String last_name;
    public DateTime last_login;

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
}
