package com.wx.apartment.ledger.infrastructure.meter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wx.apartment.ledger.domain.meter.Meter;
import com.wx.apartment.ledger.domain.meter.MeterKind;
import com.wx.apartment.ledger.domain.meter.MeterRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MeterRepositoryImpl implements MeterRepository {

    private final MeterMapper meterMapper;

    public MeterRepositoryImpl(MeterMapper meterMapper) {
        this.meterMapper = meterMapper;
    }

    @Override
    public Meter save(Meter meter) {
        MeterPO po = toPO(meter);
        if (po.getId() == null) {
            meterMapper.insert(po);
        } else {
            meterMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<Meter> findById(Long id) {
        MeterPO po = meterMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<Meter> findPage(MeterKind kind, String keyword, long offset, long pageSize) {
        long pageNo = offset / pageSize + 1;
        Page<MeterPO> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<MeterPO> wrapper = buildWrapper(kind, keyword);
        Page<MeterPO> result = meterMapper.selectPage(page, wrapper);
        return result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(MeterKind kind, String keyword) {
        LambdaQueryWrapper<MeterPO> wrapper = buildWrapper(kind, keyword);
        return meterMapper.selectCount(wrapper);
    }

    @Override
    public void deleteById(Long id) {
        meterMapper.deleteById(id);
    }

    private LambdaQueryWrapper<MeterPO> buildWrapper(MeterKind kind, String keyword) {
        LambdaQueryWrapper<MeterPO> wrapper = new LambdaQueryWrapper<>();
        if (kind != null) {
            wrapper.eq(MeterPO::getMeterKind, kind.name());
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(MeterPO::getMeterCode, keyword)
                    .or()
                    .like(MeterPO::getInstallLocation, keyword);
        }
        wrapper.orderByDesc(MeterPO::getCreatedAt);
        return wrapper;
    }

    private MeterPO toPO(Meter meter) {
        MeterPO po = new MeterPO();
        po.setId(meter.getId());
        po.setMeterCode(meter.getMeterCode());
        po.setMeterKind(meter.getMeterKind().name());
        po.setInstallLocation(meter.getInstallLocation());
        po.setIsMainMeter(meter.isMainMeter());
        po.setCreatedAt(meter.getCreatedAt());
        po.setUpdatedAt(meter.getUpdatedAt());
        return po;
    }

    private Meter toDomain(MeterPO po) {
        return new Meter(
                po.getId(),
                po.getMeterCode(),
                MeterKind.fromString(po.getMeterKind()),
                po.getInstallLocation(),
                Boolean.TRUE.equals(po.getIsMainMeter()),
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }
}

