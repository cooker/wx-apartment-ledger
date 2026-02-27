package com.wx.apartment.ledger.infrastructure.price;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.apartment.ledger.domain.price.ChargeKind;
import com.wx.apartment.ledger.domain.price.PriceConfig;
import com.wx.apartment.ledger.domain.price.PriceConfigRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PriceConfigRepositoryImpl implements PriceConfigRepository {

    private final PriceConfigMapper priceConfigMapper;

    public PriceConfigRepositoryImpl(PriceConfigMapper priceConfigMapper) {
        this.priceConfigMapper = priceConfigMapper;
    }

    @Override
    public PriceConfig save(PriceConfig priceConfig) {
        PriceConfigPO po = toPO(priceConfig);
        if (po.getId() == null) {
            priceConfigMapper.insert(po);
        } else {
            priceConfigMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<PriceConfig> findById(Long id) {
        PriceConfigPO po = priceConfigMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<PriceConfig> findCurrentByKind(ChargeKind kind, LocalDate date) {
        LambdaQueryWrapper<PriceConfigPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PriceConfigPO::getChargeKind, kind.name())
                .le(PriceConfigPO::getEffectiveDate, date)
                .and(w -> w.isNull(PriceConfigPO::getExpiredDate)
                        .or()
                        .ge(PriceConfigPO::getExpiredDate, date))
                .orderByDesc(PriceConfigPO::getEffectiveDate)
                .orderByDesc(PriceConfigPO::getId)
                .last("limit 1");
        PriceConfigPO po = priceConfigMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<PriceConfig> listByKind(ChargeKind kind) {
        LambdaQueryWrapper<PriceConfigPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PriceConfigPO::getChargeKind, kind.name())
                .orderByDesc(PriceConfigPO::getEffectiveDate)
                .orderByDesc(PriceConfigPO::getId);
        List<PriceConfigPO> list = priceConfigMapper.selectList(wrapper);
        return list.stream().map(this::toDomain).collect(Collectors.toList());
    }

    private PriceConfigPO toPO(PriceConfig config) {
        PriceConfigPO po = new PriceConfigPO();
        po.setId(config.getId());
        po.setChargeKind(config.getChargeKind().name());
        po.setUnitPrice(config.getUnitPrice());
        po.setEffectiveDate(config.getEffectiveDate());
        po.setExpiredDate(config.getExpiredDate());
        LocalDateTime createdAt = config.getCreatedAt();
        LocalDateTime updatedAt = config.getUpdatedAt();
        po.setCreatedAt(createdAt);
        po.setUpdatedAt(updatedAt);
        return po;
    }

    private PriceConfig toDomain(PriceConfigPO po) {
        return new PriceConfig(
                po.getId(),
                ChargeKind.fromString(po.getChargeKind()),
                po.getUnitPrice(),
                po.getEffectiveDate(),
                po.getExpiredDate(),
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }
}

