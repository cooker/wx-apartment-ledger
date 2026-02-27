package com.wx.apartment.ledger.domain.price;

public enum ChargeKind {
    ELECTRIC,
    WATER;

    public static ChargeKind fromString(String value) {
        if (value == null) {
            return null;
        }
        return ChargeKind.valueOf(value.toUpperCase());
    }
}

