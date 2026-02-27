package com.wx.apartment.ledger.domain.meter;

import java.util.Optional;

public interface MeterSetRepository {

    MeterSet save(MeterSet meterSet);

    Optional<MeterSet> findById(Long id);

    Optional<MeterSet> findByMainMeterId(Long mainMeterId);

    void deleteById(Long id);
}

