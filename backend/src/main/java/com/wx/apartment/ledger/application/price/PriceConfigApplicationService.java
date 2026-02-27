package com.wx.apartment.ledger.application.price;

import com.wx.apartment.ledger.application.price.dto.PriceConfigCmd;
import com.wx.apartment.ledger.application.price.dto.PriceConfigDTO;
import com.wx.apartment.ledger.domain.price.ChargeKind;
import com.wx.apartment.ledger.domain.price.PriceConfig;
import com.wx.apartment.ledger.domain.price.PriceConfigRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class PriceConfigApplicationService {

    private final PriceConfigRepository priceConfigRepository;

    public PriceConfigApplicationService(PriceConfigRepository priceConfigRepository) {
        this.priceConfigRepository = priceConfigRepository;
    }

    public Long create(PriceConfigCmd cmd) {
        ChargeKind kind = parseKind(cmd.getChargeKind());
        LocalDateTime now = LocalDateTime.now();
        PriceConfig config = new PriceConfig(
                null,
                kind,
                cmd.getUnitPrice(),
                cmd.getEffectiveDate(),
                cmd.getExpiredDate(),
                now,
                now
        );
        PriceConfig saved = priceConfigRepository.save(config);
        return saved.getId();
    }

    public void update(Long id, PriceConfigCmd cmd) {
        PriceConfig existing = priceConfigRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("水电单价配置不存在，id=" + id));
        ChargeKind kind = parseKind(cmd.getChargeKind());
        if (existing.getChargeKind() != kind) {
            throw new IllegalArgumentException("不允许修改费用类型");
        }
        PriceConfig updated = existing.update(
                cmd.getUnitPrice(),
                cmd.getEffectiveDate(),
                cmd.getExpiredDate(),
                LocalDateTime.now()
        );
        priceConfigRepository.save(updated);
    }

    public PriceConfigDTO getCurrent(String chargeKind) {
        ChargeKind kind = parseKind(chargeKind);
        PriceConfig config = priceConfigRepository.findCurrentByKind(kind, LocalDate.now())
                .orElseThrow(() -> new NoSuchElementException("当前没有生效的单价配置，类型=" + kind));
        return toDTO(config);
    }

    public List<PriceConfigDTO> listByKind(String chargeKind) {
        ChargeKind kind = parseKind(chargeKind);
        List<PriceConfig> list = priceConfigRepository.listByKind(kind);
        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private PriceConfigDTO toDTO(PriceConfig config) {
        PriceConfigDTO dto = new PriceConfigDTO();
        dto.setId(config.getId());
        dto.setChargeKind(config.getChargeKind().name());
        dto.setUnitPrice(config.getUnitPrice());
        dto.setEffectiveDate(config.getEffectiveDate());
        dto.setExpiredDate(config.getExpiredDate());
        dto.setCreatedAt(config.getCreatedAt());
        dto.setUpdatedAt(config.getUpdatedAt());
        return dto;
    }

    private ChargeKind parseKind(String value) {
        try {
            return ChargeKind.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("费用类型不合法，只能是 ELECTRIC 或 WATER", ex);
        }
    }
}

