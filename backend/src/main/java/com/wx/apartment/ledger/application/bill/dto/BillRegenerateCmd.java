package com.wx.apartment.ledger.application.bill.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BillRegenerateCmd {

    private Long tenantId;

    @NotNull
    @Min(2000)
    @Max(2100)
    private Integer billYear;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer billMonth;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
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
}

