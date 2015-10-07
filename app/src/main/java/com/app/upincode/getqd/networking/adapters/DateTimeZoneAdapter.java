package com.app.upincode.getqd.networking.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.joda.time.DateTimeZone;

import java.io.IOException;

public class DateTimeZoneAdapter extends TypeAdapter<DateTimeZone> {
    @Override
    public void write(JsonWriter writer, DateTimeZone tz) throws IOException {
        // Serialize timezone objects to timezone ID
        if (tz == null) {
            writer.nullValue();
            return;
        }
        String xy = tz.getID();
        writer.value(xy);
    }

    @Override
    public DateTimeZone read(JsonReader reader) throws IOException {
        // Convert timezone ID to TimeZone objects
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        String tz = reader.nextString();

        if (tz != null && !tz.isEmpty()) {
            return DateTimeZone.forID(tz);
        }
        return null;
    }
}
