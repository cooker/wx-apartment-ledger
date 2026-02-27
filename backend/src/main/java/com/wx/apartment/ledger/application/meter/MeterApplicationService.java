package com.wx.apartment.ledger.application.meter;

import com.wx.apartment.ledger.application.common.PageResult;
import com.wx.apartment.ledger.application.meter.dto.MeterCmd;
import com.wx.apartment.ledger.application.meter.dto.MeterDetailDTO;
import com.wx.apartment.ledger.application.meter.dto.MeterPageQuery;
import com.wx.apartment.ledger.domain.meter.Meter;
import com.wx.apartment.ledger.domain.meter.MeterKind;
import com.wx.apartment.ledger.domain.meter.MeterMonthlyUsage;
import com.wx.apartment.ledger.domain.meter.MeterMonthlyUsageRepository;
import com.wx.apartment.ledger.domain.meter.MeterRepository;
import com.wx.apartment.ledger.domain.meter.MeterSet;
import com.wx.apartment.ledger.domain.meter.MeterSetRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class MeterApplicationService {

    private final MeterRepository meterRepository;
    private final MeterSetRepository meterSetRepository;
    private final MeterMonthlyUsageRepository meterMonthlyUsageRepository;

    public MeterApplicationService(MeterRepository meterRepository,
                                   MeterSetRepository meterSetRepository,
                                   MeterMonthlyUsageRepository meterMonthlyUsageRepository) {
        this.meterRepository = meterRepository;
        this.meterSetRepository = meterSetRepository;
        this.meterMonthlyUsageRepository = meterMonthlyUsageRepository;
    }

    public Long create(MeterCmd cmd) {
        MeterKind kind = parseKind(cmd.getMeterKind());
        LocalDateTime now = LocalDateTime.now();
        boolean main = Boolean.TRUE.equals(cmd.getMainMeter());
        Meter meter = new Meter(
                null,
                cmd.getMeterCode(),
                kind,
                cmd.getInstallLocation(),
                main,
                now,
                now
        );
        Meter saved = meterRepository.save(meter);
        return saved.getId();
    }

    public void update(Long id, MeterCmd cmd) {
        Meter existing = meterRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("计量表不存在，id=" + id));
        MeterKind kind = parseKind(cmd.getMeterKind());
        if (existing.getMeterKind() != kind) {
            throw new IllegalArgumentException("不允许修改计量表类型");
        }
        boolean main = Boolean.TRUE.equals(cmd.getMainMeter());
        Meter updated = existing.updateBasicInfo(
                cmd.getInstallLocation(),
                main,
                LocalDateTime.now()
        );
        meterRepository.save(updated);
    }

    public MeterDetailDTO getDetail(Long id) {
        Meter meter = meterRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("计量表不存在，id=" + id));
        return toDTO(meter);
    }

    public void delete(Long id) {
        meterRepository.deleteById(id);
    }

    public PageResult<MeterDetailDTO> pageQuery(MeterPageQuery query) {
        long pageNo = query.getPageNo() <= 0 ? 1 : query.getPageNo();
        long pageSize = query.getPageSize() <= 0 ? 10 : query.getPageSize();
        long offset = (pageNo - 1) * pageSize;

        MeterKind kind = null;
        if (StringUtils.hasText(query.getMeterKind())) {
            kind = parseKind(query.getMeterKind());
        }
        String keyword = StringUtils.hasText(query.getKeyword()) ? query.getKeyword().trim() : null;

        long total = meterRepository.count(kind, keyword);
        List<Meter> list = meterRepository.findPage(kind, keyword, offset, pageSize);

        // 计算当前月份总表与子表用量差异
        Map<Long, BigDecimal> diffMap = calculateCurrentMonthDiff(list);

        List<MeterDetailDTO> records = list.stream()
                .map(meter -> {
                    MeterDetailDTO dto = toDTO(meter);
                    BigDecimal diff = diffMap.get(meter.getId());
                    dto.setCurrentMonthDiff(diff);
                    return dto;
                })
                .collect(Collectors.toList());
        return new PageResult<>(pageNo, pageSize, total, records);
    }

    private Map<Long, BigDecimal> calculateCurrentMonthDiff(List<Meter> meters) {
        Map<Long, BigDecimal> result = new HashMap<>();
        if (meters == null || meters.isEmpty()) {
            return result;
        }
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        for (Meter meter : meters) {
            if (!meter.isMainMeter()) {
                continue;
            }
            MeterSet set = meterSetRepository.findByMainMeterId(meter.getId()).orElse(null);
            if (set == null || set.getChildMeterIds().isEmpty()) {
                continue;
            }
            MeterMonthlyUsage mainUsage = meterMonthlyUsageRepository
                    .findByMeterAndYearMonth(meter.getId(), year, month)
                    .orElse(null);
            if (mainUsage == null || mainUsage.getUsageValue() == null) {
                continue;
            }
            BigDecimal childSum = BigDecimal.ZERO;
            for (Long childId : set.getChildMeterIds()) {
                MeterMonthlyUsage childUsage = meterMonthlyUsageRepository
                        .findByMeterAndYearMonth(childId, year, month)
                        .orElse(null);
                if (childUsage != null && childUsage.getUsageValue() != null) {
                    childSum = childSum.add(childUsage.getUsageValue());
                }
            }
            BigDecimal diff = mainUsage.getUsageValue().subtract(childSum);
            result.put(meter.getId(), diff);
        }
        return result;
    }

    private MeterDetailDTO toDTO(Meter meter) {
        MeterDetailDTO dto = new MeterDetailDTO();
        dto.setId(meter.getId());
        dto.setMeterCode(meter.getMeterCode());
        dto.setMeterKind(meter.getMeterKind().name());
        dto.setInstallLocation(meter.getInstallLocation());
        dto.setMainMeter(meter.isMainMeter());
        dto.setCreatedAt(meter.getCreatedAt());
        dto.setUpdatedAt(meter.getUpdatedAt());
        return dto;
    }

    private MeterKind parseKind(String value) {
        try {
            return MeterKind.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("计量表类型不合法，只能是 ELECTRIC 或 WATER", ex);
        }
    }
}

