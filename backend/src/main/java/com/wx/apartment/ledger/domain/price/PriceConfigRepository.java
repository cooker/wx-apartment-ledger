package com.wx.apartment.ledger.domain.price;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PriceConfigRepository {

    PriceConfig save(PriceConfig priceConfig);

    Optional<PriceConfig> findById(Long id);

    Optional<PriceConfig> findCurrentByKind(ChargeKind kind, LocalDate date);

    List<PriceConfig> listByKind(ChargeKind kind);
}

