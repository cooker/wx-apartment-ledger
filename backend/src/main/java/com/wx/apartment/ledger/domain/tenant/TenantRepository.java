package com.wx.apartment.ledger.domain.tenant;

import java.util.List;
import java.util.Optional;

public interface TenantRepository {

    Tenant save(Tenant tenant);

    Optional<Tenant> findById(Long id);

    List<Tenant> findPage(String keyword, long offset, long pageSize);

    long countByKeyword(String keyword);

    void deleteById(Long id);
}

