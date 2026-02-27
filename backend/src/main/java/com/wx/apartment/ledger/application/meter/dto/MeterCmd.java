package com.wx.apartment.ledger.application.meter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MeterCmd {

    @NotBlank(message = "计量表编号不能为空")
    @Size(max = 64, message = "计量表编号长度不能超过64个字符")
    private String meterCode;

    @NotBlank(message = "计量表类型不能为空")
    private String meterKind;

    @Size(max = 128, message = "安装位置长度不能超过128个字符")
    private String installLocation;

    private Boolean mainMeter;

    public String getMeterCode() {
        return meterCode;
    }

    public void setMeterCode(String meterCode) {
        this.meterCode = meterCode;
    }

    public String getMeterKind() {
        return meterKind;
    }

    public void setMeterKind(String meterKind) {
        this.meterKind = meterKind;
    }

    public String getInstallLocation() {
        return installLocation;
    }

    public void setInstallLocation(String installLocation) {
        this.installLocation = installLocation;
    }

    public Boolean getMainMeter() {
        return mainMeter;
    }

    public void setMainMeter(Boolean mainMeter) {
        this.mainMeter = mainMeter;
    }
}

