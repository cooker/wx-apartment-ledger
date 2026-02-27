package com.wx.apartment.ledger.interfaces.meter;

import com.wx.apartment.ledger.application.meter.MeterSetApplicationService;
import com.wx.apartment.ledger.application.meter.dto.MeterSetCmd;
import com.wx.apartment.ledger.application.meter.dto.MeterSetDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meter-sets")
public class MeterSetController {

    private final MeterSetApplicationService meterSetApplicationService;

    public MeterSetController(MeterSetApplicationService meterSetApplicationService) {
        this.meterSetApplicationService = meterSetApplicationService;
    }

    @PostMapping("/create")
    public Long create(@Valid @RequestBody MeterSetCmd cmd) {
        return meterSetApplicationService.create(cmd);
    }

    @PostMapping("/{id}/update")
    public void update(@PathVariable("id") Long id,
                       @Valid @RequestBody MeterSetCmd cmd) {
        meterSetApplicationService.update(id, cmd);
    }

    @GetMapping("/by-main")
    public MeterSetDTO getByMainMeter(@RequestParam("mainMeterId") Long mainMeterId) {
        return meterSetApplicationService.getByMainMeterId(mainMeterId);
    }

    @PostMapping("/{id}/delete")
    public void delete(@PathVariable("id") Long id) {
        meterSetApplicationService.delete(id);
    }
}

