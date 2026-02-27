package com.wx.apartment.ledger.domain.bill;

import java.util.List;

public interface BillPaymentRepository {

    BillPayment save(BillPayment payment);

    List<BillPayment> listByBillId(Long billId);

    void deleteByBillId(Long billId);

    BillPayment findById(Long id);

    void deleteById(Long id);
}

