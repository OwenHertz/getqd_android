package com.app.upincode.getqd.networking.parsers.venue_based;

import com.app.upincode.getqd.networking.parsers.BaseParser;
import com.app.upincode.getqd.networking.parsers.generic.BasePaginationParser;

import org.joda.time.DateTime;

/**
 * Created by jpnauta on 15-09-18.
 */
// Parser for receiving ticket history from server
public class VBEventsTicketHistoryParser extends BaseParser {
    public class ItemParser extends BaseParser {
        public class UserParser extends BaseParser {
            public String username;
            public String email;
        }

        public class TypeParser extends BaseParser {
            public Integer id;
            public String name;
        }

        public class ProductParser extends BaseParser {
            public Integer id;
            public String name;
        }

        public UserParser user;
        public TypeParser type; // Only set when group_type == 'ticket'
        public ProductParser product; // Only set when group_type == 'product'
    }

    public Integer id;
    public String group_type; // Either 'ticket' or 'product'
    public ItemParser item; // Each object has a 'item' object
}