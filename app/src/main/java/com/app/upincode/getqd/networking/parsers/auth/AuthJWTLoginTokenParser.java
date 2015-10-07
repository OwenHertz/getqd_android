package com.app.upincode.getqd.networking.parsers.auth;

import com.app.upincode.getqd.networking.parsers.BaseParser;

/**
 * Created by jpnauta on 15-09-18.
 */
public class AuthJWTLoginTokenParser extends BaseParser {
    public String token;

    public AuthJWTLoginTokenParser(String token) {
        this.token = token;
    }
}
