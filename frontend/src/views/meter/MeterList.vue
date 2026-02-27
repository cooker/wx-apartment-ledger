<template>
  <div class="meter-page card">
    <div class="section-title">水电表管理</div>

    <div class="meter-toolbar">
      <div class="kind-switch">
        <button
          class="tab-button"
          :class="{ active: currentKind === 'ELECTRIC' }"
          @click="switchKind('ELECTRIC')"
        >
          电表
        </button>
        <button
          class="tab-button"
          :class="{ active: currentKind === 'WATER' }"
          @click="switchKind('WATER')"
        >
          水表
        </button>
      </div>

      <div class="meter-search">
        <input
          v-model="keyword"
          type="text"
          class="search-input"
          placeholder="按编号或安装位置搜索"
          @keyup.enter="reload"
        />
        <button class="primary-button" @click="startCreate">新增计量表</button>
      </div>
    </div>

    <div class="meter-content">
      <div class="meter-table-wrapper">
        <table class="meter-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>编号</th>
              <th>类型</th>
              <th>安装位置</th>
              <th>是否总表</th>
              <th>上月用量</th>
              <th>上月差异</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="7" class="table-empty">加载中...</td>
            </tr>
            <tr v-else-if="meters.length === 0">
              <td colspan="7" class="table-empty">暂无计量表</td>
            </tr>
            <tr v-else v-for="item in meters" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.meterCode }}</td>
              <td>{{ item.meterKind === 'ELECTRIC' ? '电表' : '水表' }}</td>
              <td>{{ item.installLocation || '-' }}</td>
              <td>{{ item.mainMeter ? '是' : '否' }}</td>
              <td>
                <span v-if="thisMonthUsageMap[item.id] != null">
                  {{ thisMonthUsageMap[item.id] }}
                </span>
                <span v-else>未录入</span>
              </td>
              <td>
                <span v-if="item.mainMeter">
                  {{ item.currentMonthDiff != null ? item.currentMonthDiff : '-' }}
                </span>
                <span v-else>-</span>
              </td>
              <td>{{ item.createdAt || '-' }}</td>
              <td>
                <button class="link-button" @click="startEdit(item)">编辑</button>
                <button class="link-button" @click="selectMeter(item)">用量</button>
                <button
                  v-if="item.mainMeter"
                  class="link-button"
                  @click="configureSet(item)"
                >
                  配置子表
                </button>
                <button class="link-button danger" @click="deleteMeterItem(item)">
                  删除
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <div class="pagination" v-if="total > 0">
          <span>共 {{ total }} 条</span>
          <button :disabled="pageNo <= 1" @click="changePage(pageNo - 1)">上一页</button>
          <span>第 {{ pageNo }} / {{ totalPages }} 页</span>
          <button :disabled="pageNo >= totalPages" @click="changePage(pageNo + 1)">
            下一页
          </button>
        </div>
      </div>

      <div class="meter-form-wrapper">
        <h3 class="form-title">
          {{ editingId ? '编辑计量表' : '新增计量表' }}（{{ currentKindLabel }}）
        </h3>
        <form class="meter-form" @submit.prevent="submit">
          <div class="form-item">
            <span>编号</span>
            <input
              v-model="form.meterCode"
              type="text"
              required
              maxlength="64"
              :readonly="!!editingId"
            />
          </div>
          <div class="form-item">
            <span>安装位置</span>
            <input v-model="form.installLocation" type="text" maxlength="128" />
          </div>
          <div class="form-item checkbox-item">
            <label>
              <input v-model="form.mainMeter" type="checkbox" />
              设为总表
            </label>
          </div>
          <div class="form-actions">
            <button type="submit" class="primary-button">保存</button>
            <button type="button" class="secondary-button" @click="resetForm">重置</button>
          </div>
        </form>
      </div>
    </div>

    <div v-if="editingSetMain" class="usage-section card">
      <div class="usage-header">
        <div>
          <div class="section-title">总表子表配置</div>
          <div class="usage-meter-info">
            主表：{{ editingSetMain.meterCode }}（{{
              editingSetMain.meterKind === 'ELECTRIC' ? '电表' : '水表'
            }}）
          </div>
        </div>
      </div>

      <div class="usage-content">
        <div class="usage-table-wrapper">
          <div class="section-title">可选子表</div>
          <table class="usage-table">
            <thead>
              <tr>
                <th>选择</th>
                <th>ID</th>
                <th>编号</th>
                <th>安装位置</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="setMetersLoading">
                <td colspan="4" class="table-empty">加载中...</td>
              </tr>
              <tr v-else-if="candidateMeters.length === 0">
                <td colspan="4" class="table-empty">暂无可选子表</td>
              </tr>
              <tr v-else v-for="m in candidateMeters" :key="m.id">
                <td>
                  <input
                    type="checkbox"
                    :value="m.id"
                    v-model="setForm.childMeterIds"
                  />
                </td>
                <td>{{ m.id }}</td>
                <td>{{ m.meterCode }}</td>
                <td>{{ m.installLocation || '-' }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="usage-form-wrapper">
          <h3 class="form-title">集合信息</h3>
          <form class="usage-form" @submit.prevent="submitSet">
            <div class="form-item">
              <span>集合名称</span>
              <input v-model="setForm.setName" type="text" maxlength="64" required />
            </div>
            <div class="form-actions">
              <button type="submit" class="primary-button">保存配置</button>
              <button type="button" class="secondary-button" @click="cancelSet">
                取消
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <div v-if="selectedMeter" class="usage-section card">
      <div class="usage-header">
        <div>
          <div class="section-title">
            {{ selectedMeter.meterKind === 'ELECTRIC' ? '电表' : '水表' }}月度用量
          </div>
          <div class="usage-meter-info">
            编号：{{ selectedMeter.meterCode }}，
            位置：{{ selectedMeter.installLocation || '-' }}
          </div>
        </div>
        <div class="usage-filter">
          <span>年份：</span>
          <input v-model.number="usageYear" type="number" min="2000" max="2100" />
          <button class="secondary-button" @click="reloadUsages">查询</button>
        </div>
      </div>

      <div class="usage-content">
        <div class="usage-table-wrapper">
          <table class="usage-table">
            <thead>
              <tr>
                <th>年份</th>
                <th>月份</th>
                <th>上月读数</th>
                <th>本月读数</th>
                <th>本月用量</th>
                <th>单价</th>
                <th>费用</th>
                <th>更新时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="usageLoading">
                <td colspan="8" class="table-empty">加载中...</td>
              </tr>
              <tr v-else-if="usages.length === 0">
                <td colspan="8" class="table-empty">暂无用量记录</td>
              </tr>
            <tr v-else v-for="item in usages" :key="item.id">
                <td>{{ item.usageYear }}</td>
                <td>{{ item.usageMonth }}</td>
                <td>{{ item.previousReading }}</td>
                <td>{{ item.currentReading }}</td>
                <td>{{ item.usageValue }}</td>
                <td>{{ item.unitPrice ?? '-' }}</td>
                <td>{{ item.totalAmount ?? '-' }}</td>
              <td>
                <span>{{ item.updatedAt || item.createdAt || '-' }}</span>
                <button class="link-button" @click="editUsage(item)">编辑</button>
                <button class="link-button danger" @click="deleteUsage(item)">删除</button>
              </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="usage-form-wrapper">
          <h3 class="form-title">录入/更新月度用量</h3>
          <form class="usage-form" @submit.prevent="submitUsage">
            <div class="form-item-inline">
              <div class="form-item">
                <span>年份</span>
                <input v-model.number="usageForm.usageYear" type="number" required />
              </div>
              <div class="form-item">
                <span>月份</span>
                <input v-model.number="usageForm.usageMonth" type="number" min="1" max="12" required />
              </div>
            </div>
            <div class="form-item">
              <span>上月读数</span>
              <input
                v-model.number="usageForm.previousReading"
                type="number"
                step="0.0001"
                required
              />
            </div>
            <div class="form-item">
              <span>本月读数</span>
              <input
                v-model.number="usageForm.currentReading"
                type="number"
                step="0.0001"
                required
              />
            </div>
            <div class="form-actions">
              <button type="submit" class="primary-button">保存用量</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue';
import type {
  ChargeKind,
  MeterDetail,
  MeterUsageDTO,
  MeterPayload,
  MeterSetDTO,
} from '@/api';
import { pageMeters, createMeter, updateMeter } from '@/api';
import {
  pageMeterUsages,
  upsertMeterUsage,
  deleteMeter,
  getMeterUsageByMonth,
  deleteMeterUsage,
  getMeterSetByMain,
  createMeterSet,
  updateMeterSet,
} from '@/api';

const currentKind = ref<ChargeKind>('ELECTRIC');
const meters = ref<MeterDetail[]>([]);
const loading = ref(false);
const keyword = ref('');
const pageNo = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 上一月份用量值（key 为 meterId，value 为上月用量；null 表示未录入）
const now = new Date();
let currentYear = now.getFullYear();
let currentMonth = now.getMonth(); // 0-11，先取上一个自然月
if (currentMonth === 0) {
  currentYear -= 1;
  currentMonth = 12;
}
const thisMonthUsageMap = reactive<Record<number, number | null>>({});

const editingId = ref<number | null>(null);
const selectedMeter = ref<MeterDetail | null>(null);
const editingSetMain = ref<MeterDetail | null>(null);

const form = reactive({
  meterCode: '',
  installLocation: '',
  mainMeter: false,
});

const totalPages = computed(() => {
  return total.value === 0 ? 1 : Math.ceil(total.value / pageSize.value);
});

const currentKindLabel = computed(() =>
  currentKind.value === 'ELECTRIC' ? '电表' : '水表',
);

async function reload() {
  loading.value = true;
  try {
    const res = await pageMeters({
      meterKind: currentKind.value,
      keyword: keyword.value || undefined,
      pageNo: pageNo.value,
      pageSize: pageSize.value,
    });
    meters.value = res.records || [];
    total.value = res.total || 0;
    await refreshThisMonthUsage();
  } finally {
    loading.value = false;
  }
}

function changePage(next: number) {
  if (next < 1) return;
  pageNo.value = next;
  reload();
}

function switchKind(kind: ChargeKind) {
  if (currentKind.value === kind) return;
  currentKind.value = kind;
  resetForm();
  selectedMeter.value = null;
  pageNo.value = 1;
  reload();
}

function startCreate() {
  editingId.value = null;
  resetForm();
}

function startEdit(item: MeterDetail) {
  editingId.value = item.id;
  form.meterCode = item.meterCode;
  form.installLocation = item.installLocation || '';
  form.mainMeter = !!item.mainMeter;
}

function resetForm() {
  form.meterCode = '';
  form.installLocation = '';
  form.mainMeter = false;
}

async function submit() {
  if (!form.meterCode.trim()) {
    alert('编号不能为空');
    return;
  }
  const payload: MeterPayload = {
    meterCode: form.meterCode.trim(),
    meterKind: currentKind.value,
    installLocation: form.installLocation.trim() || undefined,
    mainMeter: form.mainMeter,
  };
  if (editingId.value) {
    await updateMeter(editingId.value, payload);
  } else {
    await createMeter(payload);
  }
  await reload();
}

// 用量管理
const usages = ref<MeterUsageDTO[]>([]);
const usageLoading = ref(false);
const usageYear = ref<number>(new Date().getFullYear());

const usageForm = reactive({
  usageYear: new Date().getFullYear(),
  usageMonth: new Date().getMonth() + 1,
  previousReading: 0,
  currentReading: 0,
});

// 总表-子表配置
const candidateMeters = ref<MeterDetail[]>([]);
const setMetersLoading = ref(false);
const currentSet = ref<MeterSetDTO | null>(null);
const setForm = reactive({
  setName: '',
  childMeterIds: [] as number[],
});

function selectMeter(item: MeterDetail) {
  selectedMeter.value = item;
  usageYear.value = new Date().getFullYear();
  reloadUsages();
}

async function reloadUsages() {
  if (!selectedMeter.value) return;
  usageLoading.value = true;
  try {
    const res = await pageMeterUsages({
      meterId: selectedMeter.value.id,
      usageYear: usageYear.value || undefined,
      pageNo: 1,
      pageSize: 12,
    });
    usages.value = res.records || [];
  } finally {
    usageLoading.value = false;
  }
}

async function submitUsage() {
  if (!selectedMeter.value) {
    alert('请先选择计量表');
    return;
  }
  if (!usageForm.usageYear || !usageForm.usageMonth) {
    alert('年份和月份不能为空');
    return;
  }
  const payload = {
    meterId: selectedMeter.value.id,
    usageYear: usageForm.usageYear,
    usageMonth: usageForm.usageMonth,
    previousReading: usageForm.previousReading,
    currentReading: usageForm.currentReading,
  };
  await upsertMeterUsage(payload);
  await reloadUsages();
}

function editUsage(item: MeterUsageDTO) {
  if (!selectedMeter.value) {
    return;
  }
  usageForm.usageYear = item.usageYear;
  usageForm.usageMonth = item.usageMonth;
  usageForm.previousReading = item.previousReading;
  usageForm.currentReading = item.currentReading;
}

async function deleteUsage(item: MeterUsageDTO) {
  if (!confirm(`确认删除 ${item.usageYear} 年 ${item.usageMonth} 月用量记录？`)) {
    return;
  }
  await deleteMeterUsage(item.id);
  await reloadUsages();
}

async function deleteMeterItem(item: MeterDetail) {
  if (!confirm(`确认删除计量表 ${item.meterCode} ？`)) {
    return;
  }
  await deleteMeter(item.id);
  if (selectedMeter.value && selectedMeter.value.id === item.id) {
    selectedMeter.value = null;
    usages.value = [];
  }
  await reload();
}

async function refreshPreviousReading() {
  if (!selectedMeter.value) {
    return;
  }
  const year = usageForm.usageYear;
  const month = usageForm.usageMonth;
  if (!year || !month) {
    return;
  }
  let prevYear = month === 1 ? year - 1 : year;
  let prevMonth = month === 1 ? 12 : month - 1;
  if (prevYear <= 0) {
    return;
  }
  try {
    const prev = await getMeterUsageByMonth(
      selectedMeter.value.id,
      prevYear,
      prevMonth,
    );
    if (prev && typeof prev.currentReading === 'number') {
      usageForm.previousReading = prev.currentReading;
    }
  } catch {
    // 无记录时忽略
  }
}

watch(
  () => [selectedMeter.value?.id, usageForm.usageYear, usageForm.usageMonth],
  () => {
    refreshPreviousReading();
  },
);

async function refreshThisMonthUsage() {
  // 重置
  Object.keys(thisMonthUsageMap).forEach((key) => {
    delete thisMonthUsageMap[Number(key)];
  });
  const list = meters.value;
  if (!list.length) return;
  await Promise.all(
    list.map(async (m) => {
      try {
        const usage = await getMeterUsageByMonth(m.id, currentYear, currentMonth);
        thisMonthUsageMap[m.id] = usage.usageValue ?? null;
      } catch {
        thisMonthUsageMap[m.id] = null;
      }
    }),
  );
}

async function configureSet(item: MeterDetail) {
  editingSetMain.value = item;
  setMetersLoading.value = true;
  setForm.setName = '';
  setForm.childMeterIds = [];
  currentSet.value = null;
  try {
    // 拉取同类型所有计量表作为候选子表（不包含主表自身）
    const res = await pageMeters({
      meterKind: item.meterKind,
      pageNo: 1,
      pageSize: 500,
    });
    candidateMeters.value =
      (res.records || []).filter((m) => m.id !== item.id) ?? [];

    // 获取当前已有的集合配置
    const set = await getMeterSetByMain(item.id).catch(() => null);
    if (set) {
      currentSet.value = set;
      setForm.setName = set.setName;
      setForm.childMeterIds = [...(set.childMeterIds || [])];
    } else {
      setForm.setName = `${item.meterCode} 子表集合`;
      setForm.childMeterIds = [];
    }
  } finally {
    setMetersLoading.value = false;
  }
}

async function submitSet() {
  if (!editingSetMain.value) return;
  const payload = {
    setName: setForm.setName.trim(),
    mainMeterId: editingSetMain.value.id,
    childMeterIds: setForm.childMeterIds,
  };
  if (!payload.setName) {
    alert('集合名称不能为空');
    return;
  }
  if (currentSet.value) {
    await updateMeterSet(currentSet.value.id, payload);
  } else {
    const id = await createMeterSet(payload);
    currentSet.value = {
      id,
      ...payload,
      createdAt: undefined,
      updatedAt: undefined,
    };
  }
  alert('子表配置已保存');
}

function cancelSet() {
  editingSetMain.value = null;
}

onMounted(() => {
  reload();
});
</script>

<style scoped>
.meter-page {
  margin-top: 24px;
}

.meter-toolbar {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.meter-search {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  flex: 1;
  max-width: 320px;
  padding: 8px 10px;
  border-radius: 6px;
  border: 1px solid var(--color-border);
}

.kind-switch {
  display: inline-flex;
  padding: 2px;
  border-radius: 999px;
  background-color: #f3f4f6;
}

.tab-button {
  border: none;
  background: none;
  padding: 6px 14px;
  font-size: 13px;
  border-radius: 999px;
  cursor: pointer;
  color: #4b5563;
}

.tab-button.active {
  background-color: #fff;
  color: var(--color-primary);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.meter-content {
  display: grid;
  grid-template-columns: 2fr 1.1fr;
  gap: 20px;
}

.meter-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.meter-table th,
.meter-table td {
  padding: 8px 10px;
  border-bottom: 1px solid var(--color-border);
  text-align: left;
}

.meter-table th {
  background-color: #fafafa;
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

.meter-form-wrapper {
  border-left: 1px solid var(--color-border);
  padding-left: 20px;
}

.form-title {
  margin-top: 0;
  margin-bottom: 12px;
}

.meter-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
}

.form-item input {
  padding: 8px 10px;
  border-radius: 6px;
  border: 1px solid var(--color-border);
}

.checkbox-item label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.form-actions {
  display: flex;
  gap: 8px;
  margin-top: 4px;
}

.secondary-button {
  padding: 8px 14px;
  border-radius: 6px;
  border: 1px solid var(--color-border);
  background-color: #fff;
  cursor: pointer;
}

.usage-section {
  margin-top: 24px;
}

.usage-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.usage-meter-info {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.usage-filter {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.usage-filter input {
  width: 100px;
  padding: 6px 8px;
  border-radius: 6px;
  border: 1px solid var(--color-border);
}

.usage-content {
  display: grid;
  grid-template-columns: 2fr 1.1fr;
  gap: 20px;
}

.usage-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.usage-table th,
.usage-table td {
  padding: 8px 10px;
  border-bottom: 1px solid var(--color-border);
  text-align: left;
}

.usage-table th {
  background-color: #fafafa;
}

.usage-form-wrapper {
  border-left: 1px solid var(--color-border);
  padding-left: 20px;
}

.usage-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-item-inline {
  display: flex;
  gap: 12px;
}

@media (max-width: 960px) {
  .meter-content,
  .usage-content {
    grid-template-columns: 1fr;
  }

  .meter-form-wrapper,
  .usage-form-wrapper {
    border-left: none;
    border-top: 1px solid var(--color-border);
    padding-left: 0;
    padding-top: 16px;
    margin-top: 16px;
  }
}
</style>

