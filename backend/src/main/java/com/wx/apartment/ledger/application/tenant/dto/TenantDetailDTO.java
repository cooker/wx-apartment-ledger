package com.wx.apartment.ledger.application.tenant.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TenantDetailDTO {

    private Long id;
    private String fullName;
    private String mobileNumber;
    private String wechatId;
    private String remarkText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** 关联的公共场所名称列表 */
    private List<String> sharedPlaceNames = new ArrayList<>();
    /** 关联的房屋（当前租客）展示列表，如 "A101(xx房)" */
    private List<String> houseNames = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getRemarkText() {
        return remarkText;
    }

    public void setRemarkText(String remarkText) {
        this.remarkText = remarkText;
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

    public List<String> getSharedPlaceNames() {
        return sharedPlaceNames;
    }

    public void setSharedPlaceNames(List<String> sharedPlaceNames) {
        this.sharedPlaceNames = sharedPlaceNames != null ? sharedPlaceNames : new ArrayList<>();
    }

    public List<String> getHouseNames() {
        return houseNames;
    }

    public void setHouseNames(List<String> houseNames) {
        this.houseNames = houseNames != null ? houseNames : new ArrayList<>();
    }
}

