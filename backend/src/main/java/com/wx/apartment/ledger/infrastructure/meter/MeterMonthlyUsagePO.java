package com.wx.apartment.ledger.infrastructure.meter;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("t_meter_monthly_usage")
public class MeterMonthlyUsagePO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long meterId;

    private Integer usageYear;

    private Integer usageMonth;

    private BigDecimal previousReading;

    private BigDecimal currentReading;

    private BigDecimal usageValue;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMeterId() {
        return meterId;
    }

    public void setMeterId(Long meterId) {
        this.meterId = meterId;
    }

    public Integer getUsageYear() {
        return usageYear;
    }

    public void setUsageYear(Integer usageYear) {
        this.usageYear = usageYear;
    }

    public Integer getUsageMonth() {
        return usageMonth;
    }

    public void setUsageMonth(Integer usageMonth) {
        this.usageMonth = usageMonth;
    }

    public BigDecimal getPreviousReading() {
        return previousReading;
    }

    public void setPreviousReading(BigDecimal previousReading) {
        this.previousReading = previousReading;
    }

    public BigDecimal getCurrentReading() {
        return currentReading;
    }

    public void setCurrentReading(BigDecimal currentReading) {
        this.currentReading = currentReading;
    }

    public BigDecimal getUsageValue() {
        return usageValue;
    }

    public void setUsageValue(BigDecimal usageValue) {
        this.usageValue = usageValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

