package com.app.upincode.getqd.networking.parsers.user_based;

import com.app.upincode.getqd.enums.PaymentType;
import com.app.upincode.getqd.networking.parsers.BaseParser;
import com.app.upincode.getqd.networking.parsers.generic.BasePaginationParser;
import com.google.gson.JsonObject;

import org.json.JSONException;

public class UBEventStatParser extends BaseParser {
    public class PaginationParser extends BasePaginationParser {
        public UBEventStatParser[] results;
    }

    public class EventParser extends BaseParser {
        public String name;
    }

    public class TTStatEntry extends BaseParser {
        public class TicketTypeParser extends BaseParser {
            public String name;
        }

        public TicketTypeParser ticket_type;
        public JsonObject tickets_sold_breakdown;
        public JsonObject amount_sold_breakdown;
    }

    public EventParser event;
    public TTStatEntry[] tt_stats;

    /**
     * @param paymentType payment type to find for
     * @return the total sold for the given payment type
     */
    public double getAmountSold(PaymentType paymentType) {
        double total = 0;

        for (TTStatEntry entry : tt_stats) {
            try {
                String key = String.valueOf(paymentType.id);

                if (entry.amount_sold_breakdown.has(key)) {
                    total += entry.amount_sold_breakdown.get(String.valueOf(paymentType.id)).getAsDouble();
                }
            } catch (ClassCastException e) {
                //Unexpected format... move on
                e.printStackTrace();
            }
        }

        return total;
    }
}
