package com.wx.apartment.ledger.domain.meter;

import java.time.LocalDateTime;

public class Meter {

    private final Long id;
    private final String meterCode;
    private final MeterKind meterKind;
    private final String installLocation;
    private final boolean mainMeter;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Meter(Long id,
                 String meterCode,
                 MeterKind meterKind,
                 String installLocation,
                 boolean mainMeter,
                 LocalDateTime createdAt,
                 LocalDateTime updatedAt) {
        this.id = id;
        this.meterCode = meterCode;
        this.meterKind = meterKind;
        this.installLocation = installLocation;
        this.mainMeter = mainMeter;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getMeterCode() {
        return meterCode;
    }

    public MeterKind getMeterKind() {
        return meterKind;
    }

    public String getInstallLocation() {
        return installLocation;
    }

    public boolean isMainMeter() {
        return mainMeter;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Meter withId(Long id) {
        return new Meter(id, meterCode, meterKind, installLocation, mainMeter, createdAt, updatedAt);
    }

    public Meter updateBasicInfo(String installLocation, boolean mainMeter, LocalDateTime updatedAt) {
        return new Meter(this.id, this.meterCode, this.meterKind, installLocation, mainMeter, this.createdAt, updatedAt);
    }
}

