package com.wx.apartment.ledger.application.bill.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class BillDetailDTO {

    private Long id;
    private Long tenantId;
    /** 租客姓名 */
    private String tenantName;
    private Integer billYear;
    private Integer billMonth;
    /** 关联房屋ID列表 */
    private List<Long> houseIds;
    /** 关联房屋展示标签（名称或编码），与 houseIds 顺序对应 */
    private List<String> houseLabels;
    /** 关联公共场所ID列表 */
    private List<Long> sharedPlaceIds;
    /** 关联公共场所展示标签，与 sharedPlaceIds 顺序对应 */
    private List<String> sharedPlaceLabels;
    /** 房屋维度的水电用量与费用明细 */
    private List<BillHouseItemDTO> houseItems;
    /** 公共场所维度的水电用量与费用明细 */
    private List<BillSharedPlaceItemDTO> sharedPlaceItems;
    private BigDecimal rentAmount;
    private BigDecimal waterAmount;
    private BigDecimal electricAmount;
    private BigDecimal adjustAmount;
    private BigDecimal totalAmount;
    private String settleState;
    private BigDecimal paidAmount;
    private LocalDateTime paidTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Integer getBillYear() {
        return billYear;
    }

    public void setBillYear(Integer billYear) {
        this.billYear = billYear;
    }

    public Integer getBillMonth() {
        return billMonth;
    }

    public void setBillMonth(Integer billMonth) {
        this.billMonth = billMonth;
    }

    public List<Long> getHouseIds() {
        return houseIds == null ? Collections.emptyList() : houseIds;
    }

    public void setHouseIds(List<Long> houseIds) {
        this.houseIds = houseIds;
    }

    public List<String> getHouseLabels() {
        return houseLabels == null ? Collections.emptyList() : houseLabels;
    }

    public void setHouseLabels(List<String> houseLabels) {
        this.houseLabels = houseLabels;
    }

    public List<Long> getSharedPlaceIds() {
        return sharedPlaceIds == null ? Collections.emptyList() : sharedPlaceIds;
    }

    public void setSharedPlaceIds(List<Long> sharedPlaceIds) {
        this.sharedPlaceIds = sharedPlaceIds;
    }

    public List<String> getSharedPlaceLabels() {
        return sharedPlaceLabels == null ? Collections.emptyList() : sharedPlaceLabels;
    }

    public void setSharedPlaceLabels(List<String> sharedPlaceLabels) {
        this.sharedPlaceLabels = sharedPlaceLabels;
    }

    public List<BillHouseItemDTO> getHouseItems() {
        return houseItems == null ? Collections.emptyList() : houseItems;
    }

    public void setHouseItems(List<BillHouseItemDTO> houseItems) {
        this.houseItems = houseItems;
    }

    public List<BillSharedPlaceItemDTO> getSharedPlaceItems() {
        return sharedPlaceItems == null ? Collections.emptyList() : sharedPlaceItems;
    }

    public void setSharedPlaceItems(List<BillSharedPlaceItemDTO> sharedPlaceItems) {
        this.sharedPlaceItems = sharedPlaceItems;
    }

    public BigDecimal getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(BigDecimal rentAmount) {
        this.rentAmount = rentAmount;
    }

    public BigDecimal getWaterAmount() {
        return waterAmount;
    }

    public void setWaterAmount(BigDecimal waterAmount) {
        this.waterAmount = waterAmount;
    }

    public BigDecimal getElectricAmount() {
        return electricAmount;
    }

    public void setElectricAmount(BigDecimal electricAmount) {
        this.electricAmount = electricAmount;
    }

    public BigDecimal getAdjustAmount() {
        return adjustAmount;
    }

    public void setAdjustAmount(BigDecimal adjustAmount) {
        this.adjustAmount = adjustAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSettleState() {
        return settleState;
    }

    public void setSettleState(String settleState) {
        this.settleState = settleState;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public LocalDateTime getPaidTime() {
        return paidTime;
    }

    public void setPaidTime(LocalDateTime paidTime) {
        this.paidTime = paidTime;
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

    public static class BillHouseItemDTO {
        private Long id;
        private Long houseId;
        private String houseLabel;
        private BigDecimal shareRatio;
        private BigDecimal electricUsage;
        private BigDecimal waterUsage;
        private BigDecimal electricAmount;
        private BigDecimal waterAmount;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getHouseId() {
            return houseId;
        }

        public void setHouseId(Long houseId) {
            this.houseId = houseId;
        }

        public String getHouseLabel() {
            return houseLabel;
        }

        public void setHouseLabel(String houseLabel) {
            this.houseLabel = houseLabel;
        }

        public BigDecimal getShareRatio() {
            return shareRatio;
        }

        public void setShareRatio(BigDecimal shareRatio) {
            this.shareRatio = shareRatio;
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

    public static class BillSharedPlaceItemDTO {
        private Long id;
        private Long sharedPlaceId;
        private String sharedPlaceLabel;
        private BigDecimal shareRatio;
        private BigDecimal electricUsage;
        private BigDecimal waterUsage;
        private BigDecimal electricAmount;
        private BigDecimal waterAmount;

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

        public String getSharedPlaceLabel() {
            return sharedPlaceLabel;
        }

        public void setSharedPlaceLabel(String sharedPlaceLabel) {
            this.sharedPlaceLabel = sharedPlaceLabel;
        }

        public BigDecimal getShareRatio() {
            return shareRatio;
        }

        public void setShareRatio(BigDecimal shareRatio) {
            this.shareRatio = shareRatio;
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

