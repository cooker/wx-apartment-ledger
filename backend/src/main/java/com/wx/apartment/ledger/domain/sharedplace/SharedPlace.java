package com.wx.apartment.ledger.domain.sharedplace;

import java.time.LocalDateTime;

public class SharedPlace {

    private final Long id;
    private final String placeName;
    private final Long electricMeterId;
    private final Long waterMeterId;
    private final Long relatedTenantId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public SharedPlace(Long id,
                       String placeName,
                       Long electricMeterId,
                       Long waterMeterId,
                       Long relatedTenantId,
                       LocalDateTime createdAt,
                       LocalDateTime updatedAt) {
        this.id = id;
        this.placeName = placeName;
        this.electricMeterId = electricMeterId;
        this.waterMeterId = waterMeterId;
        this.relatedTenantId = relatedTenantId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public Long getElectricMeterId() {
        return electricMeterId;
    }

    public Long getWaterMeterId() {
        return waterMeterId;
    }

    public Long getRelatedTenantId() {
        return relatedTenantId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public SharedPlace withId(Long id) {
        return new SharedPlace(id, placeName, electricMeterId, waterMeterId, relatedTenantId, createdAt, updatedAt);
    }

    public SharedPlace update(String placeName,
                              Long electricMeterId,
                              Long waterMeterId,
                              Long relatedTenantId,
                              LocalDateTime updatedAt) {
        return new SharedPlace(this.id, placeName, electricMeterId, waterMeterId, relatedTenantId, this.createdAt, updatedAt);
    }
}

