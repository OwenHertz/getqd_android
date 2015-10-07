package com.app.upincode.getqd.networking.parsers.user_based;

import com.app.upincode.getqd.networking.parsers.BaseParser;
import com.app.upincode.getqd.networking.parsers.generic.BasePaginationParser;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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
    }

    public Integer id;
    public TypeParser type;
    public TicketParser[] tickets;
}
