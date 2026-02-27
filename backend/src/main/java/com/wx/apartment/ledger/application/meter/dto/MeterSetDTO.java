package com.wx.apartment.ledger.application.meter.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MeterSetDTO {

    private Long id;
    private String setName;
    private Long mainMeterId;
    private List<Long> childMeterIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public Long getMainMeterId() {
        return mainMeterId;
    }

    public void setMainMeterId(Long mainMeterId) {
        this.mainMeterId = mainMeterId;
    }

    public List<Long> getChildMeterIds() {
        return childMeterIds;
    }

    public void setChildMeterIds(List<Long> childMeterIds) {
        this.childMeterIds = childMeterIds;
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

