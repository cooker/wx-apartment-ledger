package com.wx.apartment.ledger.domain.bill;

public enum BillSettleState {
    PENDING,
    SETTLED;

    public static BillSettleState fromString(String value) {
        if (value == null) {
            return PENDING;
        }
        return BillSettleState.valueOf(value.toUpperCase());
    }
}

