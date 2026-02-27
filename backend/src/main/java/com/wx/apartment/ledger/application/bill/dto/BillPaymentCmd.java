package com.wx.apartment.ledger.application.bill.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BillPaymentCmd {

    @NotNull(message = "账单ID不能为空")
    private Long billId;

    @NotNull(message = "收款金额不能为空")
    @DecimalMin(value = "0", inclusive = false, message = "收款金额必须大于0")
    private BigDecimal paymentAmount;

    /** 收款时间，若为空则使用当前时间 */
    private LocalDateTime paymentTime;

    private String paymentNote;

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getPaymentNote() {
        return paymentNote;
    }

    public void setPaymentNote(String paymentNote) {
        this.paymentNote = paymentNote;
    }
}

