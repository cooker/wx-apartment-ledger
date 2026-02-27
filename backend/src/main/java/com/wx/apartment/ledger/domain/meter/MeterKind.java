package com.wx.apartment.ledger.domain.meter;

public enum MeterKind {
    ELECTRIC,
    WATER;

    public static MeterKind fromString(String value) {
        if (value == null) {
            return null;
        }
        return MeterKind.valueOf(value.toUpperCase());
    }
}

