package com.wx.apartment.ledger.domain.sharedplace;

import java.util.List;
import java.util.Optional;

public interface SharedPlaceRepository {

    SharedPlace save(SharedPlace sharedPlace);

    Optional<SharedPlace> findById(Long id);

    List<SharedPlace> findPage(String keyword, long offset, long pageSize);

    long count(String keyword);

    void deleteById(Long id);
}

