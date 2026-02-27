package com.wx.apartment.ledger.application.tenant;

import com.wx.apartment.ledger.application.common.PageResult;
import com.wx.apartment.ledger.application.tenant.dto.TenantCmd;
import com.wx.apartment.ledger.application.tenant.dto.TenantDetailDTO;
import com.wx.apartment.ledger.application.tenant.dto.TenantPageQuery;
import com.wx.apartment.ledger.domain.tenant.Tenant;
import com.wx.apartment.ledger.domain.tenant.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class TenantApplicationService {

    private final TenantRepository tenantRepository;

    public TenantApplicationService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Long createTenant(TenantCmd cmd) {
        LocalDateTime now = LocalDateTime.now();
        Tenant tenant = new Tenant(
                null,
                cmd.getFullName(),
                cmd.getMobileNumber(),
                cmd.getWechatId(),
                cmd.getRemarkText(),
                now,
                now
        );
        Tenant saved = tenantRepository.save(tenant);
        return saved.getId();
    }

    public void updateTenant(Long id, TenantCmd cmd) {
        Tenant existing = tenantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("租客不存在，id=" + id));
        Tenant updated = existing.updateBasicInfo(
                cmd.getFullName(),
                cmd.getMobileNumber(),
                cmd.getWechatId(),
                cmd.getRemarkText(),
                LocalDateTime.now()
        );
        tenantRepository.save(updated);
    }

    public TenantDetailDTO getTenantDetail(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("租客不存在，id=" + id));
        return toDetailDTO(tenant);
    }

    public PageResult<TenantDetailDTO> pageQuery(TenantPageQuery query) {
        long pageNo = query.getPageNo() <= 0 ? 1 : query.getPageNo();
        long pageSize = query.getPageSize() <= 0 ? 10 : query.getPageSize();
        long offset = (pageNo - 1) * pageSize;

        String keyword = StringUtils.hasText(query.getKeyword()) ? query.getKeyword().trim() : null;
        long total = tenantRepository.countByKeyword(keyword);
        List<Tenant> list = tenantRepository.findPage(keyword, offset, pageSize);
        List<TenantDetailDTO> records = list.stream()
                .map(this::toDetailDTO)
                .collect(Collectors.toList());

        return new PageResult<>(pageNo, pageSize, total, records);
    }

    private TenantDetailDTO toDetailDTO(Tenant tenant) {
        TenantDetailDTO dto = new TenantDetailDTO();
        dto.setId(tenant.getId());
        dto.setFullName(tenant.getFullName());
        dto.setMobileNumber(tenant.getMobileNumber());
        dto.setWechatId(tenant.getWechatId());
        dto.setRemarkText(tenant.getRemarkText());
        dto.setCreatedAt(tenant.getCreatedAt());
        dto.setUpdatedAt(tenant.getUpdatedAt());
        return dto;
    }
}

