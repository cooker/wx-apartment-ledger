package com.wx.apartment.ledger.domain.meter;

import java.util.List;
import java.util.Optional;

public interface MeterMonthlyUsageRepository {

    MeterMonthlyUsage save(MeterMonthlyUsage usage);

    Optional<MeterMonthlyUsage> findByMeterAndYearMonth(Long meterId, int year, int month);

    List<MeterMonthlyUsage> findPageByMeter(Long meterId, Integer year, long offset, long pageSize);

    long countByMeter(Long meterId, Integer year);

    void deleteById(Long id);
}

