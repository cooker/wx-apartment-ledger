package com.wx.apartment.ledger.interfaces.bill;

import com.wx.apartment.ledger.application.bill.BillApplicationService;
import com.wx.apartment.ledger.application.bill.dto.BillDetailDTO;
import com.wx.apartment.ledger.application.bill.dto.BillPageQuery;
import com.wx.apartment.ledger.application.bill.dto.BillPaymentCmd;
import com.wx.apartment.ledger.application.bill.dto.BillPaymentDTO;
import com.wx.apartment.ledger.application.common.PageResult;
import com.wx.apartment.ledger.application.bill.dto.BillSharedPlaceAdjustCmd;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillApplicationService billApplicationService;

    public BillController(BillApplicationService billApplicationService) {
        this.billApplicationService = billApplicationService;
    }

    @GetMapping("/page")
    public PageResult<BillDetailDTO> page(BillPageQuery query) {
        return billApplicationService.pageQuery(query);
    }

    @GetMapping("/{id}")
    public BillDetailDTO getDetail(@PathVariable("id") Long id) {
        return billApplicationService.getBillDetail(id);
    }

    @GetMapping("/{id}/payments")
    public List<BillPaymentDTO> listPayments(@PathVariable("id") Long id) {
        return billApplicationService.listPayments(id);
    }

    @PostMapping("/payments/receive")
    public Long receivePayment(@Valid @RequestBody BillPaymentCmd cmd) {
        return billApplicationService.receivePayment(cmd);
    }

    @PostMapping("/{id}/shared-places/adjust")
    public void adjustSharedPlaces(@PathVariable("id") Long id,
                                   @Valid @RequestBody BillSharedPlaceAdjustCmd cmd) {
        billApplicationService.adjustSharedPlaceAmounts(id, cmd);
    }

    @PostMapping("/regenerate")
    public void regenerate(@Valid @RequestBody com.wx.apartment.ledger.application.bill.dto.BillRegenerateCmd cmd) {
        billApplicationService.regenerate(cmd);
    }

    @PostMapping("/payments/{id}/delete")
    public void deletePayment(@PathVariable("id") Long id) {
        billApplicationService.deletePayment(id);
    }
}

