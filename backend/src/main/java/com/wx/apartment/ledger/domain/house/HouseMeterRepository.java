package com.wx.apartment.ledger.domain.house;

import java.util.List;

public interface HouseMeterRepository {

    void replaceByHouseId(Long houseId, List<Long> meterIds);

    List<Long> findMeterIdsByHouseId(Long houseId);

    void deleteByHouseId(Long houseId);
}

