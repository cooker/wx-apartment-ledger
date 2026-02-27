package com.wx.apartment.ledger.application.meter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class MeterSetCmd {

    @NotBlank(message = "集合名称不能为空")
    @Size(max = 64, message = "集合名称长度不能超过64个字符")
    private String setName;

    @NotNull(message = "主表ID不能为空")
    private Long mainMeterId;

    private List<Long> childMeterIds;

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
}

