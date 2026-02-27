package com.wx.apartment.ledger.infrastructure.bill;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("t_bill_house")
public class BillHousePO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long billId;

    private Long houseId;

    private BigDecimal shareRatio;

    private BigDecimal electricUsage;

    private BigDecimal waterUsage;

    private BigDecimal electricAmount;

    private BigDecimal waterAmount;

    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
