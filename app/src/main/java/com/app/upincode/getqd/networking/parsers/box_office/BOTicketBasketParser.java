package com.app.upincode.getqd.networking.parsers.box_office;

import com.app.upincode.getqd.networking.parsers.BaseParser;

import org.joda.time.DateTime;

public class BOTicketBasketParser extends BaseParser {

    public class TicketGroupParser extends BaseParser {
        public class TicketParser extends BaseParser {
        }

        public class TicketTypeParser extends BaseParser {
            public Integer id;
            public String name;
            public Double price;
            public Double total_price;
            public Double service_charges;
            public Double subtotal;
        }

        public TicketParser[] tickets;
        public TicketTypeParser type;
        public Integer purchase_limit;
    }


    public Integer id;
    public DateTime expiry_date;
    public Double subtotal;
    public Double full_price;
    public Integer payment_type;
    public Integer shipping_type;
    public TicketGroupParser[] groups;

    public Integer getTicketCount(BOEventAccessParser.InventoryParser inventory) {
        Integer count = 0;
        for (TicketGroupParser group : groups) {
            if (group.type.id.equals(inventory.ticket_type.id)) {
                count += group.tickets.length;
            }
        }
        return count;
    }
}
