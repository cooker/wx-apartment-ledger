package com.wx.apartment.ledger.infrastructure.house;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wx.apartment.ledger.domain.house.House;
import com.wx.apartment.ledger.domain.house.HouseRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class HouseRepositoryImpl implements HouseRepository {

    private final HouseMapper houseMapper;

    public HouseRepositoryImpl(HouseMapper houseMapper) {
        this.houseMapper = houseMapper;
    }

    @Override
    public House save(House house) {
        HousePO po = toPO(house);
        if (po.getId() == null) {
            houseMapper.insert(po);
        } else {
            houseMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<House> findById(Long id) {
        HousePO po = houseMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<House> findPage(String keyword, long offset, long pageSize) {
        long pageNo = offset / pageSize + 1;
        Page<HousePO> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<HousePO> wrapper = buildWrapper(keyword);
        Page<HousePO> result = houseMapper.selectPage(page, wrapper);
        return result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByKeyword(String keyword) {
        LambdaQueryWrapper<HousePO> wrapper = buildWrapper(keyword);
        return houseMapper.selectCount(wrapper);
    }

    @Override
    public void deleteById(Long id) {
        houseMapper.deleteById(id);
    }

    private LambdaQueryWrapper<HousePO> buildWrapper(String keyword) {
        LambdaQueryWrapper<HousePO> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper
                    .like(HousePO::getHouseCode, keyword)
                    .or()
                    .like(HousePO::getHouseName, keyword)
                    .or()
                    .like(HousePO::getLocationText, keyword);
        }
        wrapper.orderByDesc(HousePO::getCreatedAt);
        return wrapper;
    }

    private HousePO toPO(House house) {
        HousePO po = new HousePO();
        po.setId(house.getId());
        po.setHouseCode(house.getHouseCode());
        po.setHouseName(house.getHouseName());
        po.setLocationText(house.getLocationText());
        po.setRentAmount(house.getRentAmount());
        po.setDepositAmount(house.getDepositAmount());
        po.setCheckInDate(house.getCheckInDate());
        po.setCurrentTenantId(house.getCurrentTenantId());
        po.setElectricMeterId(house.getElectricMeterId());
        po.setWaterMeterId(house.getWaterMeterId());
        po.setCreatedAt(house.getCreatedAt());
        po.setUpdatedAt(house.getUpdatedAt());
        return po;
    }

    private House toDomain(HousePO po) {
        return new House(
                po.getId(),
                po.getHouseCode(),
                po.getHouseName(),
                po.getLocationText(),
                po.getRentAmount(),
                po.getDepositAmount(),
                po.getCheckInDate(),
                po.getCurrentTenantId(),
                po.getElectricMeterId(),
                po.getWaterMeterId(),
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }
}

