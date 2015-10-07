package com.app.upincode.getqd.networking.adapters;

import com.app.upincode.getqd.config.GQConstants;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

public class DateTimeAdapter extends TypeAdapter<DateTime> {
    String outputFormat = GQConstants.ISO_FORMAT;

    @Override
    public void write(JsonWriter writer, DateTime dt) throws IOException {
        // Serialize timezone objects to timezone ID
        if (dt == null) {
            writer.nullValue();
            return;
        }

        dt = dt.toDateTime(GQConstants.UTC); // ISO dates should be serialized in UTC
        DateTimeFormatter isoFormatter = DateTimeFormat.forPattern(this.outputFormat).withZone(GQConstants.UTC);
        writer.value(isoFormatter.print(dt));
    }

    @Override
    public DateTime read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        String str = reader.nextString();

        try {
            DateTimeFormatter formatter = ISODateTimeFormat.dateTimeParser();
            return formatter.parseDateTime(str);
        } catch (IllegalArgumentException e) {
            //Couldn't parse with this format
        }

        return null;
    }
}
