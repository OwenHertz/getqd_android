package com.app.upincode.getqd.networking.adapters;

import com.app.upincode.getqd.models.CoordinateSet;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;


import java.io.IOException;

/**
 * Parser for a coordinate set
 */
public class CoordinateSetAdapter extends TypeAdapter<CoordinateSet> {
    @Override
    public void write(JsonWriter writer, CoordinateSet coordinateSet) throws IOException {
        if (coordinateSet == null) {
            writer.nullValue();
            return;
        }

        writer.value(coordinateSet.x + "," + coordinateSet.y);
    }

    @Override
    public CoordinateSet read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        String[] list = null;
        if (reader.peek() == JsonToken.STRING) {
            String str = reader.nextString();
            list = str.split(",");
        }

        if (list != null && list.length == 2) {
            try {
                return new CoordinateSet(Double.parseDouble(list[0]), Double.parseDouble(list[1]));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
