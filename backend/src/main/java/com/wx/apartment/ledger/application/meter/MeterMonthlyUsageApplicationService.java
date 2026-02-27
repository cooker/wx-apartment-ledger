package com.wx.apartment.ledger.application.meter;

import com.wx.apartment.ledger.application.common.PageResult;
import com.wx.apartment.ledger.application.meter.dto.MeterMonthlyUsageCmd;
import com.wx.apartment.ledger.application.meter.dto.MeterMonthlyUsageDTO;
import com.wx.apartment.ledger.application.meter.dto.MeterMonthlyUsageQuery;
import com.wx.apartment.ledger.domain.meter.Meter;
import com.wx.apartment.ledger.domain.meter.MeterMonthlyUsage;
import com.wx.apartment.ledger.domain.meter.MeterMonthlyUsageRepository;
import com.wx.apartment.ledger.domain.meter.MeterRepository;
import com.wx.apartment.ledger.domain.meter.MeterKind;
import com.wx.apartment.ledger.domain.price.ChargeKind;
import com.wx.apartment.ledger.domain.price.PriceConfig;
import com.wx.apartment.ledger.domain.price.PriceConfigRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class MeterMonthlyUsageApplicationService {

    private final MeterRepository meterRepository;
    private final MeterMonthlyUsageRepository meterMonthlyUsageRepository;
    private final PriceConfigRepository priceConfigRepository;

    public MeterMonthlyUsageApplicationService(MeterRepository meterRepository,
                                               MeterMonthlyUsageRepository meterMonthlyUsageRepository,
                                               PriceConfigRepository priceConfigRepository) {
        this.meterRepository = meterRepository;
        this.meterMonthlyUsageRepository = meterMonthlyUsageRepository;
        this.priceConfigRepository = priceConfigRepository;
    }

    public Long upsertUsage(MeterMonthlyUsageCmd cmd) {
        Meter meter = meterRepository.findById(cmd.getMeterId())
                .orElseThrow(() -> new NoSuchElementException("计量表不存在，id=" + cmd.getMeterId()));
        LocalDateTime now = LocalDateTime.now();
        // 若未显式传入上月读数，则尝试根据上一期记录自动填充
        var previousReading = cmd.getPreviousReading();
        if (previousReading == null) {
            int year = cmd.getUsageYear();
            int month = cmd.getUsageMonth();
            int prevYear = month == 1 ? year - 1 : year;
            int prevMonth = month == 1 ? 12 : month - 1;
            if (prevYear > 0) {
                meterMonthlyUsageRepository
                        .findByMeterAndYearMonth(cmd.getMeterId(), prevYear, prevMonth)
                        .ifPresent(prev -> {
                            // 使用上一期的本月读数作为本期的上月读数
                            cmd.setPreviousReading(prev.getCurrentReading());
                        });
            }
        }
        MeterMonthlyUsage usage = meterMonthlyUsageRepository
                .findByMeterAndYearMonth(cmd.getMeterId(), cmd.getUsageYear(), cmd.getUsageMonth())
                .map(existing -> existing.update(cmd.getPreviousReading(), cmd.getCurrentReading(), now))
                .orElseGet(() -> MeterMonthlyUsage.create(
                        meter.getId(),
                        cmd.getUsageYear(),
                        cmd.getUsageMonth(),
                        cmd.getPreviousReading(),
                        cmd.getCurrentReading(),
                        now
                ));
        MeterMonthlyUsage saved = meterMonthlyUsageRepository.save(usage);
        return saved.getId();
    }

    public MeterMonthlyUsageDTO getByMonth(Long meterId, Integer year, Integer month) {
        if (year == null || month == null) {
            throw new IllegalArgumentException("年份和月份不能为空");
        }
        Meter meter = meterRepository.findById(meterId)
                .orElseThrow(() -> new NoSuchElementException("计量表不存在，id=" + meterId));
        MeterMonthlyUsage usage = meterMonthlyUsageRepository
                .findByMeterAndYearMonth(meterId, year, month)
                .orElseThrow(() -> new NoSuchElementException("未找到对应的月度用量记录"));
        return toDTO(usage, meter);
    }

    public PageResult<MeterMonthlyUsageDTO> pageByMeter(MeterMonthlyUsageQuery query) {
        if (query.getMeterId() == null) {
            throw new IllegalArgumentException("计量表ID不能为空");
        }
        long pageNo = query.getPageNo() <= 0 ? 1 : query.getPageNo();
        long pageSize = query.getPageSize() <= 0 ? 10 : query.getPageSize();
        long offset = (pageNo - 1) * pageSize;

        Meter meter = meterRepository.findById(query.getMeterId())
                .orElseThrow(() -> new NoSuchElementException("计量表不存在，id=" + query.getMeterId()));
        long total = meterMonthlyUsageRepository.countByMeter(query.getMeterId(), query.getUsageYear());
        List<MeterMonthlyUsage> list = meterMonthlyUsageRepository
                .findPageByMeter(query.getMeterId(), query.getUsageYear(), offset, pageSize);
        List<MeterMonthlyUsageDTO> records = list.stream()
                .map(usage -> toDTO(usage, meter))
                .collect(Collectors.toList());
        return new PageResult<>(pageNo, pageSize, total, records);
    }

    public void delete(Long id) {
        meterMonthlyUsageRepository.deleteById(id);
    }

    private MeterMonthlyUsageDTO toDTO(MeterMonthlyUsage usage, Meter meter) {
        MeterMonthlyUsageDTO dto = new MeterMonthlyUsageDTO();
        dto.setId(usage.getId());
        dto.setMeterId(usage.getMeterId());
        dto.setUsageYear(usage.getUsageYear());
        dto.setUsageMonth(usage.getUsageMonth());
        dto.setPreviousReading(usage.getPreviousReading());
        dto.setCurrentReading(usage.getCurrentReading());
        dto.setUsageValue(usage.getUsageValue());
        // 计算单价与费用
        if (usage.getUsageValue() != null) {
            MeterKind meterKind = meter.getMeterKind();
            ChargeKind chargeKind = meterKind == MeterKind.ELECTRIC ? ChargeKind.ELECTRIC : ChargeKind.WATER;
            LocalDate chargeDate = LocalDate.of(usage.getUsageYear(), usage.getUsageMonth(), 1);
            PriceConfig config = priceConfigRepository.findCurrentByKind(chargeKind, chargeDate).orElse(null);
            if (config != null && config.getUnitPrice() != null) {
                BigDecimal unitPrice = config.getUnitPrice();
                BigDecimal totalAmount = unitPrice.multiply(usage.getUsageValue());
                dto.setUnitPrice(unitPrice);
                dto.setTotalAmount(totalAmount);
            }
        }
        dto.setCreatedAt(usage.getCreatedAt());
        dto.setUpdatedAt(usage.getUpdatedAt());
        return dto;
    }
}

