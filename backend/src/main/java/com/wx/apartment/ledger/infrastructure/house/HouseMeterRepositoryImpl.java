package com.wx.apartment.ledger.infrastructure.house;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.apartment.ledger.domain.house.HouseMeterRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class HouseMeterRepositoryImpl implements HouseMeterRepository {

    private final HouseMeterMapper mapper;

    public HouseMeterRepositoryImpl(HouseMeterMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void replaceByHouseId(Long houseId, List<Long> meterIds) {
        mapper.delete(new LambdaQueryWrapper<HouseMeterPO>()
                .eq(HouseMeterPO::getHouseId, houseId));
        if (meterIds != null && !meterIds.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (Long meterId : meterIds) {
                HouseMeterPO po = new HouseMeterPO();
                po.setHouseId(houseId);
                po.setMeterId(meterId);
                po.setCreatedAt(now);
                mapper.insert(po);
            }
        }
    }

    @Override
    public List<Long> findMeterIdsByHouseId(Long houseId) {
        List<HouseMeterPO> list = mapper.selectList(
                new LambdaQueryWrapper<HouseMeterPO>()
                        .eq(HouseMeterPO::getHouseId, houseId));
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> result = new ArrayList<>(list.size());
        for (HouseMeterPO po : list) {
            result.add(po.getMeterId());
        }
        return result;
    }

    @Override
    public void deleteByHouseId(Long houseId) {
        mapper.delete(new LambdaQueryWrapper<HouseMeterPO>()
                .eq(HouseMeterPO::getHouseId, houseId));
    }
}

