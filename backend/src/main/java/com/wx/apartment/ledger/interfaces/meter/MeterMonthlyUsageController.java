package com.wx.apartment.ledger.interfaces.meter;

import com.wx.apartment.ledger.application.common.PageResult;
import com.wx.apartment.ledger.application.meter.MeterMonthlyUsageApplicationService;
import com.wx.apartment.ledger.application.meter.dto.MeterMonthlyUsageCmd;
import com.wx.apartment.ledger.application.meter.dto.MeterMonthlyUsageDTO;
import com.wx.apartment.ledger.application.meter.dto.MeterMonthlyUsageQuery;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meter-usages")
public class MeterMonthlyUsageController {

    private final MeterMonthlyUsageApplicationService meterMonthlyUsageApplicationService;

    public MeterMonthlyUsageController(MeterMonthlyUsageApplicationService meterMonthlyUsageApplicationService) {
        this.meterMonthlyUsageApplicationService = meterMonthlyUsageApplicationService;
    }

    @PostMapping("/upsert")
    public Long upsert(@Valid @RequestBody MeterMonthlyUsageCmd cmd) {
        return meterMonthlyUsageApplicationService.upsertUsage(cmd);
    }

    @GetMapping("/by-month")
    public MeterMonthlyUsageDTO getByMonth(@RequestParam("meterId") Long meterId,
                                           @RequestParam("usageYear") Integer usageYear,
                                           @RequestParam("usageMonth") Integer usageMonth) {
        return meterMonthlyUsageApplicationService.getByMonth(meterId, usageYear, usageMonth);
    }

    @GetMapping("/page")
    public PageResult<MeterMonthlyUsageDTO> page(MeterMonthlyUsageQuery query) {
        return meterMonthlyUsageApplicationService.pageByMeter(query);
    }

    @PostMapping("/{id}/delete")
    public void delete(@PathVariable("id") Long id) {
        meterMonthlyUsageApplicationService.delete(id);
    }
}

