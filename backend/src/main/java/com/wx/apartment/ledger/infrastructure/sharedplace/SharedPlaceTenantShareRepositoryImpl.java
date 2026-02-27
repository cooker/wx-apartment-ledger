package com.wx.apartment.ledger.infrastructure.sharedplace;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlaceTenantShareRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SharedPlaceTenantShareRepositoryImpl implements SharedPlaceTenantShareRepository {

    private final SharedPlaceTenantShareMapper mapper;

    public SharedPlaceTenantShareRepositoryImpl(SharedPlaceTenantShareMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<SharedPlaceTenantShareRow> listByPlaceAndYearMonth(Long sharedPlaceId, int year, int month) {
        List<SharedPlaceTenantSharePO> list = mapper.selectList(
                new LambdaQueryWrapper<SharedPlaceTenantSharePO>()
                        .eq(SharedPlaceTenantSharePO::getSharedPlaceId, sharedPlaceId)
                        .eq(SharedPlaceTenantSharePO::getUsageYear, year)
                        .eq(SharedPlaceTenantSharePO::getUsageMonth, month)
        );
        return list.stream().map(this::toRow).collect(Collectors.toList());
    }

    @Override
    public void replaceForPlaceAndYearMonth(Long sharedPlaceId, int year, int month, List<SharedPlaceTenantShareRow> rows) {
        // 删除原有记录
        mapper.delete(new LambdaQueryWrapper<SharedPlaceTenantSharePO>()
                .eq(SharedPlaceTenantSharePO::getSharedPlaceId, sharedPlaceId)
                .eq(SharedPlaceTenantSharePO::getUsageYear, year)
                .eq(SharedPlaceTenantSharePO::getUsageMonth, month));
        // 插入新记录
        for (SharedPlaceTenantShareRow row : rows) {
            row.setSharedPlaceId(sharedPlaceId);
            SharedPlaceTenantSharePO po = toPO(row);
            po.setId(null);
            mapper.insert(po);
        }
    }

    @Override
    public void deleteByPlaceId(Long sharedPlaceId) {
        mapper.delete(new LambdaQueryWrapper<SharedPlaceTenantSharePO>()
                .eq(SharedPlaceTenantSharePO::getSharedPlaceId, sharedPlaceId));
    }

    private SharedPlaceTenantShareRow toRow(SharedPlaceTenantSharePO po) {
        SharedPlaceTenantShareRow row = new SharedPlaceTenantShareRow();
        row.setId(po.getId());
        row.setTenantId(po.getTenantId());
        row.setUsageYear(po.getUsageYear());
        row.setUsageMonth(po.getUsageMonth());
        row.setElectricUsage(po.getElectricUsage());
        row.setWaterUsage(po.getWaterUsage());
        row.setElectricAmount(po.getElectricAmount());
        row.setWaterAmount(po.getWaterAmount());
        return row;
    }

    private SharedPlaceTenantSharePO toPO(SharedPlaceTenantShareRow row) {
        SharedPlaceTenantSharePO po = new SharedPlaceTenantSharePO();
        po.setId(row.getId());
        po.setSharedPlaceId(row.getSharedPlaceId());
        po.setTenantId(row.getTenantId());
        po.setUsageYear(row.getUsageYear());
        po.setUsageMonth(row.getUsageMonth());
        po.setElectricUsage(row.getElectricUsage());
        po.setWaterUsage(row.getWaterUsage());
        po.setElectricAmount(row.getElectricAmount());
        po.setWaterAmount(row.getWaterAmount());
        return po;
    }
}

