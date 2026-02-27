package com.wx.apartment.ledger.interfaces.price;

import com.wx.apartment.ledger.application.price.PriceConfigApplicationService;
import com.wx.apartment.ledger.application.price.dto.PriceConfigCmd;
import com.wx.apartment.ledger.application.price.dto.PriceConfigDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/price-configs")
public class PriceConfigController {

    private final PriceConfigApplicationService priceConfigApplicationService;

    public PriceConfigController(PriceConfigApplicationService priceConfigApplicationService) {
        this.priceConfigApplicationService = priceConfigApplicationService;
    }

    @PostMapping("/create")
    public Long create(@Valid @RequestBody PriceConfigCmd cmd) {
        return priceConfigApplicationService.create(cmd);
    }

    @PostMapping("/{id}/update")
    public void update(@PathVariable("id") Long id,
                       @Valid @RequestBody PriceConfigCmd cmd) {
        priceConfigApplicationService.update(id, cmd);
    }

    @GetMapping("/current")
    public PriceConfigDTO getCurrent(@RequestParam("chargeKind") String chargeKind) {
        return priceConfigApplicationService.getCurrent(chargeKind);
    }

    @GetMapping
    public List<PriceConfigDTO> listByKind(@RequestParam("chargeKind") String chargeKind) {
        return priceConfigApplicationService.listByKind(chargeKind);
    }
}

