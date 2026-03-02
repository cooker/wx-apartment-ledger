<template>
  <div class="meter-usage-summary">
    <h2 class="summary-title">水表、电表用量展示</h2>
    <template v-if="!meters.length">
      <p class="summary-hint">请选择上方一行以查看该场所/房屋的用量</p>
    </template>
    <template v-else>
      <div class="summary-toolbar">
        <label class="month-picker">
          <span>选择月份</span>
          <select v-model.number="selectedYear" @change="load">
            <option v-for="y in yearOptions" :key="y" :value="y">{{ y }} 年</option>
          </select>
          <select v-model.number="selectedMonth" @change="load">
            <option v-for="m in 12" :key="m" :value="m">{{ m }} 月</option>
          </select>
        </label>
        <button type="button" class="primary-button" :disabled="loading" @click="load">
          {{ loading ? '加载中...' : '查询' }}
        </button>
      </div>
      <div class="summary-table-wrapper">
        <table class="summary-table">
          <thead>
            <tr>
              <th>编号</th>
              <th>类型</th>
              <th>安装位置</th>
              <th>上月度数</th>
              <th>本月度数</th>
              <th>用量</th>
              <th>单价</th>
              <th>费用（元）</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading && rows.length === 0">
              <td colspan="8" class="table-empty">加载中...</td>
            </tr>
            <tr v-else-if="rows.length === 0">
              <td colspan="8" class="table-empty">暂无用量记录</td>
            </tr>
            <tr v-else v-for="row in rows" :key="row.meterId">
              <td>{{ row.meterCode }}</td>
              <td>{{ row.meterKind === 'ELECTRIC' ? '电表' : '水表' }}</td>
              <td>{{ row.installLocation || '-' }}</td>
              <td>{{ formatReading(row.previousReading) }}</td>
              <td>{{ formatReading(row.currentReading) }}</td>
              <td>{{ row.usageValue != null ? row.usageValue : '未录入' }}</td>
              <td>{{ formatPrice(row.unitPrice) }}</td>
              <td>{{ row.totalAmount != null ? formatAmount(row.totalAmount) : '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { getMeterUsageByMonth, type MeterUsageDTO, type ChargeKind } from '@/api';

export interface MeterUsageSummaryMeter {
  id: number;
  meterCode: string;
  meterKind: ChargeKind;
  installLocation?: string;
}

const props = defineProps<{
  meters: MeterUsageSummaryMeter[];
}>();

function lastMonth(): { year: number; month: number } {
  const d = new Date();
  d.setMonth(d.getMonth() - 1);
  return { year: d.getFullYear(), month: d.getMonth() + 1 };
}

const { year: defaultYear, month: defaultMonth } = lastMonth();
const selectedYear = ref(defaultYear);
const selectedMonth = ref(defaultMonth);

const yearOptions = ref<number[]>([]);
(function () {
  const y = new Date().getFullYear();
  for (let i = y; i >= y - 5; i--) yearOptions.value.push(i);
})();

const loading = ref(false);

interface Row {
  meterId: number;
  meterCode: string;
  meterKind: ChargeKind;
  installLocation?: string;
  previousReading: number | null;
  currentReading: number | null;
  usageValue: number | null;
  unitPrice: number | null;
  totalAmount: number | null;
}
const rows = ref<Row[]>([]);

function formatAmount(n: number): string {
  return typeof n === 'number' ? n.toFixed(2) : '-';
}

function formatReading(n: number | null | undefined): string {
  if (n == null) return '-';
  return Number(n).toFixed(2);
}

function formatPrice(n: number | null | undefined): string {
  if (n == null) return '-';
  return Number(n).toFixed(4);
}

async function load() {
  const list = props.meters;
  if (!list.length) {
    rows.value = [];
    return;
  }
  loading.value = true;
  rows.value = [];
  try {
    const year = selectedYear.value;
    const month = selectedMonth.value;
    const results = await Promise.allSettled(
      list.map((m) => getMeterUsageByMonth(m.id, year, month)),
    );

    rows.value = list.map((meter, i) => {
      const r = results[i];
      let usage: MeterUsageDTO | null = null;
      if (r.status === 'fulfilled') usage = r.value;
      return {
        meterId: meter.id,
        meterCode: meter.meterCode,
        meterKind: meter.meterKind,
        installLocation: meter.installLocation,
        previousReading: usage?.previousReading ?? null,
        currentReading: usage?.currentReading ?? null,
        usageValue: usage?.usageValue ?? null,
        unitPrice: usage?.unitPrice ?? null,
        totalAmount: usage?.totalAmount ?? null,
      };
    });
  } finally {
    loading.value = false;
  }
}

watch(
  () => [props.meters, selectedYear.value, selectedMonth.value] as const,
  () => load(),
  { immediate: true },
);
</script>

<style scoped>
.meter-usage-summary {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.summary-title {
  margin: 0 0 16px;
  font-size: 16px;
}

.summary-hint {
  margin: 0;
  color: #999;
  font-size: 14px;
}

.summary-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.month-picker {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.month-picker select {
  padding: 6px 10px;
  border-radius: 6px;
  border: 1px solid var(--color-border, #d9d9d9);
}

.summary-table-wrapper {
  overflow-x: auto;
}

.summary-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.summary-table th,
.summary-table td {
  padding: 10px 12px;
  text-align: left;
  border-bottom: 1px solid var(--color-border, #eee);
}

.summary-table th {
  background: #fafafa;
  font-weight: 600;
}

.table-empty {
  color: #999;
  text-align: center;
  padding: 24px;
}
</style>
