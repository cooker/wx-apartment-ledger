package com.wx.apartment.ledger.infrastructure.sharedplace;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlaceTenantRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class SharedPlaceTenantRepositoryImpl implements SharedPlaceTenantRepository {

    private final SharedPlaceTenantMapper mapper;

    public SharedPlaceTenantRepositoryImpl(SharedPlaceTenantMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void replaceByPlaceId(Long sharedPlaceId, List<Long> tenantIds) {
        mapper.delete(new LambdaQueryWrapper<SharedPlaceTenantPO>()
                .eq(SharedPlaceTenantPO::getSharedPlaceId, sharedPlaceId));
        if (tenantIds != null && !tenantIds.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (Long tenantId : tenantIds) {
                SharedPlaceTenantPO po = new SharedPlaceTenantPO();
                po.setSharedPlaceId(sharedPlaceId);
                po.setTenantId(tenantId);
                po.setCreatedAt(now);
                mapper.insert(po);
            }
        }
    }

    @Override
    public List<Long> findPlaceIdsByTenantId(Long tenantId) {
        List<SharedPlaceTenantPO> list = mapper.selectList(
                new LambdaQueryWrapper<SharedPlaceTenantPO>()
                        .eq(SharedPlaceTenantPO::getTenantId, tenantId));
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> result = new ArrayList<>(list.size());
        for (SharedPlaceTenantPO po : list) {
            result.add(po.getSharedPlaceId());
        }
        return result;
    }

    @Override
    public List<Long> findTenantIdsByPlaceId(Long sharedPlaceId) {
        List<SharedPlaceTenantPO> list = mapper.selectList(
                new LambdaQueryWrapper<SharedPlaceTenantPO>()
                        .eq(SharedPlaceTenantPO::getSharedPlaceId, sharedPlaceId));
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> result = new ArrayList<>(list.size());
        for (SharedPlaceTenantPO po : list) {
            result.add(po.getTenantId());
        }
        return result;
    }

    @Override
    public void deleteByPlaceId(Long sharedPlaceId) {
        mapper.delete(new LambdaQueryWrapper<SharedPlaceTenantPO>()
                .eq(SharedPlaceTenantPO::getSharedPlaceId, sharedPlaceId));
    }
}
