package com.wx.apartment.ledger.application.house;

import com.wx.apartment.ledger.application.common.PageResult;
import com.wx.apartment.ledger.application.house.dto.HouseCmd;
import com.wx.apartment.ledger.application.house.dto.HouseDetailDTO;
import com.wx.apartment.ledger.application.house.dto.HousePageQuery;
import com.wx.apartment.ledger.domain.house.House;
import com.wx.apartment.ledger.domain.house.HouseMeterRepository;
import com.wx.apartment.ledger.domain.house.HouseRepository;
import com.wx.apartment.ledger.domain.meter.Meter;
import com.wx.apartment.ledger.domain.meter.MeterKind;
import com.wx.apartment.ledger.domain.meter.MeterMonthlyUsage;
import com.wx.apartment.ledger.domain.meter.MeterMonthlyUsageRepository;
import com.wx.apartment.ledger.domain.meter.MeterRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HouseApplicationService {

    private final HouseRepository houseRepository;
    private final HouseMeterRepository houseMeterRepository;
    private final MeterRepository meterRepository;
    private final MeterMonthlyUsageRepository meterMonthlyUsageRepository;

    public HouseApplicationService(HouseRepository houseRepository,
                                   HouseMeterRepository houseMeterRepository,
                                   MeterRepository meterRepository,
                                   MeterMonthlyUsageRepository meterMonthlyUsageRepository) {
        this.houseRepository = houseRepository;
        this.houseMeterRepository = houseMeterRepository;
        this.meterRepository = meterRepository;
        this.meterMonthlyUsageRepository = meterMonthlyUsageRepository;
    }

    public Long create(HouseCmd cmd) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate checkInDate = parseDate(cmd.getCheckInDate());
        House house = new House(
                null,
                cmd.getHouseCode(),
                cmd.getHouseName(),
                cmd.getLocationText(),
                cmd.getRentAmount(),
                cmd.getDepositAmount(),
                checkInDate,
                cmd.getCurrentTenantId(),
                null,
                null,
                now,
                now
        );
        House saved = houseRepository.save(house);
        houseMeterRepository.replaceByHouseId(saved.getId(), cmd.getMeterIds());
        return saved.getId();
    }

    public void update(Long id, HouseCmd cmd) {
        House existing = houseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("房屋不存在，id=" + id));
        LocalDate checkInDate = parseDate(cmd.getCheckInDate());
        House updated = existing.updateBasicInfo(
                cmd.getHouseName(),
                cmd.getLocationText(),
                cmd.getRentAmount(),
                cmd.getDepositAmount(),
                checkInDate,
                cmd.getCurrentTenantId(),
                null,
                null,
                LocalDateTime.now()
        );
        houseRepository.save(updated);
    }

    public HouseDetailDTO getDetail(Long id) {
        House house = houseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("房屋不存在，id=" + id));
        return toDTO(house);
    }

    public void delete(Long id) {
        houseMeterRepository.deleteByHouseId(id);
        houseRepository.deleteById(id);
    }

    public PageResult<HouseDetailDTO> pageQuery(HousePageQuery query) {
        long pageNo = query.getPageNo() <= 0 ? 1 : query.getPageNo();
        long pageSize = query.getPageSize() <= 0 ? 10 : query.getPageSize();
        long offset = (pageNo - 1) * pageSize;

        String keyword = StringUtils.hasText(query.getKeyword()) ? query.getKeyword().trim() : null;
        long total = houseRepository.countByKeyword(keyword);
        List<House> list = houseRepository.findPage(keyword, offset, pageSize);
        List<HouseDetailDTO> records = list.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(pageNo, pageSize, total, records);
    }

    private HouseDetailDTO toDTO(House house) {
        HouseDetailDTO dto = new HouseDetailDTO();
        dto.setId(house.getId());
        dto.setHouseCode(house.getHouseCode());
        dto.setHouseName(house.getHouseName());
        dto.setLocationText(house.getLocationText());
        dto.setRentAmount(house.getRentAmount());
        dto.setDepositAmount(house.getDepositAmount());
        dto.setCheckInDate(house.getCheckInDate());
        dto.setCurrentTenantId(house.getCurrentTenantId());
        List<Long> meterIds = houseMeterRepository.findMeterIdsByHouseId(house.getId());
        dto.setMeterIds(meterIds);
        fillCurrentMonthUsage(dto, meterIds);
        dto.setCreatedAt(house.getCreatedAt());
        dto.setUpdatedAt(house.getUpdatedAt());
        return dto;
    }

    private LocalDate parseDate(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return LocalDate.parse(text.trim());
    }

    /**
     * 计算本月水、电总用量并写入 DTO。
     * 这里按计量表逐个查询，房屋数量通常有限，性能可接受。
     */
    private void fillCurrentMonthUsage(HouseDetailDTO dto, List<Long> meterIds) {
        if (meterIds == null || meterIds.isEmpty()) {
            return;
        }
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        BigDecimal electricTotal = BigDecimal.ZERO;
        BigDecimal waterTotal = BigDecimal.ZERO;

        for (Long meterId : meterIds) {
            Optional<Meter> meterOpt = meterRepository.findById(meterId);
            if (meterOpt.isEmpty()) {
                continue;
            }
            Meter meter = meterOpt.get();
            Optional<MeterMonthlyUsage> usageOpt =
                    meterMonthlyUsageRepository.findByMeterAndYearMonth(meterId, year, month);
            if (usageOpt.isEmpty()) {
                continue;
            }
            MeterMonthlyUsage usage = usageOpt.get();
            if (usage.getUsageValue() == null) {
                continue;
            }
            if (meter.getMeterKind() == MeterKind.ELECTRIC) {
                electricTotal = electricTotal.add(usage.getUsageValue());
            } else if (meter.getMeterKind() == MeterKind.WATER) {
                waterTotal = waterTotal.add(usage.getUsageValue());
            }
        }

        if (electricTotal.compareTo(BigDecimal.ZERO) > 0) {
            dto.setElectricUsageThisMonth(electricTotal);
        }
        if (waterTotal.compareTo(BigDecimal.ZERO) > 0) {
            dto.setWaterUsageThisMonth(waterTotal);
        }
    }
}

