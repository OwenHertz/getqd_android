package com.app.upincode.getqd.networking.parsers.generic;

import com.app.upincode.getqd.networking.parsers.BaseParser;

/**
 * Do to limitations of Java, every paginated view has to have its own Pagination parser. However,
 * this class makes things easier.
 *
 * To make a pagination parser for your needs, create the following class:
 *
 * public class PaginationParser extends BasePaginationParser{
 *     public MyClass[] results;
 * }
 */
public class BasePaginationParser extends BaseParser {
    public Integer count;
    public Integer page_number;
    public String next;
    public String previous;
}
