package com.wx.apartment.ledger.application.meter;

import com.wx.apartment.ledger.application.meter.dto.MeterSetCmd;
import com.wx.apartment.ledger.application.meter.dto.MeterSetDTO;
import com.wx.apartment.ledger.domain.meter.Meter;
import com.wx.apartment.ledger.domain.meter.MeterRepository;
import com.wx.apartment.ledger.domain.meter.MeterSet;
import com.wx.apartment.ledger.domain.meter.MeterSetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class MeterSetApplicationService {

    private final MeterRepository meterRepository;
    private final MeterSetRepository meterSetRepository;

    public MeterSetApplicationService(MeterRepository meterRepository,
                                      MeterSetRepository meterSetRepository) {
        this.meterRepository = meterRepository;
        this.meterSetRepository = meterSetRepository;
    }

    public Long create(MeterSetCmd cmd) {
        Meter main = meterRepository.findById(cmd.getMainMeterId())
                .orElseThrow(() -> new NoSuchElementException("主表不存在，id=" + cmd.getMainMeterId()));
        LocalDateTime now = LocalDateTime.now();
        MeterSet meterSet = new MeterSet(
                null,
                cmd.getSetName(),
                main.getId(),
                cmd.getChildMeterIds(),
                now,
                now
        );
        MeterSet saved = meterSetRepository.save(meterSet);
        return saved.getId();
    }

    public void update(Long id, MeterSetCmd cmd) {
        MeterSet existing = meterSetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("计量表集合不存在，id=" + id));
        if (!existing.getMainMeterId().equals(cmd.getMainMeterId())) {
            throw new IllegalArgumentException("不允许修改主表ID");
        }
        MeterSet updated = existing.updateChildren(cmd.getChildMeterIds(), LocalDateTime.now());
        meterSetRepository.save(updated);
    }

    public MeterSetDTO getByMainMeterId(Long mainMeterId) {
        MeterSet meterSet = meterSetRepository.findByMainMeterId(mainMeterId)
                .orElseThrow(() -> new NoSuchElementException("未找到对应的计量表集合，主表ID=" + mainMeterId));
        return toDTO(meterSet);
    }

    public void delete(Long id) {
        meterSetRepository.deleteById(id);
    }

    private MeterSetDTO toDTO(MeterSet meterSet) {
        MeterSetDTO dto = new MeterSetDTO();
        dto.setId(meterSet.getId());
        dto.setSetName(meterSet.getSetName());
        dto.setMainMeterId(meterSet.getMainMeterId());
        dto.setChildMeterIds(meterSet.getChildMeterIds());
        dto.setCreatedAt(meterSet.getCreatedAt());
        dto.setUpdatedAt(meterSet.getUpdatedAt());
        return dto;
    }
}

