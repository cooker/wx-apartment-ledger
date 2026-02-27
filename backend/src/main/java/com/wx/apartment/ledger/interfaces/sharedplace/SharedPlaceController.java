package com.wx.apartment.ledger.interfaces.sharedplace;

import com.wx.apartment.ledger.application.common.PageResult;
import com.wx.apartment.ledger.application.sharedplace.SharedPlaceApplicationService;
import com.wx.apartment.ledger.application.sharedplace.dto.SharedPlaceCmd;
import com.wx.apartment.ledger.application.sharedplace.dto.SharedPlaceDetailDTO;
import com.wx.apartment.ledger.application.sharedplace.dto.SharedPlacePageQuery;
import com.wx.apartment.ledger.application.sharedplace.dto.SharedPlaceShareCmd;
import com.wx.apartment.ledger.application.sharedplace.dto.SharedPlaceShareDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shared-places")
public class SharedPlaceController {

    private final SharedPlaceApplicationService sharedPlaceApplicationService;

    public SharedPlaceController(SharedPlaceApplicationService sharedPlaceApplicationService) {
        this.sharedPlaceApplicationService = sharedPlaceApplicationService;
    }

    @PostMapping("/create")
    public Long create(@Valid @RequestBody SharedPlaceCmd cmd) {
        return sharedPlaceApplicationService.create(cmd);
    }

    @PostMapping("/{id}/update")
    public void update(@PathVariable("id") Long id,
                       @Valid @RequestBody SharedPlaceCmd cmd) {
        sharedPlaceApplicationService.update(id, cmd);
    }

    @GetMapping("/{id}")
    public SharedPlaceDetailDTO getDetail(@PathVariable("id") Long id) {
        return sharedPlaceApplicationService.getDetail(id);
    }

    @GetMapping("/page")
    public PageResult<SharedPlaceDetailDTO> page(SharedPlacePageQuery query) {
        return sharedPlaceApplicationService.pageQuery(query);
    }

    @PostMapping("/{id}/delete")
    public void delete(@PathVariable("id") Long id) {
        sharedPlaceApplicationService.delete(id);
    }

    @GetMapping("/{id}/share")
    public SharedPlaceShareDTO getShare(@PathVariable("id") Long id,
                                        Integer usageYear,
                                        Integer usageMonth) {
        return sharedPlaceApplicationService.getShareDetail(id, usageYear, usageMonth);
    }

    @PostMapping("/{id}/share")
    public void saveShare(@PathVariable("id") Long id,
                          @Valid @RequestBody SharedPlaceShareCmd cmd) {
        cmd.setPlaceId(id);
        sharedPlaceApplicationService.saveShare(cmd);
    }
}

