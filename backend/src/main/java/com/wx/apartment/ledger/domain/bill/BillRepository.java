package com.wx.apartment.ledger.domain.bill;

import java.util.List;
import java.util.Optional;

public interface BillRepository {

    Bill save(Bill bill);

    Optional<Bill> findById(Long id);

    Optional<Bill> findByTenantAndYearMonth(Long tenantId, int year, int month);

    List<Bill> findPage(Long tenantId, Integer year, Integer month, BillSettleState settleState,
                        long offset, long pageSize);

    long count(Long tenantId, Integer year, Integer month, BillSettleState settleState);

    void deleteById(Long id);
}

