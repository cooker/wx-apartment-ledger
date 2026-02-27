package com.wx.apartment.ledger.infrastructure.bill;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wx.apartment.ledger.domain.bill.Bill;
import com.wx.apartment.ledger.domain.bill.BillRepository;
import com.wx.apartment.ledger.domain.bill.BillSettleState;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class BillRepositoryImpl implements BillRepository {

    private final BillMapper billMapper;

    public BillRepositoryImpl(BillMapper billMapper) {
        this.billMapper = billMapper;
    }

    @Override
    public Bill save(Bill bill) {
        BillPO po = toPO(bill);
        if (po.getId() == null) {
            billMapper.insert(po);
        } else {
            billMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<Bill> findById(Long id) {
        BillPO po = billMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<Bill> findByTenantAndYearMonth(Long tenantId, int year, int month) {
        LambdaQueryWrapper<BillPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BillPO::getTenantId, tenantId)
                .eq(BillPO::getBillYear, year)
                .eq(BillPO::getBillMonth, month);
        BillPO po = billMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<Bill> findPage(Long tenantId, Integer year, Integer month, BillSettleState settleState,
                               long offset, long pageSize) {
        long pageNo = offset / pageSize + 1;
        Page<BillPO> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<BillPO> wrapper = buildWrapper(tenantId, year, month, settleState);
        Page<BillPO> result = billMapper.selectPage(page, wrapper);
        return result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(Long tenantId, Integer year, Integer month, BillSettleState settleState) {
        LambdaQueryWrapper<BillPO> wrapper = buildWrapper(tenantId, year, month, settleState);
        return billMapper.selectCount(wrapper);
    }

    @Override
    public void deleteById(Long id) {
        billMapper.deleteById(id);
    }

    private LambdaQueryWrapper<BillPO> buildWrapper(Long tenantId, Integer year, Integer month, BillSettleState settleState) {
        LambdaQueryWrapper<BillPO> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(BillPO::getTenantId, tenantId);
        }
        if (year != null) {
            wrapper.eq(BillPO::getBillYear, year);
        }
        if (month != null) {
            wrapper.eq(BillPO::getBillMonth, month);
        }
        if (settleState != null) {
            wrapper.eq(BillPO::getSettleState, settleState.name());
        }
        wrapper.orderByDesc(BillPO::getBillYear)
                .orderByDesc(BillPO::getBillMonth)
                .orderByDesc(BillPO::getCreatedAt);
        return wrapper;
    }

    private BillPO toPO(Bill bill) {
        BillPO po = new BillPO();
        po.setId(bill.getId());
        po.setTenantId(bill.getTenantId());
        po.setBillYear(bill.getBillYear());
        po.setBillMonth(bill.getBillMonth());
        po.setRentAmount(bill.getRentAmount());
        po.setWaterAmount(bill.getWaterAmount());
        po.setElectricAmount(bill.getElectricAmount());
        po.setAdjustAmount(bill.getAdjustAmount());
        po.setTotalAmount(bill.getTotalAmount());
        po.setSettleState(bill.getSettleState() == null ? null : bill.getSettleState().name());
        po.setPaidAmount(bill.getPaidAmount());
        po.setPaidTime(bill.getPaidTime());
        po.setCreatedAt(bill.getCreatedAt());
        po.setUpdatedAt(bill.getUpdatedAt());
        return po;
    }

    private Bill toDomain(BillPO po) {
        return new Bill(
                po.getId(),
                po.getTenantId(),
                po.getBillYear(),
                po.getBillMonth(),
                po.getRentAmount(),
                po.getWaterAmount(),
                po.getElectricAmount(),
                po.getAdjustAmount(),
                po.getTotalAmount(),
                BillSettleState.fromString(po.getSettleState()),
                po.getPaidAmount(),
                po.getPaidTime(),
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }
}

