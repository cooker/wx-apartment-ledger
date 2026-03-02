package com.wx.apartment.ledger.domain.house;

import java.util.List;
import java.util.Optional;

public interface HouseRepository {

    House save(House house);

    Optional<House> findById(Long id);

    List<House> findPage(String keyword, long offset, long pageSize);

    long countByKeyword(String keyword);

    boolean existsByCurrentTenantId(Long tenantId);

    List<House> findByCurrentTenantId(Long tenantId);

    void deleteById(Long id);
}

