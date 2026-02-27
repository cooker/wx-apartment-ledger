package com.wx.apartment.ledger.domain.sharedplace;

import java.util.List;

public interface SharedPlaceTenantShareRepository {

    List<SharedPlaceTenantShareRow> listByPlaceAndYearMonth(Long sharedPlaceId, int year, int month);

    void replaceForPlaceAndYearMonth(Long sharedPlaceId, int year, int month, List<SharedPlaceTenantShareRow> rows);

    void deleteByPlaceId(Long sharedPlaceId);

    class SharedPlaceTenantShareRow {
        private Long id;
        private Long sharedPlaceId;
        private Long tenantId;
        private int usageYear;
        private int usageMonth;
        private java.math.BigDecimal electricUsage;
        private java.math.BigDecimal waterUsage;
        private java.math.BigDecimal electricAmount;
        private java.math.BigDecimal waterAmount;

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

        public int getUsageYear() {
            return usageYear;
        }

        public void setUsageYear(int usageYear) {
            this.usageYear = usageYear;
        }

        public int getUsageMonth() {
            return usageMonth;
        }

        public void setUsageMonth(int usageMonth) {
            this.usageMonth = usageMonth;
        }

        public java.math.BigDecimal getElectricUsage() {
            return electricUsage;
        }

        public void setElectricUsage(java.math.BigDecimal electricUsage) {
            this.electricUsage = electricUsage;
        }

        public java.math.BigDecimal getWaterUsage() {
            return waterUsage;
        }

        public void setWaterUsage(java.math.BigDecimal waterUsage) {
            this.waterUsage = waterUsage;
        }

        public java.math.BigDecimal getElectricAmount() {
            return electricAmount;
        }

        public void setElectricAmount(java.math.BigDecimal electricAmount) {
            this.electricAmount = electricAmount;
        }

        public java.math.BigDecimal getWaterAmount() {
            return waterAmount;
        }

        public void setWaterAmount(java.math.BigDecimal waterAmount) {
            this.waterAmount = waterAmount;
        }
    }
}

