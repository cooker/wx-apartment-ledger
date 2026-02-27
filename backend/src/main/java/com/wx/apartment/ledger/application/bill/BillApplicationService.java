package com.wx.apartment.ledger.application.bill;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.apartment.ledger.application.bill.dto.*;
import com.wx.apartment.ledger.application.common.PageResult;
import com.wx.apartment.ledger.domain.bill.*;
import com.wx.apartment.ledger.domain.house.House;
import com.wx.apartment.ledger.domain.house.HouseRepository;
import com.wx.apartment.ledger.domain.house.HouseMeterRepository;
import com.wx.apartment.ledger.domain.meter.Meter;
import com.wx.apartment.ledger.domain.meter.MeterKind;
import com.wx.apartment.ledger.domain.meter.MeterMonthlyUsage;
import com.wx.apartment.ledger.domain.meter.MeterMonthlyUsageRepository;
import com.wx.apartment.ledger.domain.meter.MeterRepository;
import com.wx.apartment.ledger.domain.price.ChargeKind;
import com.wx.apartment.ledger.domain.price.PriceConfig;
import com.wx.apartment.ledger.domain.price.PriceConfigRepository;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlace;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlaceMeterRepository;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlaceRepository;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlaceTenantRepository;
import com.wx.apartment.ledger.domain.sharedplace.SharedPlaceTenantShareRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import com.wx.apartment.ledger.domain.tenant.Tenant;
import com.wx.apartment.ledger.domain.tenant.TenantRepository;
import com.wx.apartment.ledger.infrastructure.bill.BillHouseMapper;
import com.wx.apartment.ledger.infrastructure.bill.BillHousePO;
import com.wx.apartment.ledger.infrastructure.bill.BillSharedPlaceMapper;
import com.wx.apartment.ledger.infrastructure.bill.BillSharedPlacePO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BillApplicationService {

    private final BillRepository billRepository;
    private final BillPaymentRepository billPaymentRepository;
    private final TenantRepository tenantRepository;
    private final HouseRepository houseRepository;
    private final SharedPlaceRepository sharedPlaceRepository;
    private final SharedPlaceTenantRepository sharedPlaceTenantRepository;
    private final SharedPlaceTenantShareRepository sharedPlaceTenantShareRepository;
    private final HouseMeterRepository houseMeterRepository;
    private final SharedPlaceMeterRepository sharedPlaceMeterRepository;
    private final MeterRepository meterRepository;
    private final MeterMonthlyUsageRepository meterMonthlyUsageRepository;
    private final PriceConfigRepository priceConfigRepository;
    private final BillHouseMapper billHouseMapper;
    private final BillSharedPlaceMapper billSharedPlaceMapper;

    public BillApplicationService(BillRepository billRepository,
                                  BillPaymentRepository billPaymentRepository,
                                  TenantRepository tenantRepository,
                                  HouseRepository houseRepository,
                                  SharedPlaceRepository sharedPlaceRepository,
                                  SharedPlaceTenantRepository sharedPlaceTenantRepository,
                                  SharedPlaceTenantShareRepository sharedPlaceTenantShareRepository,
                                  HouseMeterRepository houseMeterRepository,
                                  SharedPlaceMeterRepository sharedPlaceMeterRepository,
                                  MeterRepository meterRepository,
                                  MeterMonthlyUsageRepository meterMonthlyUsageRepository,
                                  PriceConfigRepository priceConfigRepository,
                                  BillHouseMapper billHouseMapper,
                                  BillSharedPlaceMapper billSharedPlaceMapper) {
        this.billRepository = billRepository;
        this.billPaymentRepository = billPaymentRepository;
        this.tenantRepository = tenantRepository;
        this.houseRepository = houseRepository;
        this.sharedPlaceRepository = sharedPlaceRepository;
        this.sharedPlaceTenantRepository = sharedPlaceTenantRepository;
        this.sharedPlaceTenantShareRepository = sharedPlaceTenantShareRepository;
        this.houseMeterRepository = houseMeterRepository;
        this.sharedPlaceMeterRepository = sharedPlaceMeterRepository;
        this.meterRepository = meterRepository;
        this.meterMonthlyUsageRepository = meterMonthlyUsageRepository;
        this.priceConfigRepository = priceConfigRepository;
        this.billHouseMapper = billHouseMapper;
        this.billSharedPlaceMapper = billSharedPlaceMapper;
    }

    public PageResult<BillDetailDTO> pageQuery(BillPageQuery query) {
        long pageNo = query.getPageNo() <= 0 ? 1 : query.getPageNo();
        long pageSize = query.getPageSize() <= 0 ? 10 : query.getPageSize();
        long offset = (pageNo - 1) * pageSize;

        BillSettleState settleState = null;
        if (query.getSettleState() != null) {
            settleState = BillSettleState.fromString(query.getSettleState());
        }

        // 若前端按具体年月筛选，则按该年月生成；否则默认按当前年月生成一次
        if (query.getBillYear() != null && query.getBillMonth() != null) {
            generateMonthlyBillsIfAbsent(query.getBillYear(), query.getBillMonth());
        } else if (query.getBillYear() == null && query.getBillMonth() == null) {
            LocalDateTime now = LocalDateTime.now();
            generateMonthlyBillsIfAbsent(now.getYear(), now.getMonthValue());
        }

        long total = billRepository.count(query.getTenantId(), query.getBillYear(), query.getBillMonth(), settleState);
        List<Bill> list = billRepository.findPage(query.getTenantId(), query.getBillYear(), query.getBillMonth(),
                settleState, offset, pageSize);
        List<BillDetailDTO> records = list.stream()
                .map(this::toDetailDTO)
                .collect(Collectors.toList());

        return new PageResult<>(pageNo, pageSize, total, records);
    }

    public BillDetailDTO getBillDetail(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("账单不存在，id=" + id));
        return toDetailDTO(bill);
    }

    public List<BillPaymentDTO> listPayments(Long billId) {
        List<BillPayment> payments = billPaymentRepository.listByBillId(billId);
        return payments.stream()
                .map(this::toPaymentDTO)
                .collect(Collectors.toList());
    }

    /**
     * 导出指定账期的账单明细为 PDF，按租客姓名升序。
     */
    public byte[] exportMonthlyBillsPdf(Integer year, Integer month) {
        LocalDate today = LocalDate.now();
        int exportYear = (year != null ? year : today.getYear());
        int exportMonth = (month != null ? month : today.getMonthValue());

        List<Bill> bills = billRepository.findPage(null, exportYear, exportMonth, null, 0, 10000);
        if (bills.isEmpty()) {
            return new byte[0];
        }

        List<BillDetailDTO> dtos = bills.stream()
                .map(this::toDetailDTO)
                .sorted(Comparator.comparing(dto -> dto.getTenantName() == null ? "" : dto.getTenantName()))
                .toList();

        try (PDDocument document = new PDDocument()) {
            for (BillDetailDTO dto : dtos) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                    float margin = 40;
                    float y = page.getMediaBox().getHeight() - margin;

                    cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    cs.beginText();
                    cs.newLineAtOffset(margin, y);
                    cs.showText("租客账单明细");
                    cs.endText();

                    cs.setFont(PDType1Font.HELVETICA, 11);
                    y -= 24;

                    // 基本信息
                    y = writeLine(cs, "租客: " + (dto.getTenantName() == null ? ("#" + dto.getTenantId()) : dto.getTenantName())
                            + "   账期: " + dto.getBillYear() + "-" + String.format("%02d", dto.getBillMonth()), margin, y);
                    y = writeLine(cs, "房租: " + amount(dto.getRentAmount())
                            + " 元   水费: " + amount(dto.getWaterAmount())
                            + " 元   电费: " + amount(dto.getElectricAmount()) + " 元", margin, y);
                    y = writeLine(cs, "合计: " + amount(dto.getTotalAmount()) + " 元", margin, y - 4);

                    // 房屋水电明细
                    y -= 18;
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
                    y = writeLine(cs, "房屋水电明细:", margin, y);
                    cs.setFont(PDType1Font.HELVETICA, 10);
                    for (BillDetailDTO.BillHouseItemDTO hi : dto.getHouseItems()) {
                        String line = String.format(
                                "  - %s  电量: %s  电费: %s 元  水量: %s  水费: %s 元",
                                hi.getHouseLabel(),
                                usage(hi.getElectricUsage()),
                                amount(hi.getElectricAmount()),
                                usage(hi.getWaterUsage()),
                                amount(hi.getWaterAmount())
                        );
                        y = writeLine(cs, line, margin, y);
                        if (y < margin + 60) {
                            break;
                        }
                    }

                    // 公共场所水电明细
                    y -= 12;
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
                    y = writeLine(cs, "公共场所水电明细:", margin, y);
                    cs.setFont(PDType1Font.HELVETICA, 10);
                    for (BillDetailDTO.BillSharedPlaceItemDTO si : dto.getSharedPlaceItems()) {
                        String line = String.format(
                                "  - %s  电量: %s  电费: %s 元  水量: %s  水费: %s 元",
                                si.getSharedPlaceLabel(),
                                usage(si.getElectricUsage()),
                                amount(si.getElectricAmount()),
                                usage(si.getWaterUsage()),
                                amount(si.getWaterAmount())
                        );
                        y = writeLine(cs, line, margin, y);
                        if (y < margin + 40) {
                            break;
                        }
                    }
                }
            }

            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出账单PDF失败", e);
        }
    }

    private float writeLine(PDPageContentStream cs, String text, float x, float y) throws java.io.IOException {
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y - 14;
    }

    private String amount(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        return value.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    private String usage(BigDecimal value) {
        if (value == null) {
            return "-";
        }
        return value.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    public Long receivePayment(BillPaymentCmd cmd) {
        Bill bill = billRepository.findById(cmd.getBillId())
                .orElseThrow(() -> new NoSuchElementException("账单不存在，id=" + cmd.getBillId()));

        LocalDateTime paymentTime = cmd.getPaymentTime() != null ? cmd.getPaymentTime() : LocalDateTime.now();

        BillPayment payment = new BillPayment(
                null,
                cmd.getBillId(),
                cmd.getPaymentAmount(),
                paymentTime,
                cmd.getPaymentNote(),
                paymentTime
        );
        BillPayment savedPayment = billPaymentRepository.save(payment);

        Bill updatedBill = bill.applyPayment(cmd.getPaymentAmount(), paymentTime);
        billRepository.save(updatedBill);

        return savedPayment.getId();
    }

    /**
     * 删除一条收款记录，并按剩余收款记录重新计算账单的已收金额与结清状态。
     */
    public void deletePayment(Long paymentId) {
        BillPayment payment = billPaymentRepository.findById(paymentId);
        if (payment == null) {
            return;
        }
        Long billId = payment.getBillId();
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new NoSuchElementException("账单不存在，id=" + billId));

        // 删除该收款记录
        billPaymentRepository.deleteById(paymentId);

        // 重新加载该账单下剩余的收款记录
        List<BillPayment> payments = billPaymentRepository.listByBillId(billId);
        BigDecimal totalPaid = BigDecimal.ZERO;
        LocalDateTime latestTime = null;
        for (BillPayment p : payments) {
            if (p.getPaymentAmount() != null) {
                totalPaid = totalPaid.add(p.getPaymentAmount());
            }
            if (p.getPaymentTime() != null && (latestTime == null || p.getPaymentTime().isAfter(latestTime))) {
                latestTime = p.getPaymentTime();
            }
        }

        BigDecimal totalAmount = bill.getTotalAmount() == null ? BigDecimal.ZERO : bill.getTotalAmount();
        BillSettleState newState = !payments.isEmpty() && totalPaid.compareTo(totalAmount) >= 0
                ? BillSettleState.SETTLED
                : BillSettleState.PENDING;

        Bill updated = new Bill(
                bill.getId(),
                bill.getTenantId(),
                bill.getBillYear(),
                bill.getBillMonth(),
                bill.getRentAmount(),
                bill.getWaterAmount(),
                bill.getElectricAmount(),
                bill.getAdjustAmount(),
                bill.getTotalAmount(),
                newState,
                totalPaid,
                latestTime,
                bill.getCreatedAt(),
                LocalDateTime.now()
        );
        billRepository.save(updated);
    }

    /**
     * 删除指定年月（及可选租客）的未结清账单并重新生成。
     */
    public void regenerate(BillRegenerateCmd cmd) {
        int year = cmd.getBillYear();
        int month = cmd.getBillMonth();
        Long tenantId = cmd.getTenantId();
        if (year <= 0 || month <= 0 || month > 12) {
            throw new IllegalArgumentException("账单年月不合法");
        }

        // 查询本次要处理的账单（不按结清状态过滤，在应用层判断）
        List<Bill> bills = billRepository.findPage(tenantId, year, month, null, 0, 10000);
        if (bills.isEmpty()) {
            // 若本期本就没有账单，直接按规则生成即可
            generateMonthlyBillsIfAbsent(year, month);
            return;
        }

        // 不允许存在已结清账单：
        // 规则：结清状态为 SETTLED，或存在任意收款记录（有收款即视为已结清）
        boolean hasSettled = bills.stream().anyMatch(bill -> {
            if (bill.getSettleState() == BillSettleState.SETTLED) {
                return true;
            }
            Long billId = bill.getId();
            return billId != null && !billPaymentRepository.listByBillId(billId).isEmpty();
        });
        if (hasSettled) {
            throw new IllegalStateException("存在已结清账单，不能重新生成该账期账单");
        }

        // 删除账单关联的收款记录、房屋/公共场所明细，再删除账单本身
        for (Bill bill : bills) {
            Long billId = bill.getId();
            if (billId == null) {
                continue;
            }
            billPaymentRepository.deleteByBillId(billId);
            billHouseMapper.delete(new LambdaQueryWrapper<BillHousePO>()
                    .eq(BillHousePO::getBillId, billId));
            billSharedPlaceMapper.delete(new LambdaQueryWrapper<BillSharedPlacePO>()
                    .eq(BillSharedPlacePO::getBillId, billId));
            billRepository.deleteById(billId);
        }

        // 重新按规则生成本期账单
        generateMonthlyBillsIfAbsent(year, month);
    }

    /**
     * 调整指定账单下公共场所的水电费用，并据此重算账单水费、电费与总金额。
     */
    public void adjustSharedPlaceAmounts(Long billId, BillSharedPlaceAdjustCmd cmd) {
        if (billId == null || cmd == null || cmd.getItems() == null || cmd.getItems().isEmpty()) {
            return;
        }
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new NoSuchElementException("账单不存在，id=" + billId));

        // 更新公共场所水电费用
        for (BillSharedPlaceAdjustCmd.Item item : cmd.getItems()) {
            if (item.getId() == null) {
                continue;
            }
            BillSharedPlacePO po = billSharedPlaceMapper.selectById(item.getId());
            if (po == null || !billId.equals(po.getBillId())) {
                continue;
            }
            if (item.getElectricAmount() != null) {
                po.setElectricAmount(item.getElectricAmount());
            }
            if (item.getWaterAmount() != null) {
                po.setWaterAmount(item.getWaterAmount());
            }
            billSharedPlaceMapper.updateById(po);
        }

        // 重新汇总整张账单的水费、电费
        List<BillHousePO> billHouses = billHouseMapper.selectList(new LambdaQueryWrapper<BillHousePO>()
                .eq(BillHousePO::getBillId, billId));
        List<BillSharedPlacePO> billPlaces = billSharedPlaceMapper.selectList(new LambdaQueryWrapper<BillSharedPlacePO>()
                .eq(BillSharedPlacePO::getBillId, billId));

        BigDecimal newWaterAmount = BigDecimal.ZERO;
        BigDecimal newElectricAmount = BigDecimal.ZERO;

        for (BillHousePO bh : billHouses) {
            if (bh.getWaterAmount() != null) {
                newWaterAmount = newWaterAmount.add(bh.getWaterAmount());
            }
            if (bh.getElectricAmount() != null) {
                newElectricAmount = newElectricAmount.add(bh.getElectricAmount());
            }
        }
        for (BillSharedPlacePO bsp : billPlaces) {
            if (bsp.getWaterAmount() != null) {
                newWaterAmount = newWaterAmount.add(bsp.getWaterAmount());
            }
            if (bsp.getElectricAmount() != null) {
                newElectricAmount = newElectricAmount.add(bsp.getElectricAmount());
            }
        }

        BigDecimal rent = bill.getRentAmount() == null ? BigDecimal.ZERO : bill.getRentAmount();
        BigDecimal adjust = bill.getAdjustAmount() == null ? BigDecimal.ZERO : bill.getAdjustAmount();
        BigDecimal total = rent.add(newWaterAmount).add(newElectricAmount).add(adjust);
        LocalDateTime now = LocalDateTime.now();

        Bill updated = new Bill(
                bill.getId(),
                bill.getTenantId(),
                bill.getBillYear(),
                bill.getBillMonth(),
                bill.getRentAmount(),
                newWaterAmount,
                newElectricAmount,
                bill.getAdjustAmount(),
                total,
                bill.getSettleState(),
                bill.getPaidAmount(),
                bill.getPaidTime(),
                bill.getCreatedAt(),
                now
        );
        billRepository.save(updated);
    }

    /**
     * 若指定账期当前无任何账单，则按“租客+年月”维度自动生成基础账单。
     * 一个租客对应一条账单，关联该租客下的多套房屋与多个公共场所；仅包含房租，水电后续通过人工调整或重新生成。
     */
    private void generateMonthlyBillsIfAbsent(int year, int month) {
        if (year <= 0 || month <= 0 || month > 12) {
            return;
        }
        long existing = billRepository.count(null, year, month, null);
        if (existing > 0) {
            return;
        }

        long pageSize = 1000;
        List<House> houses = houseRepository.findPage(null, 0, pageSize);
        if (houses == null || houses.isEmpty()) {
            return;
        }

        // 按租客分组：租客 -> 其租住的房屋列表
        Map<Long, List<House>> tenantToHouses = new LinkedHashMap<>();
        for (House house : houses) {
            if (house.getCurrentTenantId() == null) {
                continue;
            }
            tenantToHouses
                    .computeIfAbsent(house.getCurrentTenantId(), k -> new ArrayList<>())
                    .add(house);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDate chargeDate = LocalDate.of(year, month, 1);
        for (Map.Entry<Long, List<House>> entry : tenantToHouses.entrySet()) {
            Long tenantId = entry.getKey();
            List<House> tenantHouses = entry.getValue();

            BigDecimal rentAmount = BigDecimal.ZERO;
            BigDecimal tenantWaterAmount = BigDecimal.ZERO;
            BigDecimal tenantElectricAmount = BigDecimal.ZERO;
            for (House h : tenantHouses) {
                rentAmount = rentAmount.add(h.getRentAmount() == null ? BigDecimal.ZERO : h.getRentAmount());
            }

            Bill bill = new Bill(
                    null,
                    tenantId,
                    year,
                    month,
                    rentAmount,
                    null,
                    null,
                    BigDecimal.ZERO,
                    null,
                    BillSettleState.PENDING,
                    BigDecimal.ZERO,
                    null,
                    now,
                    now
            ).recalculateTotal(now);

            Bill saved = billRepository.save(bill);
            Long billId = saved.getId();

            // 写入 t_bill_house 关联（从计量表用量与单价计算电费/水费，取不到则为 0）
            for (House h : tenantHouses) {
                BigDecimal houseElectricAmount = BigDecimal.ZERO;
                BigDecimal houseWaterAmount = BigDecimal.ZERO;
                BigDecimal placeElectricUsage = BigDecimal.ZERO;
                BigDecimal placeWaterUsage = BigDecimal.ZERO;

                List<Long> meterIds = houseMeterRepository.findMeterIdsByHouseId(h.getId());
                for (Long meterId : meterIds) {
                    Meter meter = meterRepository.findById(meterId).orElse(null);
                    if (meter == null) {
                        continue;
                    }
                    MeterMonthlyUsage usage = meterMonthlyUsageRepository
                            .findByMeterAndYearMonth(meterId, year, month)
                            .orElse(null);
                    if (usage == null || usage.getUsageValue() == null) {
                        continue;
                    }
                    MeterKind meterKind = meter.getMeterKind();
                    ChargeKind chargeKind = meterKind == MeterKind.ELECTRIC ? ChargeKind.ELECTRIC : ChargeKind.WATER;
                    PriceConfig config = priceConfigRepository.findCurrentByKind(chargeKind, chargeDate).orElse(null);
                    BigDecimal unitPrice = config != null ? config.getUnitPrice() : null;
                    BigDecimal amount = unitPrice != null ? unitPrice.multiply(usage.getUsageValue()) : BigDecimal.ZERO;

                    if (meterKind == MeterKind.ELECTRIC) {
                        houseElectricAmount = houseElectricAmount.add(amount);
                        tenantElectricAmount = tenantElectricAmount.add(amount);
                        placeElectricUsage = placeElectricUsage.add(usage.getUsageValue());
                    } else if (meterKind == MeterKind.WATER) {
                        houseWaterAmount = houseWaterAmount.add(amount);
                        tenantWaterAmount = tenantWaterAmount.add(amount);
                        placeWaterUsage = placeWaterUsage.add(usage.getUsageValue());
                    }
                }

                BillHousePO bh = new BillHousePO();
                bh.setBillId(billId);
                bh.setHouseId(h.getId());
                bh.setElectricAmount(houseElectricAmount);
                bh.setWaterAmount(houseWaterAmount);
                bh.setCreatedAt(now);
                bh.setElectricUsage(placeElectricUsage);
                bh.setWaterUsage(placeWaterUsage);
                billHouseMapper.insert(bh);
            }

            // 查询该租客关联的公共场所，写入 t_bill_shared_place（使用 t_shared_place_tenant_share 的分摊结果，取不到则为 0）
            List<Long> placeIds = sharedPlaceTenantRepository.findPlaceIdsByTenantId(tenantId);
            for (Long placeId : placeIds) {
                BigDecimal placeElectricAmount = BigDecimal.ZERO;
                BigDecimal placeWaterAmount = BigDecimal.ZERO;
                BigDecimal placeElectricUsage = BigDecimal.ZERO;
                BigDecimal placeWaterUsage = BigDecimal.ZERO;

                // 从分摊表中查找该公共场所 + 年月 + 当前租客的分摊记录
                List<SharedPlaceTenantShareRepository.SharedPlaceTenantShareRow> shareRows =
                        sharedPlaceTenantShareRepository.listByPlaceAndYearMonth(placeId, year, month);
                for (SharedPlaceTenantShareRepository.SharedPlaceTenantShareRow row : shareRows) {
                    if (!tenantId.equals(row.getTenantId())) {
                        continue;
                    }
                    placeElectricUsage = row.getElectricUsage() == null
                            ? BigDecimal.ZERO : row.getElectricUsage();
                    placeWaterUsage = row.getWaterUsage() == null
                            ? BigDecimal.ZERO : row.getWaterUsage();
                    placeElectricAmount = row.getElectricAmount() == null
                            ? BigDecimal.ZERO : row.getElectricAmount();
                    placeWaterAmount = row.getWaterAmount() == null
                            ? BigDecimal.ZERO : row.getWaterAmount();
                    break;
                }

                // 计入该租客本账单的水费、电费汇总
                tenantElectricAmount = tenantElectricAmount.add(placeElectricAmount);
                tenantWaterAmount = tenantWaterAmount.add(placeWaterAmount);

                BillSharedPlacePO bsp = new BillSharedPlacePO();
                bsp.setBillId(billId);
                bsp.setSharedPlaceId(placeId);
                bsp.setElectricUsage(placeElectricUsage);
                bsp.setWaterUsage(placeWaterUsage);
                bsp.setElectricAmount(placeElectricAmount);
                bsp.setWaterAmount(placeWaterAmount);
                bsp.setCreatedAt(now);
                billSharedPlaceMapper.insert(bsp);
            }

            // 更新账单上的水费、电费汇总
            BigDecimal adjust = saved.getAdjustAmount() == null ? BigDecimal.ZERO : saved.getAdjustAmount();
            BigDecimal total = rentAmount.add(tenantWaterAmount).add(tenantElectricAmount).add(adjust);
            Bill updated = new Bill(
                    saved.getId(),
                    saved.getTenantId(),
                    saved.getBillYear(),
                    saved.getBillMonth(),
                    rentAmount,
                    tenantWaterAmount,
                    tenantElectricAmount,
                    saved.getAdjustAmount(),
                    total,
                    saved.getSettleState(),
                    saved.getPaidAmount(),
                    saved.getPaidTime(),
                    saved.getCreatedAt(),
                    now
            );
            billRepository.save(updated);
        }
    }

    private BillDetailDTO toDetailDTO(Bill bill) {
        BillDetailDTO dto = new BillDetailDTO();
        dto.setId(bill.getId());
        dto.setTenantId(bill.getTenantId());
        if (bill.getTenantId() != null) {
            Tenant tenant = tenantRepository.findById(bill.getTenantId()).orElse(null);
            if (tenant != null) {
                dto.setTenantName(tenant.getFullName());
            }
        }
        dto.setBillYear(bill.getBillYear());
        dto.setBillMonth(bill.getBillMonth());

        // 从 t_bill_house 加载关联房屋及水电明细
        List<BillHousePO> billHouses = bill.getId() == null ? Collections.emptyList()
                : billHouseMapper.selectList(new LambdaQueryWrapper<BillHousePO>()
                        .eq(BillHousePO::getBillId, bill.getId()));
        List<Long> houseIds = new ArrayList<>();
        List<String> houseLabels = new ArrayList<>();
        List<BillDetailDTO.BillHouseItemDTO> houseItems = new ArrayList<>();
        for (BillHousePO bh : billHouses) {
            houseIds.add(bh.getHouseId());
            House house = houseRepository.findById(bh.getHouseId()).orElse(null);
            String label = house != null
                    ? (house.getHouseName() != null ? house.getHouseName() : house.getHouseCode())
                    : "#" + bh.getHouseId();
            houseLabels.add(label);

            BillDetailDTO.BillHouseItemDTO item = new BillDetailDTO.BillHouseItemDTO();
            item.setId(bh.getId());
            item.setHouseId(bh.getHouseId());
            item.setHouseLabel(label);
            item.setShareRatio(bh.getShareRatio());
            item.setElectricUsage(bh.getElectricUsage());
            item.setWaterUsage(bh.getWaterUsage());
            item.setElectricAmount(bh.getElectricAmount());
            item.setWaterAmount(bh.getWaterAmount());
            houseItems.add(item);
        }
        dto.setHouseIds(houseIds);
        dto.setHouseLabels(houseLabels);
        dto.setHouseItems(houseItems);

        // 从 t_bill_shared_place 加载关联公共场所及水电明细
        List<BillSharedPlacePO> billPlaces = bill.getId() == null ? Collections.emptyList()
                : billSharedPlaceMapper.selectList(new LambdaQueryWrapper<BillSharedPlacePO>()
                        .eq(BillSharedPlacePO::getBillId, bill.getId()));
        List<Long> placeIds = new ArrayList<>();
        List<String> placeLabels = new ArrayList<>();
        List<BillDetailDTO.BillSharedPlaceItemDTO> placeItems = new ArrayList<>();
        for (BillSharedPlacePO bsp : billPlaces) {
            placeIds.add(bsp.getSharedPlaceId());
            SharedPlace sp = sharedPlaceRepository.findById(bsp.getSharedPlaceId()).orElse(null);
            String label = sp != null ? sp.getPlaceName() : "#" + bsp.getSharedPlaceId();
            placeLabels.add(label);

            BillDetailDTO.BillSharedPlaceItemDTO item = new BillDetailDTO.BillSharedPlaceItemDTO();
            item.setId(bsp.getId());
            item.setSharedPlaceId(bsp.getSharedPlaceId());
            item.setSharedPlaceLabel(label);
            item.setShareRatio(bsp.getShareRatio());
            item.setElectricUsage(bsp.getElectricUsage());
            item.setWaterUsage(bsp.getWaterUsage());
            item.setElectricAmount(bsp.getElectricAmount());
            item.setWaterAmount(bsp.getWaterAmount());
            placeItems.add(item);
        }
        dto.setSharedPlaceIds(placeIds);
        dto.setSharedPlaceLabels(placeLabels);
        dto.setSharedPlaceItems(placeItems);

        dto.setRentAmount(bill.getRentAmount());
        dto.setWaterAmount(bill.getWaterAmount());
        dto.setElectricAmount(bill.getElectricAmount());
        dto.setAdjustAmount(bill.getAdjustAmount());
        dto.setTotalAmount(bill.getTotalAmount());
        dto.setSettleState(bill.getSettleState() == null ? null : bill.getSettleState().name());
        dto.setPaidAmount(bill.getPaidAmount());
        dto.setPaidTime(bill.getPaidTime());
        dto.setCreatedAt(bill.getCreatedAt());
        dto.setUpdatedAt(bill.getUpdatedAt());
        return dto;
    }

    private BillPaymentDTO toPaymentDTO(BillPayment payment) {
        BillPaymentDTO dto = new BillPaymentDTO();
        dto.setId(payment.getId());
        dto.setBillId(payment.getBillId());
        dto.setPaymentAmount(payment.getPaymentAmount());
        dto.setPaymentTime(payment.getPaymentTime());
        dto.setPaymentNote(payment.getPaymentNote());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }
}

