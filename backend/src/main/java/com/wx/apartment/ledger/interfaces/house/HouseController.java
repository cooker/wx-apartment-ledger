package com.wx.apartment.ledger.interfaces.house;

import com.wx.apartment.ledger.application.common.PageResult;
import com.wx.apartment.ledger.application.house.HouseApplicationService;
import com.wx.apartment.ledger.application.house.dto.HouseCmd;
import com.wx.apartment.ledger.application.house.dto.HouseDetailDTO;
import com.wx.apartment.ledger.application.house.dto.HousePageQuery;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/houses")
public class HouseController {

    private final HouseApplicationService houseApplicationService;

    public HouseController(HouseApplicationService houseApplicationService) {
        this.houseApplicationService = houseApplicationService;
    }

    @PostMapping("/create")
    public Long create(@Valid @RequestBody HouseCmd cmd) {
        return houseApplicationService.create(cmd);
    }

    @PostMapping("/{id}/update")
    public void update(@PathVariable("id") Long id,
                       @Valid @RequestBody HouseCmd cmd) {
        houseApplicationService.update(id, cmd);
    }

    @GetMapping("/{id}")
    public HouseDetailDTO getDetail(@PathVariable("id") Long id) {
        return houseApplicationService.getDetail(id);
    }

    @GetMapping("/page")
    public PageResult<HouseDetailDTO> page(HousePageQuery query) {
        return houseApplicationService.pageQuery(query);
    }

    @PostMapping("/{id}/delete")
    public void delete(@PathVariable("id") Long id) {
        houseApplicationService.delete(id);
    }
}

