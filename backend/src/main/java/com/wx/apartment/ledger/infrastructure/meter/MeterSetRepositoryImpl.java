package com.wx.apartment.ledger.infrastructure.meter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.apartment.ledger.domain.meter.MeterSet;
import com.wx.apartment.ledger.domain.meter.MeterSetRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MeterSetRepositoryImpl implements MeterSetRepository {

    private final MeterSetMapper meterSetMapper;
    private final MeterSetItemMapper meterSetItemMapper;

    public MeterSetRepositoryImpl(MeterSetMapper meterSetMapper,
                                  MeterSetItemMapper meterSetItemMapper) {
        this.meterSetMapper = meterSetMapper;
        this.meterSetItemMapper = meterSetItemMapper;
    }

    @Override
    public MeterSet save(MeterSet meterSet) {
        MeterSetPO po = toPO(meterSet);
        if (po.getId() == null) {
            meterSetMapper.insert(po);
        } else {
            meterSetMapper.updateById(po);
            // 清理旧的子表关联
            LambdaQueryWrapper<MeterSetItemPO> delWrapper = new LambdaQueryWrapper<>();
            delWrapper.eq(MeterSetItemPO::getSetId, po.getId());
            meterSetItemMapper.delete(delWrapper);
        }
        // 新建子表关联
        if (meterSet.getChildMeterIds() != null) {
            LocalDateTime now = LocalDateTime.now();
            for (Long childId : meterSet.getChildMeterIds()) {
                MeterSetItemPO item = new MeterSetItemPO();
                item.setSetId(po.getId());
                item.setMeterId(childId);
                item.setCreatedAt(now);
                meterSetItemMapper.insert(item);
            }
        }
        return findById(po.getId()).orElseThrow();
    }

    @Override
    public Optional<MeterSet> findById(Long id) {
        MeterSetPO po = meterSetMapper.selectById(id);
        if (po == null) {
            return Optional.empty();
        }
        List<Long> childIds = loadChildIds(po.getId());
        return Optional.of(toDomain(po, childIds));
    }

    @Override
    public Optional<MeterSet> findByMainMeterId(Long mainMeterId) {
        LambdaQueryWrapper<MeterSetPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MeterSetPO::getMainMeterId, mainMeterId);
        MeterSetPO po = meterSetMapper.selectOne(wrapper);
        if (po == null) {
            return Optional.empty();
        }
        List<Long> childIds = loadChildIds(po.getId());
        return Optional.of(toDomain(po, childIds));
    }

    @Override
    public void deleteById(Long id) {
        LambdaQueryWrapper<MeterSetItemPO> delWrapper = new LambdaQueryWrapper<>();
        delWrapper.eq(MeterSetItemPO::getSetId, id);
        meterSetItemMapper.delete(delWrapper);
        meterSetMapper.deleteById(id);
    }

    private List<Long> loadChildIds(Long setId) {
        LambdaQueryWrapper<MeterSetItemPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MeterSetItemPO::getSetId, setId);
        List<MeterSetItemPO> items = meterSetItemMapper.selectList(wrapper);
        return items.stream().map(MeterSetItemPO::getMeterId).collect(Collectors.toList());
    }

    private MeterSetPO toPO(MeterSet meterSet) {
        MeterSetPO po = new MeterSetPO();
        po.setId(meterSet.getId());
        po.setSetName(meterSet.getSetName());
        po.setMainMeterId(meterSet.getMainMeterId());
        po.setCreatedAt(meterSet.getCreatedAt());
        po.setUpdatedAt(meterSet.getUpdatedAt());
        return po;
    }

    private MeterSet toDomain(MeterSetPO po, List<Long> childIds) {
        return new MeterSet(
                po.getId(),
                po.getSetName(),
                po.getMainMeterId(),
                childIds,
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }
}

