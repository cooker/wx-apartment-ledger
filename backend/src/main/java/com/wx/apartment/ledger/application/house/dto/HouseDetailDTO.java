package com.wx.apartment.ledger.application.house.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class HouseDetailDTO {

    private Long id;
    private String houseCode;
    private String houseName;
    private String locationText;
    private BigDecimal rentAmount;
    private BigDecimal depositAmount;
    private LocalDate checkInDate;
    private Long currentTenantId;
    /** 绑定的计量表ID列表（电表+水表） */
    private List<Long> meterIds;
    /** 本月电表总用量（所有电表之和） */
    private BigDecimal electricUsageThisMonth;
    /** 本月水表总用量（所有水表之和） */
    private BigDecimal waterUsageThisMonth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHouseCode() {
        return houseCode;
    }

    public void setHouseCode(String houseCode) {
        this.houseCode = houseCode;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getLocationText() {
        return locationText;
    }

    public void setLocationText(String locationText) {
        this.locationText = locationText;
    }

    public BigDecimal getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(BigDecimal rentAmount) {
        this.rentAmount = rentAmount;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Long getCurrentTenantId() {
        return currentTenantId;
    }

    public void setCurrentTenantId(Long currentTenantId) {
        this.currentTenantId = currentTenantId;
    }

    public List<Long> getMeterIds() {
        return meterIds == null ? Collections.emptyList() : meterIds;
    }

    public void setMeterIds(List<Long> meterIds) {
        this.meterIds = meterIds;
    }

    public BigDecimal getElectricUsageThisMonth() {
        return electricUsageThisMonth;
    }

    public void setElectricUsageThisMonth(BigDecimal electricUsageThisMonth) {
        this.electricUsageThisMonth = electricUsageThisMonth;
    }

    public BigDecimal getWaterUsageThisMonth() {
        return waterUsageThisMonth;
    }

    public void setWaterUsageThisMonth(BigDecimal waterUsageThisMonth) {
        this.waterUsageThisMonth = waterUsageThisMonth;
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

