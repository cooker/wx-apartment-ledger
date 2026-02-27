package com.wx.apartment.ledger.application.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TenantCmd {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 64, message = "姓名长度不能超过64个字符")
    private String fullName;

    @NotBlank(message = "手机号不能为空")
    @Size(max = 32, message = "手机号长度不能超过32个字符")
    private String mobileNumber;

    @Size(max = 64, message = "微信号长度不能超过64个字符")
    private String wechatId;

    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String remarkText;

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
}

