package com.wx.apartment.ledger.application.house.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class HouseCmd {

    @NotBlank(message = "房屋编号不能为空")
    @Size(max = 64, message = "房屋编号长度不能超过64个字符")
    private String houseCode;

    @Size(max = 128, message = "房屋名称长度不能超过128个字符")
    private String houseName;

    @Size(max = 255, message = "地址描述长度不能超过255个字符")
    private String locationText;

    @DecimalMin(value = "0", inclusive = true, message = "月租金不能为负数")
    private BigDecimal rentAmount;

    @DecimalMin(value = "0", inclusive = true, message = "押金不能为负数")
    private BigDecimal depositAmount;

    /** 入住日期，格式：yyyy-MM-dd，可为空 */
    private String checkInDate;

    /** 当前租客ID，可为空 */
    private Long currentTenantId;

    /**
     * 绑定的计量表ID列表（电表+水表）
     */
    private List<Long> meterIds;

    public String getHouseCode() {
        return houseCode;
    }

    public void setHouseCode(String houseCode) {
        this.houseCode = houseCode;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getLocationText() {
        return locationText;
    }

    public void setLocationText(String locationText) {
        this.locationText = locationText;
    }

    public BigDecimal getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(BigDecimal rentAmount) {
        this.rentAmount = rentAmount;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Long getCurrentTenantId() {
        return currentTenantId;
    }

    public void setCurrentTenantId(Long currentTenantId) {
        this.currentTenantId = currentTenantId;
    }

    public List<Long> getMeterIds() {
        return meterIds == null ? Collections.emptyList() : meterIds;
    }

    public void setMeterIds(List<Long> meterIds) {
        this.meterIds = meterIds;
    }
}

