package com.wx.apartment.ledger.infrastructure.sharedplace;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("t_shared_place_tenant_share")
public class SharedPlaceTenantSharePO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sharedPlaceId;

    private Long tenantId;

    private Integer usageYear;

    private Integer usageMonth;

    private BigDecimal electricUsage;

    private BigDecimal waterUsage;

    private BigDecimal electricAmount;

    private BigDecimal waterAmount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSharedPlaceId() {
        return sharedPlaceId;
    }

    public void setSharedPlaceId(Long sharedPlaceId) {
        this.sharedPlaceId = sharedPlaceId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
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

    public BigDecimal getElectricUsage() {
        return electricUsage;
    }

    public void setElectricUsage(BigDecimal electricUsage) {
        this.electricUsage = electricUsage;
    }

    public BigDecimal getWaterUsage() {
        return waterUsage;
    }

    public void setWaterUsage(BigDecimal waterUsage) {
        this.waterUsage = waterUsage;
    }

    public BigDecimal getElectricAmount() {
        return electricAmount;
    }

    public void setElectricAmount(BigDecimal electricAmount) {
        this.electricAmount = electricAmount;
    }

    public BigDecimal getWaterAmount() {
        return waterAmount;
    }

    public void setWaterAmount(BigDecimal waterAmount) {
        this.waterAmount = waterAmount;
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

