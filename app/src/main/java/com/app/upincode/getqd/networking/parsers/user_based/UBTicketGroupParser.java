package com.app.upincode.getqd.networking.parsers.user_based;

import android.graphics.Bitmap;

import com.app.upincode.getqd.networking.parsers.BaseParser;
import com.app.upincode.getqd.networking.parsers.generic.BasePaginationParser;

import org.joda.time.DateTime;

public class UBTicketGroupParser extends BaseParser {
    public class PaginationParser extends BasePaginationParser {
        public UBTicketGroupParser[] results;
    }

    public class EventParser extends BaseParser {
        public String name;
        public String image;
        public String thumbnail;
        public DateTime starts_on;
        public DateTime ends_on;
        public DateTime opens_at;
    }

    public class TypeParser extends BaseParser {
        public String name;
        public EventParser event;
    }

    public class TicketParser extends BaseParser {
        public Integer id;
        public Integer status;
        public String barcode_string;
        public String pkpass;

        //Fields used by view
        public Integer _position;
        public Bitmap _barcode_bitmap;
    }

    public Integer id;
    public TypeParser type;
    public TicketParser[] tickets;

    public String getTicketCountStr() {
        if (tickets != null) {
            return String.valueOf(tickets.length);
        }
        return null;
    }
}
