<template>
  <div class="bill-page card">
    <div class="section-title">账单中心</div>

    <div class="bill-layout">
      <div class="bill-left">
        <div class="bill-filters">
          <div class="filter-group">
            <label>
              <span>年份</span>
              <input
                v-model.number="filters.billYear"
                type="number"
                min="2000"
                max="2100"
                placeholder="例如 2025"
              />
            </label>
            <label>
              <span>月份</span>
              <input
                v-model.number="filters.billMonth"
                type="number"
                min="1"
                max="12"
                placeholder="1-12"
              />
            </label>
          </div>
          <div class="filter-group">
            <label>
              <span>租客（可选）</span>
              <select v-model.number="filters.tenantId">
                <option :value="undefined">全部租客</option>
                <option
                  v-for="t in tenants"
                  :key="t.id"
                  :value="t.id"
                >
                  {{ t.fullName }}（{{ t.mobileNumber }}）
                </option>
              </select>
            </label>
            <label>
              <span>结清状态</span>
              <select v-model="filters.settleState">
                <option value="">全部</option>
                <option value="PENDING">未结清</option>
                <option value="SETTLED">已结清</option>
              </select>
            </label>
          </div>
          <div class="filter-actions">
            <button class="primary-button" @click="reload">查询</button>
            <button class="secondary-button" type="button" @click="resetFilters">
              重置
            </button>
            <button class="secondary-button" type="button" @click="handleRegenerate">
              重新生成
            </button>
            <button class="secondary-button" type="button" @click="handleExportPdf">
              导出PDF
            </button>
          </div>
        </div>

        <div class="bill-table-wrapper">
          <table class="bill-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>租客</th>
                <th>年月</th>
                <th>房屋</th>
                <th>账单总额</th>
                <th>已收金额</th>
                <th>结清状态</th>
                <th>创建时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="loading">
                <td colspan="8" class="table-empty">加载中...</td>
              </tr>
              <tr v-else-if="bills.length === 0">
                <td colspan="8" class="table-empty">暂无数据</td>
              </tr>
              <tr
                v-else
                v-for="item in bills"
                :key="item.id"
                :class="{ active: selectedBill && selectedBill.id === item.id }"
                @click="selectBill(item)"
              >
                <td>{{ item.id }}</td>
                <td>
                  <span>{{ item.tenantName || '-' }}</span>
                  <span v-if="item.tenantId" class="sub-text">#{{ item.tenantId }}</span>
                </td>
                <td>{{ item.billYear }}-{{ String(item.billMonth).padStart(2, '0') }}</td>
                <td>
                  <span>{{ (item.houseLabels && item.houseLabels.length) ? item.houseLabels.join('、') : '-' }}</span>
                  <span v-if="item.houseIds && item.houseIds.length" class="sub-text"
                    >#{{ item.houseIds.join(',#') }}</span>
                </td>
                <td>{{ item.totalAmount ?? '-' }}</td>
                <td>{{ item.paidAmount ?? 0 }}</td>
                <td>
                  <span
                    class="tag"
                    :class="item.settleState === 'SETTLED' ? 'tag-success' : 'tag-warning'"
                  >
                    {{ item.settleState === 'SETTLED' ? '已结清' : '未结清' }}
                  </span>
                </td>
                <td>{{ item.createdAt }}</td>
              </tr>
            </tbody>
          </table>

        </div>
      </div>

      <div class="bill-right" v-if="selectedBill">
        <div class="bill-detail card-inner">
          <h3>账单详情</h3>
          <div class="detail-grid">
            <div><span class="label">账单ID：</span>{{ selectedBill.id }}</div>
            <div>
              <span class="label">租客：</span>
              {{ selectedBill.tenantName || '-' }}
              <span v-if="selectedBill.tenantId" class="sub-text">(#{{ selectedBill.tenantId }})</span>
            </div>
            <div>
              <span class="label">账单年月：</span>
              {{ selectedBill.billYear }}-{{ String(selectedBill.billMonth).padStart(2, '0') }}
            </div>
            <div>
              <span class="label">房屋：</span>
              {{ (selectedBill.houseLabels && selectedBill.houseLabels.length) ? selectedBill.houseLabels.join('、') : '-' }}
              <span v-if="selectedBill.houseIds && selectedBill.houseIds.length" class="sub-text"
                >({{ selectedBill.houseIds.join(', ') }})</span>
            </div>
            <div>
              <span class="label">公共场所：</span>
              {{ (selectedBill.sharedPlaceLabels && selectedBill.sharedPlaceLabels.length) ? selectedBill.sharedPlaceLabels.join('、') : '-' }}
              <span v-if="selectedBill.sharedPlaceIds && selectedBill.sharedPlaceIds.length" class="sub-text"
                >({{ selectedBill.sharedPlaceIds.join(', ') }})</span>
            </div>
            <div><span class="label">房租：</span>{{ selectedBill.rentAmount ?? '-' }}</div>
            <div>
              <span class="label">水费：</span>
              {{ formatAmount(selectedBill.waterAmount) }} 元
              <span class="sub-text">
                （约 {{ renderUsage(selectedBill.waterAmount, 'WATER') }} 度）
              </span>
            </div>
            <div>
              <span class="label">电费：</span>
              {{ formatAmount(selectedBill.electricAmount) }} 元
              <span class="sub-text">
                （约 {{ renderUsage(selectedBill.electricAmount, 'ELECTRIC') }} 度）
              </span>
            </div>
            <div><span class="label">调整金额：</span>{{ selectedBill.adjustAmount ?? '-' }}</div>
            <div><span class="label">合计金额：</span>{{ selectedBill.totalAmount ?? '-' }}</div>
            <div><span class="label">已收金额：</span>{{ selectedBill.paidAmount ?? 0 }}</div>
            <div><span class="label">最后收款时间：</span>{{ selectedBill.paidTime || '-' }}</div>
            <div><span class="label">创建时间：</span>{{ selectedBill.createdAt }}</div>
            <div><span class="label">更新时间：</span>{{ selectedBill.updatedAt || '-' }}</div>
          </div>
        </div>

        <div class="bill-detail card-inner" v-if="selectedBill.houseItems?.length">
          <h3>房屋水电明细</h3>
          <table class="bill-table small">
            <thead>
              <tr>
                <th>房屋</th>
                <th>电量</th>
                <th>电费</th>
                <th>水量</th>
                <th>水费</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in selectedBill.houseItems" :key="item.id">
                <td>{{ item.houseLabel }}</td>
                <td>{{ item.electricUsage ?? '-' }}</td>
                <td>{{ formatAmount(item.electricAmount) }}</td>
                <td>{{ item.waterUsage ?? '-' }}</td>
                <td>{{ formatAmount(item.waterAmount) }}</td>
              </tr>
            </tbody>
            <tfoot>
              <tr class="summary-row">
                <td>合计</td>
                <td>{{ houseElectricUsageTotal }}</td>
                <td>{{ formatAmount(houseElectricAmountTotal) }}</td>
                <td>{{ houseWaterUsageTotal }}</td>
                <td>{{ formatAmount(houseWaterAmountTotal) }}</td>
              </tr>
            </tfoot>
          </table>
        </div>

        <div class="bill-detail card-inner" v-if="selectedBill.sharedPlaceItems?.length">
          <h3>公共场所水电明细（可调整）</h3>
          <table class="bill-table small">
            <thead>
              <tr>
                <th>公共场所</th>
                <th>电量</th>
                <th>电费（元）</th>
                <th>水量</th>
                <th>水费（元）</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in selectedBill.sharedPlaceItems" :key="item.id">
                <td>{{ item.sharedPlaceLabel }}</td>
                <td>{{ item.electricUsage ?? '-' }}</td>
                <td>
                  <input
                    v-model.number="item.electricAmount"
                    type="number"
                    min="0"
                    step="0.01"
                    class="inline-input"
                  />
                </td>
                <td>{{ item.waterUsage ?? '-' }}</td>
                <td>
                  <input
                    v-model.number="item.waterAmount"
                    type="number"
                    min="0"
                    step="0.01"
                    class="inline-input"
                  />
                </td>
              </tr>
            </tbody>
            <tfoot>
              <tr class="summary-row">
                <td>合计</td>
                <td>{{ sharedElectricUsageTotal }}</td>
                <td>{{ formatAmount(sharedElectricAmountTotal) }}</td>
                <td>{{ sharedWaterUsageTotal }}</td>
                <td>{{ formatAmount(sharedWaterAmountTotal) }}</td>
              </tr>
            </tfoot>
          </table>
          <div class="form-actions" style="margin-top: 8px">
            <button
              type="button"
              class="primary-button"
              @click="saveSharedPlaceAdjust"
            >
              保存公共场所水电调整
            </button>
          </div>
        </div>

        <div class="bill-payments card-inner">
          <h3>收款记录</h3>
          <table class="bill-table small">
            <thead>
              <tr>
                <th>ID</th>
                <th>金额</th>
                <th>收款时间</th>
                <th>备注</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="paymentsLoading">
                <td colspan="5" class="table-empty">加载中...</td>
              </tr>
              <tr v-else-if="payments.length === 0">
                <td colspan="5" class="table-empty">暂无收款记录</td>
              </tr>
              <tr v-else v-for="p in payments" :key="p.id">
                <td>{{ p.id }}</td>
                <td>{{ p.paymentAmount }}</td>
                <td>{{ p.paymentTime }}</td>
                <td>{{ p.paymentNote || '-' }}</td>
                <td>
                  <button
                    type="button"
                    class="link-button"
                    @click.stop="handleDeletePayment(p)"
                  >
                    删除
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="bill-payment-form card-inner">
          <h3>登记收款</h3>
          <form class="payment-form" @submit.prevent="submitPayment">
            <label class="form-item">
              <span>收款金额</span>
              <input
                v-model.number="paymentForm.paymentAmount"
                type="number"
                min="0.01"
                step="0.01"
                required
              />
            </label>
            <label class="form-item">
              <span>收款时间</span>
              <input v-model="paymentForm.paymentTime" type="datetime-local" />
            </label>
            <label class="form-item">
              <span>备注</span>
              <textarea v-model="paymentForm.paymentNote" rows="2" maxlength="255" />
            </label>
            <div class="form-actions">
              <button type="submit" class="primary-button">确认收款</button>
              <button
                type="button"
                class="secondary-button"
                @click="resetPaymentForm"
              >
                重置
              </button>
            </div>
          </form>
        </div>
      </div>

      <div class="bill-right empty" v-else>
        <p class="table-empty">请选择左侧一条账单查看详情与收款信息</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue';
import {
  adjustBillSharedPlaces,
  getCurrentPriceConfig,
  listBillPayments,
  pageBills,
  pageTenants,
  receiveBillPayment,
  regenerateBills,
  deleteBillPayment,
  type BillDetail,
  type BillPayment,
  type ChargeKind,
  type TenantDetail,
  exportBillsPdf,
} from '@/api';

const bills = ref<BillDetail[]>([]);
const loading = ref(false);
const pageNo = ref(1);
const pageSize = ref(500);
const total = ref(0);

const now = new Date();
const currentYear = now.getFullYear();
const currentMonth = now.getMonth() + 1;

const filters = reactive({
  tenantId: undefined as number | undefined,
  billYear: currentYear as number | undefined,
  billMonth: currentMonth as number | undefined,
  settleState: '' as '' | 'PENDING' | 'SETTLED',
});

const selectedBill = ref<BillDetail | null>(null);
const payments = ref<BillPayment[]>([]);
const paymentsLoading = ref(false);

const tenants = ref<TenantDetail[]>([]);

const paymentForm = reactive({
  paymentAmount: undefined as number | undefined,
  paymentTime: '',
  paymentNote: '',
});

const houseElectricUsageTotal = computed(() => {
  const bill = selectedBill.value;
  if (!bill?.houseItems?.length) return 0;
  return bill.houseItems.reduce(
    (sum, item) => sum + (item.electricUsage || 0),
    0,
  );
});

const houseWaterUsageTotal = computed(() => {
  const bill = selectedBill.value;
  if (!bill?.houseItems?.length) return 0;
  return bill.houseItems.reduce(
    (sum, item) => sum + (item.waterUsage || 0),
    0,
  );
});

const houseElectricAmountTotal = computed(() => {
  const bill = selectedBill.value;
  if (!bill?.houseItems?.length) return 0;
  return bill.houseItems.reduce(
    (sum, item) => sum + (item.electricAmount || 0),
    0,
  );
});

const houseWaterAmountTotal = computed(() => {
  const bill = selectedBill.value;
  if (!bill?.houseItems?.length) return 0;
  return bill.houseItems.reduce(
    (sum, item) => sum + (item.waterAmount || 0),
    0,
  );
});

const sharedElectricUsageTotal = computed(() => {
  const bill = selectedBill.value;
  if (!bill?.sharedPlaceItems?.length) return 0;
  return bill.sharedPlaceItems.reduce(
    (sum, item) => sum + (item.electricUsage || 0),
    0,
  );
});

const sharedWaterUsageTotal = computed(() => {
  const bill = selectedBill.value;
  if (!bill?.sharedPlaceItems?.length) return 0;
  return bill.sharedPlaceItems.reduce(
    (sum, item) => sum + (item.waterUsage || 0),
    0,
  );
});

const sharedElectricAmountTotal = computed(() => {
  const bill = selectedBill.value;
  if (!bill?.sharedPlaceItems?.length) return 0;
  return bill.sharedPlaceItems.reduce(
    (sum, item) => sum + (item.electricAmount || 0),
    0,
  );
});

const sharedWaterAmountTotal = computed(() => {
  const bill = selectedBill.value;
  if (!bill?.sharedPlaceItems?.length) return 0;
  return bill.sharedPlaceItems.reduce(
    (sum, item) => sum + (item.waterAmount || 0),
    0,
  );
});

const waterUnitPrice = ref<number | null>(null);
const electricUnitPrice = ref<number | null>(null);

const displayYear = computed(() => {
  if (filters.billYear) return filters.billYear;
  if (bills.value.length > 0) return bills.value[0].billYear;
  return new Date().getFullYear();
});

const displayMonth = computed(() => {
  if (filters.billMonth) return filters.billMonth;
  if (bills.value.length > 0) return bills.value[0].billMonth;
  return new Date().getMonth() + 1;
});

type TenantBillGroup = {
  tenantId: number | null;
  tenantName: string;
  houseLabels: string[];
  sharedPlaceLabels: string[];
  rentAmount: number;
  waterAmount: number;
  electricAmount: number;
  adjustAmount: number;
  totalAmount: number;
};

const tenantBillGroups = computed<TenantBillGroup[]>(() => {
  if (!bills.value.length) {
    return [];
  }
  const map = new Map<string, TenantBillGroup>();
  for (const bill of bills.value) {
    const key = bill.tenantId != null ? String(bill.tenantId) : bill.tenantName || 'unknown';
    let group = map.get(key);
    if (!group) {
      group = {
        tenantId: bill.tenantId ?? null,
        tenantName: bill.tenantName || '',
        houseLabels: [],
        sharedPlaceLabels: [],
        rentAmount: 0,
        waterAmount: 0,
        electricAmount: 0,
        adjustAmount: 0,
        totalAmount: 0,
      };
      map.set(key, group);
    }

    const labels = bill.houseLabels || [];
    for (const label of labels) {
      if (label && !group.houseLabels.includes(label)) {
        group.houseLabels.push(label);
      }
    }

    const spLabels = bill.sharedPlaceLabels || [];
    for (const label of spLabels) {
      if (label && !group.sharedPlaceLabels.includes(label)) {
        group.sharedPlaceLabels.push(label);
      }
    }

    group.rentAmount += bill.rentAmount || 0;
    group.waterAmount += bill.waterAmount || 0;
    group.electricAmount += bill.electricAmount || 0;
    group.adjustAmount += bill.adjustAmount || 0;
    group.totalAmount += bill.totalAmount || 0;
  }
  return Array.from(map.values());
});

function formatAmount(value: number | undefined | null): string {
  if (value === null || value === undefined) {
    return '0.00';
  }
  return Number(value).toFixed(2);
}

function renderUsage(amount: number | undefined | null, kind: ChargeKind): string {
  const unitPrice = kind === 'WATER' ? waterUnitPrice.value : electricUnitPrice.value;
  if (!unitPrice || !amount || amount <= 0) {
    return '-';
  }
  const usage = amount / unitPrice;
  return usage.toFixed(2);
}

async function reload() {
  loading.value = true;
  try {
    // 每次查询前刷新当前单价，便于估算当月用量
    await loadUnitPrices();
    const res = await pageBills({
      tenantId: filters.tenantId || undefined,
      billYear: filters.billYear || undefined,
      billMonth: filters.billMonth || undefined,
      settleState: filters.settleState || undefined,
      pageNo: pageNo.value,
      pageSize: pageSize.value,
    });
    bills.value = res.records || [];
    total.value = res.total || 0;
    if (bills.value.length > 0) {
      if (!selectedBill.value) {
        selectBill(bills.value[0]);
      } else {
        const match = bills.value.find((b) => b.id === selectedBill.value?.id);
        if (match) {
          selectBill(match);
        }
      }
    } else {
      selectedBill.value = null;
      payments.value = [];
    }
  } finally {
    loading.value = false;
  }
}

async function loadUnitPrices() {
  try {
    const [water, electric] = await Promise.all([
      getCurrentPriceConfig('WATER'),
      getCurrentPriceConfig('ELECTRIC'),
    ]);
    waterUnitPrice.value = water?.unitPrice ?? null;
    electricUnitPrice.value = electric?.unitPrice ?? null;
  } catch {
    // 单价获取失败时，不阻塞账单查询，只是不展示用量
    waterUnitPrice.value = null;
    electricUnitPrice.value = null;
  }
}

async function loadTenants() {
  try {
    const res = await pageTenants({ pageNo: 1, pageSize: 500 });
    tenants.value = res.records || [];
  } catch {
    tenants.value = [];
  }
}

function resetFilters() {
  filters.tenantId = undefined;
  filters.billYear = currentYear;
  filters.billMonth = currentMonth;
  filters.settleState = '';
  pageNo.value = 1;
  reload();
}

function selectBill(item: BillDetail) {
  selectedBill.value = item;
  loadPayments(item.id);
}

async function loadPayments(billId: number) {
  paymentsLoading.value = true;
  try {
    payments.value = await listBillPayments(billId);
  } finally {
    paymentsLoading.value = false;
  }
}

function resetPaymentForm() {
  paymentForm.paymentAmount = undefined;
  paymentForm.paymentTime = '';
  paymentForm.paymentNote = '';
}

async function submitPayment() {
  if (!selectedBill.value) {
    alert('请先选择一条账单');
    return;
  }
  if (!paymentForm.paymentAmount || paymentForm.paymentAmount <= 0) {
    alert('请输入正确的收款金额');
    return;
  }
  const payload = {
    billId: selectedBill.value.id,
    paymentAmount: paymentForm.paymentAmount,
    paymentTime: paymentForm.paymentTime ? new Date(paymentForm.paymentTime).toISOString() : undefined,
    paymentNote: paymentForm.paymentNote.trim() || undefined,
  };
  await receiveBillPayment(payload);
  resetPaymentForm();
  await reload();
  if (selectedBill.value) {
    await loadPayments(selectedBill.value.id);
  }
}

async function handleDeletePayment(p: BillPayment) {
  if (!selectedBill.value) {
    return;
  }
  if (!window.confirm(`确定要删除该收款记录（ID=${p.id}，金额=${p.paymentAmount}）吗？`)) {
    return;
  }
  await deleteBillPayment(p.id);
  await reload();
  await loadPayments(selectedBill.value.id);
}

async function handleRegenerate() {
  if (!filters.billYear || !filters.billMonth) {
    alert('请先选择要重新生成的账单年份和月份');
    return;
  }
  if (
    !window.confirm(
      `确定要删除 ${filters.billYear} 年 ${filters.billMonth} 月${
        filters.tenantId ? '（当前租客）' : ''
      }的未结清账单并重新生成吗？此操作会删除对应账单及其收款记录。`,
    )
  ) {
    return;
  }
  await regenerateBills({
    billYear: filters.billYear,
    billMonth: filters.billMonth,
    tenantId: filters.tenantId || undefined,
  });
  await reload();
}

async function saveSharedPlaceAdjust() {
  if (!selectedBill.value || !selectedBill.value.sharedPlaceItems?.length) {
    return;
  }
  const items = selectedBill.value.sharedPlaceItems
    .filter((item) => item.id != null)
    .map((item) => ({
      id: item.id,
      electricAmount: item.electricAmount ?? 0,
      waterAmount: item.waterAmount ?? 0,
    }));
  await adjustBillSharedPlaces(selectedBill.value.id, { items });
  await reload();
}

async function handleExportPdf() {
  const params = {
    tenantId: filters.tenantId || undefined,
    billYear: filters.billYear || undefined,
    billMonth: filters.billMonth || undefined,
  };
  const data = await exportBillsPdf(params);
  const blob = new Blob([data as any], { type: 'application/pdf' });
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  const year = params.billYear ?? new Date().getFullYear();
  const month = params.billMonth ?? new Date().getMonth() + 1;
  a.href = url;
  a.download = `账单明细-${year}-${String(month).padStart(2, '0')}.pdf`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  window.URL.revokeObjectURL(url);
}

watch(
  () => [filters.tenantId, filters.billYear, filters.billMonth, filters.settleState],
  () => {
    pageNo.value = 1;
  },
);

onMounted(() => {
  loadTenants();
  reload();
});
</script>

<style scoped>
.bill-page {
  margin-top: 24px;
}

.bill-layout {
  display: grid;
  grid-template-columns: 2.1fr 1.4fr;
  gap: 20px;
  margin-top: 12px;
}

.bill-left {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.bill-right {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.bill-right.empty {
  display: flex;
  align-items: center;
  justify-content: center;
}

.bill-filters {
  display: grid;
  grid-template-columns: 1fr;
  gap: 8px;
  margin-bottom: 8px;
}

.filter-group {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.bill-filters label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
}

.bill-filters input,
.bill-filters select {
  padding: 6px 8px;
  border-radius: 6px;
  border: 1px solid var(--color-border);
}

.filter-actions {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}

.bill-table-wrapper {
  background-color: #fff;
  border-radius: 8px;
  padding: 12px;
  border: 1px solid var(--color-border-light);
}

.bill-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.bill-table.small th,
.bill-table.small td {
  padding-top: 4px;
  padding-bottom: 4px;
}

.bill-table th,
.bill-table td {
  padding: 8px 10px;
  border-bottom: 1px solid var(--color-border);
  text-align: left;
}

.bill-table th {
  background-color: #fafafa;
}

.bill-table tr.active {
  background-color: #e6f4ff;
}

.table-empty {
  text-align: center;
  color: var(--color-text-secondary);
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
  font-size: 12px;
}

.card-inner {
  background-color: #fff;
  border-radius: 8px;
  padding: 12px 14px;
  border: 1px solid var(--color-border-light);
}

.bill-preview {
  margin-bottom: 12px;
  font-family: Menlo, Monaco, Consolas, 'Courier New', monospace;
}

.bill-preview-title {
  margin: 0 0 8px;
  font-size: 13px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.bill-preview-title .title-text {
  font-weight: 600;
}

.bill-preview-item {
  padding: 8px 0;
  border-top: 1px dashed var(--color-border-light);
  font-size: 13px;
}

.bill-preview-item:first-of-type {
  border-top: none;
}

.preview-line {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.preview-line.main-line {
  font-weight: 600;
  margin-bottom: 4px;
}

.preview-empty {
  padding: 8px 0;
}

.indent {
  display: inline-block;
  width: 48px;
}

.gap {
  display: inline-block;
  width: 24px;
}

.divider {
  margin: 0 8px;
  color: var(--color-text-secondary);
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px 16px;
  font-size: 13px;
  margin-top: 8px;
}

.label {
  color: var(--color-text-secondary);
}

.sub-text {
  margin-left: 4px;
  font-size: 11px;
  color: var(--color-text-secondary);
}

.tag {
  display: inline-block;
  padding: 0 8px;
  border-radius: 12px;
  font-size: 12px;
  line-height: 20px;
}

.tag-success {
  background-color: #f6ffed;
  color: #52c41a;
}

.tag-warning {
  background-color: #fff7e6;
  color: #fa8c16;
}

.bill-payments h3,
.bill-payment-form h3 {
  margin-top: 0;
  margin-bottom: 8px;
}

.payment-form {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
}

.form-item input,
.form-item textarea {
  padding: 6px 8px;
  border-radius: 6px;
  border: 1px solid var(--color-border);
}

.form-actions {
  margin-top: 4px;
  display: flex;
  gap: 8px;
}

@media (max-width: 1080px) {
  .bill-layout {
    grid-template-columns: 1fr;
  }
}
</style>

