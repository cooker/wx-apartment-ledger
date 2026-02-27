package com.wx.apartment.ledger.domain.bill;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bill {

    private final Long id;
    private final Long tenantId;
    private final int billYear;
    private final int billMonth;
    private final BigDecimal rentAmount;
    private final BigDecimal waterAmount;
    private final BigDecimal electricAmount;
    private final BigDecimal adjustAmount;
    private final BigDecimal totalAmount;
    private final BillSettleState settleState;
    private final BigDecimal paidAmount;
    private final LocalDateTime paidTime;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Bill(Long id,
                Long tenantId,
                int billYear,
                int billMonth,
                BigDecimal rentAmount,
                BigDecimal waterAmount,
                BigDecimal electricAmount,
                BigDecimal adjustAmount,
                BigDecimal totalAmount,
                BillSettleState settleState,
                BigDecimal paidAmount,
                LocalDateTime paidTime,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.billYear = billYear;
        this.billMonth = billMonth;
        this.rentAmount = rentAmount;
        this.waterAmount = waterAmount;
        this.electricAmount = electricAmount;
        this.adjustAmount = adjustAmount;
        this.totalAmount = totalAmount;
        this.settleState = settleState;
        this.paidAmount = paidAmount;
        this.paidTime = paidTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public int getBillYear() {
        return billYear;
    }

    public int getBillMonth() {
        return billMonth;
    }

    public BigDecimal getRentAmount() {
        return rentAmount;
    }

    public BigDecimal getWaterAmount() {
        return waterAmount;
    }

    public BigDecimal getElectricAmount() {
        return electricAmount;
    }

    public BigDecimal getAdjustAmount() {
        return adjustAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BillSettleState getSettleState() {
        return settleState;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public LocalDateTime getPaidTime() {
        return paidTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Bill withId(Long id) {
        return new Bill(id, tenantId, billYear, billMonth,
                rentAmount, waterAmount, electricAmount, adjustAmount, totalAmount,
                settleState, paidAmount, paidTime, createdAt, updatedAt);
    }

    public Bill recalculateTotal(LocalDateTime updatedAt) {
        BigDecimal rent = rentAmount == null ? BigDecimal.ZERO : rentAmount;
        BigDecimal water = waterAmount == null ? BigDecimal.ZERO : waterAmount;
        BigDecimal electric = electricAmount == null ? BigDecimal.ZERO : electricAmount;
        BigDecimal adjust = adjustAmount == null ? BigDecimal.ZERO : adjustAmount;
        BigDecimal total = rent.add(water).add(electric).add(adjust);
        return new Bill(id, tenantId, billYear, billMonth,
                rentAmount, waterAmount, electricAmount, adjustAmount, total,
                settleState, paidAmount, paidTime, createdAt, updatedAt);
    }

    public Bill applyPayment(BigDecimal amount, LocalDateTime paymentTime) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("收款金额必须大于0");
        }
        BigDecimal currentPaid = paidAmount == null ? BigDecimal.ZERO : paidAmount;
        BigDecimal newPaid = currentPaid.add(amount);
        BillSettleState newState = BillSettleState.SETTLED;
        return new Bill(id, tenantId, billYear, billMonth,
                rentAmount, waterAmount, electricAmount, adjustAmount, totalAmount,
                newState, newPaid, paymentTime, createdAt, paymentTime);
    }
}

