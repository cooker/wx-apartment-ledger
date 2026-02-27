package com.wx.apartment.ledger.infrastructure.sharedplace;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlaceMeterRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class SharedPlaceMeterRepositoryImpl implements SharedPlaceMeterRepository {

    private final SharedPlaceMeterMapper mapper;

    public SharedPlaceMeterRepositoryImpl(SharedPlaceMeterMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void replaceByPlaceId(Long sharedPlaceId, List<Long> meterIds) {
        mapper.delete(new LambdaQueryWrapper<SharedPlaceMeterPO>()
                .eq(SharedPlaceMeterPO::getSharedPlaceId, sharedPlaceId));
        if (meterIds != null && !meterIds.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (Long meterId : meterIds) {
                SharedPlaceMeterPO po = new SharedPlaceMeterPO();
                po.setSharedPlaceId(sharedPlaceId);
                po.setMeterId(meterId);
                po.setCreatedAt(now);
                mapper.insert(po);
            }
        }
    }

    @Override
    public List<Long> findMeterIdsByPlaceId(Long sharedPlaceId) {
        List<SharedPlaceMeterPO> list = mapper.selectList(
                new LambdaQueryWrapper<SharedPlaceMeterPO>()
                        .eq(SharedPlaceMeterPO::getSharedPlaceId, sharedPlaceId));
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> result = new ArrayList<>(list.size());
        for (SharedPlaceMeterPO po : list) {
            result.add(po.getMeterId());
        }
        return result;
    }

    @Override
    public void deleteByPlaceId(Long sharedPlaceId) {
        mapper.delete(new LambdaQueryWrapper<SharedPlaceMeterPO>()
                .eq(SharedPlaceMeterPO::getSharedPlaceId, sharedPlaceId));
    }
}
