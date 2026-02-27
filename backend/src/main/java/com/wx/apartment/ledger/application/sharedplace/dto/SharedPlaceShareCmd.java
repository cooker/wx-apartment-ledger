package com.wx.apartment.ledger.application.sharedplace.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class SharedPlaceShareCmd {

    @NotNull
    private Long placeId;

    @NotNull
    @Min(2000)
    @Max(2100)
    private Integer usageYear;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer usageMonth;

    @NotNull
    private List<Item> items;

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
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

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        @NotNull
        private Long tenantId;
        @Min(0)
        private BigDecimal electricUsage;
        @Min(0)
        private BigDecimal waterUsage;

        public Long getTenantId() {
            return tenantId;
        }

        public void setTenantId(Long tenantId) {
            this.tenantId = tenantId;
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
    }
}

