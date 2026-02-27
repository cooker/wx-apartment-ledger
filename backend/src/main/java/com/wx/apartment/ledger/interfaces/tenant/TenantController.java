package com.wx.apartment.ledger.interfaces.tenant;

import com.wx.apartment.ledger.application.common.PageResult;
import com.wx.apartment.ledger.application.tenant.TenantApplicationService;
import com.wx.apartment.ledger.application.tenant.dto.TenantCmd;
import com.wx.apartment.ledger.application.tenant.dto.TenantDetailDTO;
import com.wx.apartment.ledger.application.tenant.dto.TenantPageQuery;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantApplicationService tenantApplicationService;

    public TenantController(TenantApplicationService tenantApplicationService) {
        this.tenantApplicationService = tenantApplicationService;
    }

    @PostMapping("/create")
    public Long createTenant(@Valid @RequestBody TenantCmd cmd) {
        return tenantApplicationService.createTenant(cmd);
    }

    @PostMapping("/{id}/update")
    public void updateTenant(@PathVariable("id") Long id,
                             @Valid @RequestBody TenantCmd cmd) {
        tenantApplicationService.updateTenant(id, cmd);
    }

    @GetMapping("/{id}")
    public TenantDetailDTO getTenant(@PathVariable("id") Long id) {
        return tenantApplicationService.getTenantDetail(id);
    }

    @GetMapping("/page")
    public PageResult<TenantDetailDTO> pageQuery(TenantPageQuery query) {
        return tenantApplicationService.pageQuery(query);
    }
}

