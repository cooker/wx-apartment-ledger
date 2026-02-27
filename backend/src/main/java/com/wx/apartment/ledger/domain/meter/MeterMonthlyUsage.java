package com.wx.apartment.ledger.domain.meter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MeterMonthlyUsage {

    private final Long id;
    private final Long meterId;
    private final int usageYear;
    private final int usageMonth;
    private final BigDecimal previousReading;
    private final BigDecimal currentReading;
    private final BigDecimal usageValue;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public MeterMonthlyUsage(Long id,
                             Long meterId,
                             int usageYear,
                             int usageMonth,
                             BigDecimal previousReading,
                             BigDecimal currentReading,
                             BigDecimal usageValue,
                             LocalDateTime createdAt,
                             LocalDateTime updatedAt) {
        this.id = id;
        this.meterId = meterId;
        this.usageYear = usageYear;
        this.usageMonth = usageMonth;
        this.previousReading = previousReading;
        this.currentReading = currentReading;
        this.usageValue = usageValue;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getMeterId() {
        return meterId;
    }

    public int getUsageYear() {
        return usageYear;
    }

    public int getUsageMonth() {
        return usageMonth;
    }

    public BigDecimal getPreviousReading() {
        return previousReading;
    }

    public BigDecimal getCurrentReading() {
        return currentReading;
    }

    public BigDecimal getUsageValue() {
        return usageValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public MeterMonthlyUsage withId(Long id) {
        return new MeterMonthlyUsage(id, meterId, usageYear, usageMonth, previousReading, currentReading, usageValue, createdAt, updatedAt);
    }

    public static MeterMonthlyUsage create(Long meterId,
                                           int usageYear,
                                           int usageMonth,
                                           BigDecimal previousReading,
                                           BigDecimal currentReading,
                                           LocalDateTime now) {
        if (previousReading == null || currentReading == null) {
            throw new IllegalArgumentException("读数不能为空");
        }
        if (currentReading.compareTo(previousReading) < 0) {
            throw new IllegalArgumentException("本月读数不能小于上月读数");
        }
        BigDecimal usageValue = currentReading.subtract(previousReading);
        return new MeterMonthlyUsage(null, meterId, usageYear, usageMonth, previousReading, currentReading, usageValue, now, now);
    }

    public MeterMonthlyUsage update(BigDecimal previousReading,
                                    BigDecimal currentReading,
                                    LocalDateTime updatedAt) {
        if (previousReading == null || currentReading == null) {
            throw new IllegalArgumentException("读数不能为空");
        }
        if (currentReading.compareTo(previousReading) < 0) {
            throw new IllegalArgumentException("本月读数不能小于上月读数");
        }
        BigDecimal usageValue = currentReading.subtract(previousReading);
        return new MeterMonthlyUsage(this.id, this.meterId, this.usageYear, this.usageMonth,
                previousReading, currentReading, usageValue, this.createdAt, updatedAt);
    }
}

