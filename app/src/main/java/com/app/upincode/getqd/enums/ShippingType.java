package com.app.upincode.getqd.enums;

public class ShippingType extends Constant {
    public static ShippingType WILL_CALL = new ShippingType(
            1, "Will Call"
    );
    public static ShippingType PRINT_AT_HOME = new ShippingType(
            2, "Print At Home"
    );
    public static ShippingType LOAD_ONTO_CARD = new ShippingType(
            3, "Load Onto Card"
    );
    public static final ShippingType[] ALL = {WILL_CALL, PRINT_AT_HOME, LOAD_ONTO_CARD};

    public ShippingType(Integer id, String name) {
        super(id, name);
    }
}
