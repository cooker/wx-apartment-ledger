package com.wx.apartment.ledger.application.sharedplace;

import com.wx.apartment.ledger.application.common.PageResult;
import com.wx.apartment.ledger.application.sharedplace.dto.SharedPlaceCmd;
import com.wx.apartment.ledger.application.sharedplace.dto.SharedPlaceDetailDTO;
import com.wx.apartment.ledger.application.sharedplace.dto.SharedPlacePageQuery;
import com.wx.apartment.ledger.application.sharedplace.dto.SharedPlaceShareCmd;
import com.wx.apartment.ledger.application.sharedplace.dto.SharedPlaceShareDTO;
import com.wx.apartment.ledger.domain.meter.Meter;
import com.wx.apartment.ledger.domain.meter.MeterKind;
import com.wx.apartment.ledger.domain.meter.MeterMonthlyUsage;
import com.wx.apartment.ledger.domain.meter.MeterMonthlyUsageRepository;
import com.wx.apartment.ledger.domain.meter.MeterRepository;
import com.wx.apartment.ledger.domain.price.ChargeKind;
import com.wx.apartment.ledger.domain.price.PriceConfig;
import com.wx.apartment.ledger.domain.price.PriceConfigRepository;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlace;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlaceMeterRepository;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlaceRepository;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlaceTenantRepository;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlaceTenantShareRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SharedPlaceApplicationService {

    private final SharedPlaceRepository sharedPlaceRepository;
    private final SharedPlaceMeterRepository sharedPlaceMeterRepository;
    private final SharedPlaceTenantRepository sharedPlaceTenantRepository;
    private final MeterRepository meterRepository;
    private final MeterMonthlyUsageRepository meterMonthlyUsageRepository;
    private final SharedPlaceTenantShareRepository sharedPlaceTenantShareRepository;
    private final PriceConfigRepository priceConfigRepository;

    public SharedPlaceApplicationService(SharedPlaceRepository sharedPlaceRepository,
                                        SharedPlaceMeterRepository sharedPlaceMeterRepository,
                                        SharedPlaceTenantRepository sharedPlaceTenantRepository,
                                        MeterRepository meterRepository,
                                        MeterMonthlyUsageRepository meterMonthlyUsageRepository,
                                        SharedPlaceTenantShareRepository sharedPlaceTenantShareRepository,
                                        PriceConfigRepository priceConfigRepository) {
        this.sharedPlaceRepository = sharedPlaceRepository;
        this.sharedPlaceMeterRepository = sharedPlaceMeterRepository;
        this.sharedPlaceTenantRepository = sharedPlaceTenantRepository;
        this.meterRepository = meterRepository;
        this.meterMonthlyUsageRepository = meterMonthlyUsageRepository;
        this.sharedPlaceTenantShareRepository = sharedPlaceTenantShareRepository;
        this.priceConfigRepository = priceConfigRepository;
    }

    public Long create(SharedPlaceCmd cmd) {
        LocalDateTime now = LocalDateTime.now();
        SharedPlace place = new SharedPlace(
                null,
                cmd.getPlaceName(),
                null,
                null,
                null,
                now,
                now
        );
        SharedPlace saved = sharedPlaceRepository.save(place);
        sharedPlaceMeterRepository.replaceByPlaceId(saved.getId(), cmd.getMeterIds());
        sharedPlaceTenantRepository.replaceByPlaceId(saved.getId(), cmd.getTenantIds());
        return saved.getId();
    }

    public void update(Long id, SharedPlaceCmd cmd) {
        SharedPlace existing = sharedPlaceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("公共场所不存在，id=" + id));
        SharedPlace updated = existing.update(
                cmd.getPlaceName(),
                null,
                null,
                null,
                LocalDateTime.now()
        );
        sharedPlaceRepository.save(updated);
        sharedPlaceMeterRepository.replaceByPlaceId(id, cmd.getMeterIds());
        sharedPlaceTenantRepository.replaceByPlaceId(id, cmd.getTenantIds());
    }

    public SharedPlaceDetailDTO getDetail(Long id) {
        SharedPlace place = sharedPlaceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("公共场所不存在，id=" + id));
        return toDTO(place);
    }

    public void delete(Long id) {
        sharedPlaceMeterRepository.deleteByPlaceId(id);
        sharedPlaceTenantRepository.deleteByPlaceId(id);
        sharedPlaceTenantShareRepository.deleteByPlaceId(id);
        sharedPlaceRepository.deleteById(id);
    }

    public PageResult<SharedPlaceDetailDTO> pageQuery(SharedPlacePageQuery query) {
        long pageNo = query.getPageNo() <= 0 ? 1 : query.getPageNo();
        long pageSize = query.getPageSize() <= 0 ? 10 : query.getPageSize();
        long offset = (pageNo - 1) * pageSize;

        String keyword = StringUtils.hasText(query.getKeyword()) ? query.getKeyword().trim() : null;
        long total = sharedPlaceRepository.count(keyword);
        List<SharedPlace> list = sharedPlaceRepository.findPage(keyword, offset, pageSize);
        List<SharedPlaceDetailDTO> records = list.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(pageNo, pageSize, total, records);
    }

    public SharedPlaceShareDTO getShareDetail(Long placeId, Integer year, Integer month) {
        if (placeId == null) {
            throw new IllegalArgumentException("公共场所ID不能为空");
        }
        if (year == null || month == null) {
            LocalDate today = LocalDate.now();
            year = today.getYear();
            month = today.getMonthValue();
        }
        SharedPlace place = sharedPlaceRepository.findById(placeId)
                .orElseThrow(() -> new NoSuchElementException("公共场所不存在，id=" + placeId));
        List<Long> meterIds = sharedPlaceMeterRepository.findMeterIdsByPlaceId(placeId);
        var totals = calcUsageForYearMonth(meterIds, year, month);

        SharedPlaceShareDTO dto = new SharedPlaceShareDTO();
        dto.setPlaceId(placeId);
        dto.setPlaceName(place.getPlaceName());
        dto.setUsageYear(year);
        dto.setUsageMonth(month);
        dto.setTotalElectricUsage(totals[0]);
        dto.setTotalWaterUsage(totals[1]);

        List<Long> tenantIds = sharedPlaceTenantRepository.findTenantIdsByPlaceId(placeId);
        List<SharedPlaceTenantShareRepository.SharedPlaceTenantShareRow> rows =
                sharedPlaceTenantShareRepository.listByPlaceAndYearMonth(placeId, year, month);

        for (Long tenantId : tenantIds) {
            SharedPlaceShareDTO.Item item = new SharedPlaceShareDTO.Item();
            item.setTenantId(tenantId);
            rows.stream()
                    .filter(r -> tenantId.equals(r.getTenantId()))
                    .findFirst()
                    .ifPresent(r -> {
                        item.setElectricUsage(r.getElectricUsage());
                        item.setWaterUsage(r.getWaterUsage());
                        item.setElectricAmount(r.getElectricAmount());
                        item.setWaterAmount(r.getWaterAmount());
                    });
            dto.getItems().add(item);
        }
        return dto;
    }

    public void saveShare(SharedPlaceShareCmd cmd) {
        Long placeId = cmd.getPlaceId();
        int year = cmd.getUsageYear();
        int month = cmd.getUsageMonth();
        if (placeId == null) {
            throw new IllegalArgumentException("公共场所ID不能为空");
        }
        if (year <= 0 || month <= 0 || month > 12) {
            throw new IllegalArgumentException("年份或月份不合法");
        }
        SharedPlace place = sharedPlaceRepository.findById(placeId)
                .orElseThrow(() -> new NoSuchElementException("公共场所不存在，id=" + placeId));
        List<Long> meterIds = sharedPlaceMeterRepository.findMeterIdsByPlaceId(placeId);
        var totals = calcUsageForYearMonth(meterIds, year, month);
        java.math.BigDecimal totalElectric = totals[0];
        java.math.BigDecimal totalWater = totals[1];

        java.math.BigDecimal sumElectric = java.math.BigDecimal.ZERO;
        java.math.BigDecimal sumWater = java.math.BigDecimal.ZERO;
        for (SharedPlaceShareCmd.Item item : cmd.getItems()) {
            if (item.getElectricUsage() != null) {
                sumElectric = sumElectric.add(item.getElectricUsage());
            }
            if (item.getWaterUsage() != null) {
                sumWater = sumWater.add(item.getWaterUsage());
            }
        }
        if (sumElectric.compareTo(totalElectric) > 0) {
            throw new IllegalArgumentException("电量分摊合计不能超过公共场所总电量");
        }
        if (sumWater.compareTo(totalWater) > 0) {
            throw new IllegalArgumentException("水量分摊合计不能超过公共场所总水量");
        }

        LocalDate chargeDate = LocalDate.of(year, month, 1);
        PriceConfig electricConfig = priceConfigRepository
                .findCurrentByKind(ChargeKind.ELECTRIC, chargeDate)
                .orElse(null);
        PriceConfig waterConfig = priceConfigRepository
                .findCurrentByKind(ChargeKind.WATER, chargeDate)
                .orElse(null);
        java.math.BigDecimal electricUnit = electricConfig != null ? electricConfig.getUnitPrice() : null;
        java.math.BigDecimal waterUnit = waterConfig != null ? waterConfig.getUnitPrice() : null;

        List<SharedPlaceTenantShareRepository.SharedPlaceTenantShareRow> rows = new ArrayList<>();
        for (SharedPlaceShareCmd.Item item : cmd.getItems()) {
            SharedPlaceTenantShareRepository.SharedPlaceTenantShareRow row =
                    new SharedPlaceTenantShareRepository.SharedPlaceTenantShareRow();
            row.setSharedPlaceId(placeId);
            row.setTenantId(item.getTenantId());
            row.setUsageYear(year);
            row.setUsageMonth(month);
            java.math.BigDecimal eUsage = item.getElectricUsage() == null
                    ? java.math.BigDecimal.ZERO : item.getElectricUsage();
            java.math.BigDecimal wUsage = item.getWaterUsage() == null
                    ? java.math.BigDecimal.ZERO : item.getWaterUsage();
            row.setElectricUsage(eUsage);
            row.setWaterUsage(wUsage);
            java.math.BigDecimal eAmount = electricUnit != null ? electricUnit.multiply(eUsage) : java.math.BigDecimal.ZERO;
            java.math.BigDecimal wAmount = waterUnit != null ? waterUnit.multiply(wUsage) : java.math.BigDecimal.ZERO;
            row.setElectricAmount(eAmount);
            row.setWaterAmount(wAmount);
            rows.add(row);
        }

        sharedPlaceTenantShareRepository.replaceForPlaceAndYearMonth(placeId, year, month, rows);
    }

    private SharedPlaceDetailDTO toDTO(SharedPlace place) {
        SharedPlaceDetailDTO dto = new SharedPlaceDetailDTO();
        dto.setId(place.getId());
        dto.setPlaceName(place.getPlaceName());
        List<Long> meterIds = sharedPlaceMeterRepository.findMeterIdsByPlaceId(place.getId());
        dto.setMeterIds(meterIds);
        dto.setTenantIds(sharedPlaceTenantRepository.findTenantIdsByPlaceId(place.getId()));
        fillCurrentMonthUsage(dto, meterIds);
        dto.setCreatedAt(place.getCreatedAt());
        dto.setUpdatedAt(place.getUpdatedAt());
        return dto;
    }

    /**
     * 计算本月水、电总用量并写入 DTO。
     * 为避免复杂 SQL，这里直接按计量表逐个查询，公共场所数量通常有限，性能可接受。
     */
    private void fillCurrentMonthUsage(SharedPlaceDetailDTO dto, List<Long> meterIds) {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        BigDecimal[] totals = calcUsageForYearMonth(meterIds, year, month);
        if (totals[0].compareTo(BigDecimal.ZERO) > 0) {
            dto.setElectricUsageThisMonth(totals[0]);
        }
        if (totals[1].compareTo(BigDecimal.ZERO) > 0) {
            dto.setWaterUsageThisMonth(totals[1]);
        }
    }

    private BigDecimal[] calcUsageForYearMonth(List<Long> meterIds, int year, int month) {
        BigDecimal electricTotal = BigDecimal.ZERO;
        BigDecimal waterTotal = BigDecimal.ZERO;
        if (meterIds == null || meterIds.isEmpty()) {
            return new BigDecimal[]{electricTotal, waterTotal};
        }
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
        return new BigDecimal[]{electricTotal, waterTotal};
    }
}

