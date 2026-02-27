package com.wx.apartment.ledger.application.meter.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class MeterMonthlyUsageCmd {

    @NotNull(message = "计量表ID不能为空")
    private Long meterId;

    @NotNull(message = "年份不能为空")
    private Integer usageYear;

    @NotNull(message = "月份不能为空")
    @Min(value = 1, message = "月份必须在1到12之间")
    @Max(value = 12, message = "月份必须在1到12之间")
    private Integer usageMonth;

    private BigDecimal previousReading;

    @NotNull(message = "本月读数不能为空")
    private BigDecimal currentReading;

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
}

