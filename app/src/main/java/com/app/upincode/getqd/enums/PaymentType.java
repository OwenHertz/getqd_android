package com.app.upincode.getqd.enums;

public class PaymentType {
    public static final PaymentType CASH = new PaymentType(1);
    public static final PaymentType CREDIT = new PaymentType(2);
    public static final PaymentType INTERAC = new PaymentType(3);
    public static final PaymentType COMPLIMENTARY = new PaymentType(4);
    public static final PaymentType FREE = new PaymentType(5);
    public static final PaymentType[] PAYMENT_TYPES = {CASH, CREDIT, INTERAC, COMPLIMENTARY, FREE};

    public final Integer id;

    protected PaymentType(Integer id) {
        this.id = id;
    }
}
