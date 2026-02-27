package com.wx.apartment.ledger.application.sharedplace.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SharedPlaceShareDTO {

    private Long placeId;
    private String placeName;
    private Integer usageYear;
    private Integer usageMonth;
    /** 公共场所该月总电量 */
    private BigDecimal totalElectricUsage;
    /** 公共场所该月总水量 */
    private BigDecimal totalWaterUsage;
    /** 各租客分摊明细 */
    private List<Item> items = new ArrayList<>();

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
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

    public BigDecimal getTotalElectricUsage() {
        return totalElectricUsage;
    }

    public void setTotalElectricUsage(BigDecimal totalElectricUsage) {
        this.totalElectricUsage = totalElectricUsage;
    }

    public BigDecimal getTotalWaterUsage() {
        return totalWaterUsage;
    }

    public void setTotalWaterUsage(BigDecimal totalWaterUsage) {
        this.totalWaterUsage = totalWaterUsage;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        private Long tenantId;
        private BigDecimal electricUsage;
        private BigDecimal waterUsage;
        private BigDecimal electricAmount;
        private BigDecimal waterAmount;

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
    }
}

