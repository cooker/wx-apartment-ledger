package com.wx.apartment.ledger.application.sharedplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Collections;
import java.util.List;

public class SharedPlaceCmd {

    @NotBlank(message = "公共场所名称不能为空")
    @Size(max = 128, message = "公共场所名称长度不能超过128个字符")
    private String placeName;

    /** 计量表ID列表（电表+水表） */
    private List<Long> meterIds;

    /** 关联租客ID列表 */
    private List<Long> tenantIds;

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
}

