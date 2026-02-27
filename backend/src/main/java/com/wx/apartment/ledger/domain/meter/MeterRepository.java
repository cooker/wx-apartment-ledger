package com.wx.apartment.ledger.domain.meter;

import java.util.List;
import java.util.Optional;

public interface MeterRepository {

    Meter save(Meter meter);

    Optional<Meter> findById(Long id);

    List<Meter> findPage(MeterKind kind, String keyword, long offset, long pageSize);

    long count(MeterKind kind, String keyword);

    void deleteById(Long id);
}

