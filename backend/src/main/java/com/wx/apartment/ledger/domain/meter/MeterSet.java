package com.wx.apartment.ledger.domain.meter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class MeterSet {

    private final Long id;
    private final String setName;
    private final Long mainMeterId;
    private final List<Long> childMeterIds;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public MeterSet(Long id,
                    String setName,
                    Long mainMeterId,
                    List<Long> childMeterIds,
                    LocalDateTime createdAt,
                    LocalDateTime updatedAt) {
        this.id = id;
        this.setName = setName;
        this.mainMeterId = mainMeterId;
        this.childMeterIds = childMeterIds == null ? Collections.emptyList() : List.copyOf(childMeterIds);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getSetName() {
        return setName;
    }

    public Long getMainMeterId() {
        return mainMeterId;
    }

    public List<Long> getChildMeterIds() {
        return childMeterIds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public MeterSet withId(Long id) {
        return new MeterSet(id, setName, mainMeterId, childMeterIds, createdAt, updatedAt);
    }

    public MeterSet updateChildren(List<Long> childMeterIds, LocalDateTime updatedAt) {
        return new MeterSet(this.id, this.setName, this.mainMeterId, childMeterIds, this.createdAt, updatedAt);
    }
}

