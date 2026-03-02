package com.wx.apartment.ledger.application.tenant;

import com.wx.apartment.ledger.application.common.PageResult;
import com.wx.apartment.ledger.application.tenant.dto.TenantCmd;
import com.wx.apartment.ledger.application.tenant.dto.TenantDetailDTO;
import com.wx.apartment.ledger.application.tenant.dto.TenantPageQuery;
import com.wx.apartment.ledger.domain.bill.BillRepository;
import com.wx.apartment.ledger.domain.house.House;
import com.wx.apartment.ledger.domain.house.HouseRepository;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlace;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlaceRepository;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlaceTenantRepository;
import com.wx.apartment.ledger.domain.tenant.Tenant;
import com.wx.apartment.ledger.domain.tenant.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class TenantApplicationService {

    private final TenantRepository tenantRepository;
    private final BillRepository billRepository;
    private final HouseRepository houseRepository;
    private final SharedPlaceTenantRepository sharedPlaceTenantRepository;
    private final SharedPlaceRepository sharedPlaceRepository;

    public TenantApplicationService(TenantRepository tenantRepository,
                                    BillRepository billRepository,
                                    HouseRepository houseRepository,
                                    SharedPlaceTenantRepository sharedPlaceTenantRepository,
                                    SharedPlaceRepository sharedPlaceRepository) {
        this.tenantRepository = tenantRepository;
        this.billRepository = billRepository;
        this.houseRepository = houseRepository;
        this.sharedPlaceTenantRepository = sharedPlaceTenantRepository;
        this.sharedPlaceRepository = sharedPlaceRepository;
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
        TenantDetailDTO dto = toDetailDTO(tenant);
        fillAssociations(dto);
        return dto;
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
        records.forEach(this::fillAssociations);

        return new PageResult<>(pageNo, pageSize, total, records);
    }

    /** 填充租客关联的公共场所名称、房屋名称 */
    private void fillAssociations(TenantDetailDTO dto) {
        Long tenantId = dto.getId();
        if (tenantId == null) return;
        List<Long> placeIds = sharedPlaceTenantRepository.findPlaceIdsByTenantId(tenantId);
        List<String> placeNames = new ArrayList<>();
        for (Long placeId : placeIds) {
            sharedPlaceRepository.findById(placeId)
                    .map(SharedPlace::getPlaceName)
                    .ifPresent(placeNames::add);
        }
        dto.setSharedPlaceNames(placeNames);

        List<House> houses = houseRepository.findByCurrentTenantId(tenantId);
        List<String> houseNames = houses.stream()
                .map(h -> {
                    String name = h.getHouseName() != null ? h.getHouseName().trim() : "";
                    return name.isEmpty() ? h.getHouseCode() : name + "（" + h.getHouseCode() + "）";
                })
                .collect(Collectors.toList());
        dto.setHouseNames(houseNames);
    }

    /**
     * 删除租客。若存在关联账单、作为某房屋当前租客或已关联公共场所，则不允许删除。
     */
    public void deleteTenant(Long id) {
        tenantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("租客不存在，id=" + id));
        if (billRepository.count(id, null, null, null) > 0) {
            throw new IllegalStateException("该租客存在关联账单，无法删除");
        }
        if (houseRepository.existsByCurrentTenantId(id)) {
            throw new IllegalStateException("该租客为某房屋当前租客，无法删除");
        }
        if (!sharedPlaceTenantRepository.findPlaceIdsByTenantId(id).isEmpty()) {
            throw new IllegalStateException("该租客已关联公共场所，无法删除");
        }
        tenantRepository.deleteById(id);
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

