package com.app.upincode.getqd.networking.parsers.box_office;

import com.app.upincode.getqd.networking.parsers.BaseParser;

public class BORequestTicketParser extends BaseParser {
    public Integer type;
    public Integer change;

    public BORequestTicketParser(Integer change, Integer type) {
        this.change = change;
        this.type = type;
    }
}
