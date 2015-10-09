package com.app.upincode.getqd.networking.parsers.box_office;

import com.app.upincode.getqd.networking.parsers.BaseParser;

public class BOStaffBuyParser extends BaseParser {
    public String first_name;
    public String last_name;
    public String email;
    public String phone_number;
    public String name;
    public String event_access;
    public String number;
    public String expiration_0;
    public String expiration_1;
    public String ccv;
    public String postal;

    public BOStaffBuyParser(String first_name, String last_name, String email, String phone_number, String event_access) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone_number = phone_number;
        this.event_access = event_access;
    }

    public BOStaffBuyParser(
            String first_name, String last_name, String email, String phone_number, String event_access, String name,
            String number, String expiration_0, String expiration_1, String ccv, String postal) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone_number = phone_number;
        this.name = name;
        this.number = number;
        this.expiration_0 = expiration_0;
        this.expiration_1 = expiration_1;
        this.ccv = ccv;
        this.postal = postal;
        this.event_access = event_access;
    }
}
