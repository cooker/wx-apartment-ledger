package com.wx.apartment.ledger.infrastructure.tenant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wx.apartment.ledger.domain.tenant.Tenant;
import com.wx.apartment.ledger.domain.tenant.TenantRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TenantRepositoryImpl implements TenantRepository {

    private final TenantMapper tenantMapper;

    public TenantRepositoryImpl(TenantMapper tenantMapper) {
        this.tenantMapper = tenantMapper;
    }

    @Override
    public Tenant save(Tenant tenant) {
        TenantPO po = toPO(tenant);
        if (po.getId() == null) {
            tenantMapper.insert(po);
        } else {
            tenantMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<Tenant> findById(Long id) {
        TenantPO po = tenantMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<Tenant> findPage(String keyword, long offset, long pageSize) {
        long pageNo = offset / pageSize + 1;
        Page<TenantPO> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<TenantPO> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(TenantPO::getFullName, keyword)
                    .or()
                    .like(TenantPO::getMobileNumber, keyword);
        }
        wrapper.orderByDesc(TenantPO::getCreatedAt);
        Page<TenantPO> result = tenantMapper.selectPage(page, wrapper);
        return result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByKeyword(String keyword) {
        LambdaQueryWrapper<TenantPO> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(TenantPO::getFullName, keyword)
                    .or()
                    .like(TenantPO::getMobileNumber, keyword);
        }
        return tenantMapper.selectCount(wrapper);
    }

    @Override
    public void deleteById(Long id) {
        tenantMapper.deleteById(id);
    }

    private TenantPO toPO(Tenant tenant) {
        TenantPO po = new TenantPO();
        po.setId(tenant.getId());
        po.setFullName(tenant.getFullName());
        po.setMobileNumber(tenant.getMobileNumber());
        po.setWechatId(tenant.getWechatId());
        po.setRemarkText(tenant.getRemarkText());
        po.setCreatedAt(tenant.getCreatedAt());
        po.setUpdatedAt(tenant.getUpdatedAt());
        return po;
    }

    private Tenant toDomain(TenantPO po) {
        return new Tenant(
                po.getId(),
                po.getFullName(),
                po.getMobileNumber(),
                po.getWechatId(),
                po.getRemarkText(),
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }
}

