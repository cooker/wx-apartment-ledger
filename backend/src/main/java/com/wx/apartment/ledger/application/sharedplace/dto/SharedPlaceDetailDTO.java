package com.wx.apartment.ledger.application.sharedplace.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class SharedPlaceDetailDTO {

    private Long id;
    private String placeName;
    /** 计量表ID列表（电表+水表） */
    private List<Long> meterIds;
    /** 关联租客ID列表 */
    private List<Long> tenantIds;
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

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public List<Long> getMeterIds() {
        return meterIds == null ? Collections.emptyList() : meterIds;
    }

    public void setMeterIds(List<Long> meterIds) {
        this.meterIds = meterIds;
    }

    public List<Long> getTenantIds() {
        return tenantIds == null ? Collections.emptyList() : tenantIds;
    }

    public void setTenantIds(List<Long> tenantIds) {
        this.tenantIds = tenantIds;
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

