package com.wx.apartment.ledger.infrastructure.bill;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.apartment.ledger.domain.bill.BillPayment;
import com.wx.apartment.ledger.domain.bill.BillPaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BillPaymentRepositoryImpl implements BillPaymentRepository {

    private final BillPaymentMapper mapper;

    public BillPaymentRepositoryImpl(BillPaymentMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public BillPayment save(BillPayment payment) {
        BillPaymentPO po = toPO(payment);
        if (po.getId() == null) {
            mapper.insert(po);
        } else {
            mapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public List<BillPayment> listByBillId(Long billId) {
        List<BillPaymentPO> list = mapper.selectList(
                new LambdaQueryWrapper<BillPaymentPO>()
                        .eq(BillPaymentPO::getBillId, billId)
                        .orderByAsc(BillPaymentPO::getPaymentTime)
        );
        return list.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByBillId(Long billId) {
        mapper.delete(new LambdaQueryWrapper<BillPaymentPO>()
                .eq(BillPaymentPO::getBillId, billId));
    }

    @Override
    public BillPayment findById(Long id) {
        BillPaymentPO po = mapper.selectById(id);
        return po == null ? null : toDomain(po);
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    private BillPaymentPO toPO(BillPayment payment) {
        BillPaymentPO po = new BillPaymentPO();
        po.setId(payment.getId());
        po.setBillId(payment.getBillId());
        po.setPaymentAmount(payment.getPaymentAmount());
        po.setPaymentTime(payment.getPaymentTime());
        po.setPaymentNote(payment.getPaymentNote());
        po.setCreatedAt(payment.getCreatedAt());
        return po;
    }

    private BillPayment toDomain(BillPaymentPO po) {
        return new BillPayment(
                po.getId(),
                po.getBillId(),
                po.getPaymentAmount(),
                po.getPaymentTime(),
                po.getPaymentNote(),
                po.getCreatedAt()
        );
    }
}

