package com.wx.apartment.ledger.domain.bill;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BillPayment {

    private final Long id;
    private final Long billId;
    private final BigDecimal paymentAmount;
    private final LocalDateTime paymentTime;
    private final String paymentNote;
    private final LocalDateTime createdAt;

    public BillPayment(Long id,
                       Long billId,
                       BigDecimal paymentAmount,
                       LocalDateTime paymentTime,
                       String paymentNote,
                       LocalDateTime createdAt) {
        this.id = id;
        this.billId = billId;
        this.paymentAmount = paymentAmount;
        this.paymentTime = paymentTime;
        this.paymentNote = paymentNote;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getBillId() {
        return billId;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public String getPaymentNote() {
        return paymentNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public BillPayment withId(Long id) {
        return new BillPayment(id, billId, paymentAmount, paymentTime, paymentNote, createdAt);
    }
}

