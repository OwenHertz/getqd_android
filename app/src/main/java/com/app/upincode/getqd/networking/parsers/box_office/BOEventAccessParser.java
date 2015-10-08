package com.app.upincode.getqd.networking.parsers.box_office;

import com.app.upincode.getqd.networking.parsers.BaseParser;

import org.joda.time.DateTime;

public class BOEventAccessParser extends BaseParser {
    public class EventParser extends BaseParser {
        public Integer id;
        public String name;
        public String slug;
        public String subtitle;
        public String image;
        public String image_medium;
        public DateTime starts_on;
        public DateTime ends_on;
        public DateTime opens_at;
    }

    public class VenueParser extends BaseParser {
        public Integer id;
        public String name;
        public String slug;
    }

    public class InventoryParser extends BaseParser {
        class TicketTypeParser extends BaseParser {
            public Integer id;
            public String name;
        }

        public Integer limit_count;
    }

    public String slug;
    public EventParser event;
    public VenueParser venue;

    public InventoryParser[] inventories; //NOTE: only available when slug is given in request
}
