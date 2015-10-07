package com.app.upincode.getqd.networking.parsers.auth;

import com.app.upincode.getqd.networking.parsers.BaseParser;

/**
 * Created by jpnauta on 15-09-18.
 */
public class AuthPasswordResetParser extends BaseParser {
    public String email;

    public AuthPasswordResetParser(String email) {
        this.email = email;
    }
}
