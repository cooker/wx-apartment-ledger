package com.wx.apartment.ledger.application.meter.dto;

import java.time.LocalDateTime;

public class MeterDetailDTO {

    private Long id;
    private String meterCode;
    private String meterKind;
    private String installLocation;
    private Boolean mainMeter;
    /** 当月总表与子表用量差异：主表用量 - 子表用量之和，仅对总表有效 */
    private java.math.BigDecimal currentMonthDiff;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeterCode() {
        return meterCode;
    }

    public void setMeterCode(String meterCode) {
        this.meterCode = meterCode;
    }

    public String getMeterKind() {
        return meterKind;
    }

    public void setMeterKind(String meterKind) {
        this.meterKind = meterKind;
    }

    public String getInstallLocation() {
        return installLocation;
    }

    public void setInstallLocation(String installLocation) {
        this.installLocation = installLocation;
    }

    public Boolean getMainMeter() {
        return mainMeter;
    }

    public void setMainMeter(Boolean mainMeter) {
        this.mainMeter = mainMeter;
    }

    public java.math.BigDecimal getCurrentMonthDiff() {
        return currentMonthDiff;
    }

    public void setCurrentMonthDiff(java.math.BigDecimal currentMonthDiff) {
        this.currentMonthDiff = currentMonthDiff;
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

