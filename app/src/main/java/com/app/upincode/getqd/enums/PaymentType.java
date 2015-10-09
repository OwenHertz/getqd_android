package com.app.upincode.getqd.enums;

public final class PaymentType extends Constant {
    public final static PaymentType CASH = new PaymentType(
            1, "Cash"
    );
    public final static PaymentType CREDIT = new PaymentType(
            2, "Credit Card"
    );
    public final static PaymentType INTERAC = new PaymentType(
            3, "Interac"
    );
    public final static PaymentType COMPLIMENTARY = new PaymentType(
            4, "Complimentary"
    );
    public final static PaymentType FREE = new PaymentType(
            5, "Free"
    );
    public final static PaymentType[] ALL = {CASH, CREDIT, INTERAC, COMPLIMENTARY, FREE};

    public PaymentType(Integer id, String name) {
        super(id, name);
    }
}
