package com.app.upincode.getqd.config;

import org.joda.time.DateTimeZone;

/**
 * Created by jpnauta on 15-09-18.
 */
public class GQConstants {
    //App constants
    public static final String APP_NAME = "GetQD Copyright 2015,2016";

    //Build settings
    public static final String DEFAULT_PROPERTIES_PATH = "config/default.properties";
    public static final String DEBUG_PROPERTIES_PATH = "config/debug.properties";
    public static final String PRODUCTION_PROPERTIES_PATH = "config/production.properties";
    public static final String LOCAL_PROPERTIES_PATH = "config/local.properties";

    //User properties
    public static final String PROPERTIES_FILE_NAME = "GetQD.Properties";
    public static final String PROPERTIES_USER_TOKEN_KEY = "AuthToken";

    //Date formatting
    public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final DateTimeZone UTC = DateTimeZone.forID("UTC");

    public static final String[] DAYS_OF_WEEK = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
}
