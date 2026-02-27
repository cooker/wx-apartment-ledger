package com.wx.apartment.ledger.domain.sharedplace;

import java.util.List;

public interface SharedPlaceMeterRepository {

    void replaceByPlaceId(Long sharedPlaceId, List<Long> meterIds);

    List<Long> findMeterIdsByPlaceId(Long sharedPlaceId);

    void deleteByPlaceId(Long sharedPlaceId);
}
