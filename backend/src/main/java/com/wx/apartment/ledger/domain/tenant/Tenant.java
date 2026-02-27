package com.wx.apartment.ledger.domain.tenant;

import java.time.LocalDateTime;

public class Tenant {

    private Long id;
    private String fullName;
    private String mobileNumber;
    private String wechatId;
    private String remarkText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Tenant(Long id,
                  String fullName,
                  String mobileNumber,
                  String wechatId,
                  String remarkText,
                  LocalDateTime createdAt,
                  LocalDateTime updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.wechatId = wechatId;
        this.remarkText = remarkText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getWechatId() {
        return wechatId;
    }

    public String getRemarkText() {
        return remarkText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Tenant withId(Long id) {
        return new Tenant(id, fullName, mobileNumber, wechatId, remarkText, createdAt, updatedAt);
    }

    public Tenant updateBasicInfo(String fullName,
                                  String mobileNumber,
                                  String wechatId,
                                  String remarkText,
                                  LocalDateTime updatedAt) {
        return new Tenant(this.id, fullName, mobileNumber, wechatId, remarkText, this.createdAt, updatedAt);
    }
}

