package com.wx.apartment.ledger.infrastructure.meter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wx.apartment.ledger.domain.meter.MeterMonthlyUsage;
import com.wx.apartment.ledger.domain.meter.MeterMonthlyUsageRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MeterMonthlyUsageRepositoryImpl implements MeterMonthlyUsageRepository {

    private final MeterMonthlyUsageMapper meterMonthlyUsageMapper;

    public MeterMonthlyUsageRepositoryImpl(MeterMonthlyUsageMapper meterMonthlyUsageMapper) {
        this.meterMonthlyUsageMapper = meterMonthlyUsageMapper;
    }

    @Override
    public MeterMonthlyUsage save(MeterMonthlyUsage usage) {
        MeterMonthlyUsagePO po = toPO(usage);
        if (po.getId() == null) {
            meterMonthlyUsageMapper.insert(po);
        } else {
            meterMonthlyUsageMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<MeterMonthlyUsage> findByMeterAndYearMonth(Long meterId, int year, int month) {
        LambdaQueryWrapper<MeterMonthlyUsagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MeterMonthlyUsagePO::getMeterId, meterId)
                .eq(MeterMonthlyUsagePO::getUsageYear, year)
                .eq(MeterMonthlyUsagePO::getUsageMonth, month);
        MeterMonthlyUsagePO po = meterMonthlyUsageMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<MeterMonthlyUsage> findPageByMeter(Long meterId, Integer year, long offset, long pageSize) {
        long pageNo = offset / pageSize + 1;
        Page<MeterMonthlyUsagePO> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<MeterMonthlyUsagePO> wrapper = buildWrapper(meterId, year);
        Page<MeterMonthlyUsagePO> result = meterMonthlyUsageMapper.selectPage(page, wrapper);
        return result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByMeter(Long meterId, Integer year) {
        LambdaQueryWrapper<MeterMonthlyUsagePO> wrapper = buildWrapper(meterId, year);
        return meterMonthlyUsageMapper.selectCount(wrapper);
    }

    @Override
    public void deleteById(Long id) {
        meterMonthlyUsageMapper.deleteById(id);
    }

    private LambdaQueryWrapper<MeterMonthlyUsagePO> buildWrapper(Long meterId, Integer year) {
        LambdaQueryWrapper<MeterMonthlyUsagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MeterMonthlyUsagePO::getMeterId, meterId);
        if (year != null) {
            wrapper.eq(MeterMonthlyUsagePO::getUsageYear, year);
        }
        wrapper.orderByDesc(MeterMonthlyUsagePO::getUsageYear)
                .orderByDesc(MeterMonthlyUsagePO::getUsageMonth);
        return wrapper;
    }

    private MeterMonthlyUsagePO toPO(MeterMonthlyUsage usage) {
        MeterMonthlyUsagePO po = new MeterMonthlyUsagePO();
        po.setId(usage.getId());
        po.setMeterId(usage.getMeterId());
        po.setUsageYear(usage.getUsageYear());
        po.setUsageMonth(usage.getUsageMonth());
        po.setPreviousReading(usage.getPreviousReading());
        po.setCurrentReading(usage.getCurrentReading());
        po.setUsageValue(usage.getUsageValue());
        po.setCreatedAt(usage.getCreatedAt());
        po.setUpdatedAt(usage.getUpdatedAt());
        return po;
    }

    private MeterMonthlyUsage toDomain(MeterMonthlyUsagePO po) {
        return new MeterMonthlyUsage(
                po.getId(),
                po.getMeterId(),
                po.getUsageYear(),
                po.getUsageMonth(),
                po.getPreviousReading(),
                po.getCurrentReading(),
                po.getUsageValue(),
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }
}

