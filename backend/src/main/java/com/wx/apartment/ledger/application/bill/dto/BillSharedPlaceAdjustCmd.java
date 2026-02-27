package com.wx.apartment.ledger.application.bill.dto;

import java.math.BigDecimal;
import java.util.List;

public class BillSharedPlaceAdjustCmd {

    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        private Long id;
        private BigDecimal electricAmount;
        private BigDecimal waterAmount;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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
    }
}

