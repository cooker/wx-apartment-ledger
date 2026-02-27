package com.wx.apartment.ledger.domain.sharedplace;

import java.util.List;

public interface SharedPlaceTenantRepository {

    void replaceByPlaceId(Long sharedPlaceId, List<Long> tenantIds);

    List<Long> findTenantIdsByPlaceId(Long sharedPlaceId);

    /** 查询某租客关联的所有公共场所ID */
    List<Long> findPlaceIdsByTenantId(Long tenantId);

    void deleteByPlaceId(Long sharedPlaceId);
}
