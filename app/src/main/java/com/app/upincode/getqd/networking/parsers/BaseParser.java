package com.app.upincode.getqd.networking.parsers;

import android.databinding.BaseObservable;

import com.app.upincode.getqd.config.GQConstants;
import com.app.upincode.getqd.models.CoordinateSet;
import com.app.upincode.getqd.networking.adapters.DateTimeAdapter;
import com.app.upincode.getqd.networking.adapters.DateTimeZoneAdapter;
import com.app.upincode.getqd.networking.adapters.CoordinateSetAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public abstract class BaseParser extends BaseObservable {
    /**
     * Serializes the parser to JSON
     */
    public String toString() {
        Gson gson = getGsonBuilder().create();
        return gson.toJson(this);
    }

    /**
     * Default gson build for entire application. Adds serialization support for dates,
     * timezones
     */
    public static GsonBuilder getGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Use ISO getFormat when sending dates to server
        gsonBuilder.setDateFormat(GQConstants.ISO_FORMAT);

        //Register Gson type adapters
        gsonBuilder.registerTypeAdapter(CoordinateSet.class, new CoordinateSetAdapter());
        gsonBuilder.registerTypeHierarchyAdapter(DateTime.class, new DateTimeAdapter());
        gsonBuilder.registerTypeHierarchyAdapter(DateTimeZone.class, new DateTimeZoneAdapter());

        return gsonBuilder;
    }

    /**
     * Converts the given JSON string to a parser object
     */
    public static <T> T fromString(Class<T> clazz, String json) {
        Gson gson = getGsonBuilder().create();

        return gson.fromJson(json, clazz);
    }
}