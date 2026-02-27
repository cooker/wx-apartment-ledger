package com.wx.apartment.ledger.infrastructure.sharedplace;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlace;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlaceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SharedPlaceRepositoryImpl implements SharedPlaceRepository {

    private final SharedPlaceMapper sharedPlaceMapper;

    public SharedPlaceRepositoryImpl(SharedPlaceMapper sharedPlaceMapper) {
        this.sharedPlaceMapper = sharedPlaceMapper;
    }

    @Override
    public SharedPlace save(SharedPlace sharedPlace) {
        SharedPlacePO po = toPO(sharedPlace);
        if (po.getId() == null) {
            sharedPlaceMapper.insert(po);
        } else {
            sharedPlaceMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<SharedPlace> findById(Long id) {
        SharedPlacePO po = sharedPlaceMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<SharedPlace> findPage(String keyword, long offset, long pageSize) {
        long pageNo = offset / pageSize + 1;
        Page<SharedPlacePO> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<SharedPlacePO> wrapper = buildWrapper(keyword);
        Page<SharedPlacePO> result = sharedPlaceMapper.selectPage(page, wrapper);
        return result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(String keyword) {
        LambdaQueryWrapper<SharedPlacePO> wrapper = buildWrapper(keyword);
        return sharedPlaceMapper.selectCount(wrapper);
    }

    @Override
    public void deleteById(Long id) {
        sharedPlaceMapper.deleteById(id);
    }

    private LambdaQueryWrapper<SharedPlacePO> buildWrapper(String keyword) {
        LambdaQueryWrapper<SharedPlacePO> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SharedPlacePO::getPlaceName, keyword);
        }
        wrapper.orderByDesc(SharedPlacePO::getCreatedAt);
        return wrapper;
    }

    private SharedPlacePO toPO(SharedPlace place) {
        SharedPlacePO po = new SharedPlacePO();
        po.setId(place.getId());
        po.setPlaceName(place.getPlaceName());
        po.setElectricMeterId(place.getElectricMeterId());
        po.setWaterMeterId(place.getWaterMeterId());
        po.setRelatedTenantId(place.getRelatedTenantId());
        po.setCreatedAt(place.getCreatedAt());
        po.setUpdatedAt(place.getUpdatedAt());
        return po;
    }

    private SharedPlace toDomain(SharedPlacePO po) {
        return new SharedPlace(
                po.getId(),
                po.getPlaceName(),
                po.getElectricMeterId(),
                po.getWaterMeterId(),
                po.getRelatedTenantId(),
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }
}

