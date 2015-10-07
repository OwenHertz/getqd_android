package com.app.upincode.getqd.networking.parsers.auth;

import com.app.upincode.getqd.networking.parsers.BaseParser;

/**
 * Created by jpnauta on 15-09-18.
 */
public class AuthJWTLoginParser extends BaseParser {
    public String email;
    public String password;

    public AuthJWTLoginParser(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
