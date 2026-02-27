package com.wx.apartment.ledger.interfaces.meter;

import com.wx.apartment.ledger.application.common.PageResult;
import com.wx.apartment.ledger.application.meter.MeterApplicationService;
import com.wx.apartment.ledger.application.meter.dto.MeterCmd;
import com.wx.apartment.ledger.application.meter.dto.MeterDetailDTO;
import com.wx.apartment.ledger.application.meter.dto.MeterPageQuery;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meters")
public class MeterController {

    private final MeterApplicationService meterApplicationService;

    public MeterController(MeterApplicationService meterApplicationService) {
        this.meterApplicationService = meterApplicationService;
    }

    @PostMapping("/create")
    public Long create(@Valid @RequestBody MeterCmd cmd) {
        return meterApplicationService.create(cmd);
    }

    @PostMapping("/{id}/update")
    public void update(@PathVariable("id") Long id,
                       @Valid @RequestBody MeterCmd cmd) {
        meterApplicationService.update(id, cmd);
    }

    @GetMapping("/{id}")
    public MeterDetailDTO getDetail(@PathVariable("id") Long id) {
        return meterApplicationService.getDetail(id);
    }

    @GetMapping("/page")
    public PageResult<MeterDetailDTO> page(MeterPageQuery query) {
        return meterApplicationService.pageQuery(query);
    }

    @PostMapping("/{id}/delete")
    public void delete(@PathVariable("id") Long id) {
        meterApplicationService.delete(id);
    }
}

