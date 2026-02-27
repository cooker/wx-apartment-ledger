package com.wx.apartment.ledger.infrastructure.sharedplace;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("t_shared_place_tenant")
public class SharedPlaceTenantPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sharedPlaceId;

    private Long tenantId;

    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSharedPlaceId() {
        return sharedPlaceId;
    }

    public void setSharedPlaceId(Long sharedPlaceId) {
        this.sharedPlaceId = sharedPlaceId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
