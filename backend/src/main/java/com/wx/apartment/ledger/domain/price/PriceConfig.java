package com.wx.apartment.ledger.domain.price;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PriceConfig {

    private final Long id;
    private final ChargeKind chargeKind;
    private final BigDecimal unitPrice;
    private final LocalDate effectiveDate;
    private final LocalDate expiredDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PriceConfig(Long id,
                       ChargeKind chargeKind,
                       BigDecimal unitPrice,
                       LocalDate effectiveDate,
                       LocalDate expiredDate,
                       LocalDateTime createdAt,
                       LocalDateTime updatedAt) {
        this.id = id;
        this.chargeKind = chargeKind;
        this.unitPrice = unitPrice;
        this.effectiveDate = effectiveDate;
        this.expiredDate = expiredDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public ChargeKind getChargeKind() {
        return chargeKind;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isEffectiveOn(LocalDate date) {
        if (date == null) {
            return false;
        }
        boolean afterStart = !date.isBefore(effectiveDate);
        boolean beforeEnd = expiredDate == null || !date.isAfter(expiredDate);
        return afterStart && beforeEnd;
    }

    public PriceConfig withId(Long id) {
        return new PriceConfig(id, chargeKind, unitPrice, effectiveDate, expiredDate, createdAt, updatedAt);
    }

    public PriceConfig update(BigDecimal unitPrice,
                              LocalDate effectiveDate,
                              LocalDate expiredDate,
                              LocalDateTime updatedAt) {
        return new PriceConfig(this.id, this.chargeKind, unitPrice, effectiveDate, expiredDate, this.createdAt, updatedAt);
    }
}

