package com.wx.apartment.ledger.domain.house;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class House {

    private final Long id;
    private final String houseCode;
    private final String houseName;
    private final String locationText;
    private final BigDecimal rentAmount;
    private final BigDecimal depositAmount;
    private final LocalDate checkInDate;
    private final Long currentTenantId;
    private final Long electricMeterId;
    private final Long waterMeterId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public House(Long id,
                 String houseCode,
                 String houseName,
                 String locationText,
                 BigDecimal rentAmount,
                 BigDecimal depositAmount,
                 LocalDate checkInDate,
                 Long currentTenantId,
                 Long electricMeterId,
                 Long waterMeterId,
                 LocalDateTime createdAt,
                 LocalDateTime updatedAt) {
        this.id = id;
        this.houseCode = houseCode;
        this.houseName = houseName;
        this.locationText = locationText;
        this.rentAmount = rentAmount;
        this.depositAmount = depositAmount;
        this.checkInDate = checkInDate;
        this.currentTenantId = currentTenantId;
        this.electricMeterId = electricMeterId;
        this.waterMeterId = waterMeterId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getHouseCode() {
        return houseCode;
    }

    public String getHouseName() {
        return houseName;
    }

    public String getLocationText() {
        return locationText;
    }

    public BigDecimal getRentAmount() {
        return rentAmount;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public Long getCurrentTenantId() {
        return currentTenantId;
    }

    public Long getElectricMeterId() {
        return electricMeterId;
    }

    public Long getWaterMeterId() {
        return waterMeterId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public House withId(Long id) {
        return new House(id, houseCode, houseName, locationText, rentAmount, depositAmount,
                checkInDate, currentTenantId, electricMeterId, waterMeterId, createdAt, updatedAt);
    }

    public House updateBasicInfo(String houseName,
                                 String locationText,
                                 BigDecimal rentAmount,
                                 BigDecimal depositAmount,
                                 LocalDate checkInDate,
                                 Long currentTenantId,
                                 Long electricMeterId,
                                 Long waterMeterId,
                                 LocalDateTime updatedAt) {
        return new House(this.id, this.houseCode, houseName, locationText, rentAmount, depositAmount,
                checkInDate, currentTenantId, electricMeterId, waterMeterId, this.createdAt, updatedAt);
    }
}

