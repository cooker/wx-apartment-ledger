package com.wx.apartment.ledger.application.meter.dto;

public class MeterMonthlyUsageQuery {

    private Long meterId;
    private Integer usageYear;
    private long pageNo = 1;
    private long pageSize = 12;

    public Long getMeterId() {
        return meterId;
    }

    public void setMeterId(Long meterId) {
        this.meterId = meterId;
    }

    public Integer getUsageYear() {
        return usageYear;
    }

    public void setUsageYear(Integer usageYear) {
        this.usageYear = usageYear;
    }

    public long getPageNo() {
        return pageNo;
    }

    public void setPageNo(long pageNo) {
        this.pageNo = pageNo;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }
}

