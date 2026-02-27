package com.wx.apartment.ledger.infrastructure.sharedplace;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("t_shared_place")
public class SharedPlacePO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String placeName;

    private Long electricMeterId;

    private Long waterMeterId;

    private Long relatedTenantId;

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

    public Long getElectricMeterId() {
        return electricMeterId;
    }

    public void setElectricMeterId(Long electricMeterId) {
        this.electricMeterId = electricMeterId;
    }

    public Long getWaterMeterId() {
        return waterMeterId;
    }

    public void setWaterMeterId(Long waterMeterId) {
        this.waterMeterId = waterMeterId;
    }

    public Long getRelatedTenantId() {
        return relatedTenantId;
    }

    public void setRelatedTenantId(Long relatedTenantId) {
        this.relatedTenantId = relatedTenantId;
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

