<template>
  <div class="house-page card">
    <div class="section-title">房屋管理</div>

    <div class="house-toolbar">
      <input
        v-model="keyword"
        type="text"
        class="search-input"
        placeholder="按编号 / 名称 / 地址搜索"
        @keyup.enter="reload"
      />
      <button class="primary-button" @click="startCreate">新增房屋</button>
    </div>

    <div class="house-content">
      <div class="house-table-wrapper">
        <table class="house-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>房屋编号</th>
              <th>名称</th>
              <th>地址</th>
              <th>月租金</th>
              <th>押金</th>
              <th>当前租客</th>
              <th>电表数</th>
              <th>水表数</th>
              <th>本月电量</th>
              <th>本月水量</th>
              <th>入住日期</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="12" class="table-empty">加载中...</td>
            </tr>
            <tr v-else-if="houses.length === 0">
              <td colspan="12" class="table-empty">暂无房屋数据</td>
            </tr>
            <tr v-else v-for="item in houses" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.houseCode }}</td>
              <td>{{ item.houseName || '-' }}</td>
              <td>{{ item.locationText || '-' }}</td>
              <td>{{ formatAmount(item.rentAmount) }}</td>
              <td>{{ formatAmount(item.depositAmount) }}</td>
              <td>{{ renderTenant(item.currentTenantId) }}</td>
              <td>{{ electricCount(item) }}</td>
              <td>{{ waterCount(item) }}</td>
              <td>{{ renderUsage(item.electricUsageThisMonth) }}</td>
              <td>{{ renderUsage(item.waterUsageThisMonth) }}</td>
              <td>{{ item.checkInDate || '-' }}</td>
              <td>{{ item.createdAt || '-' }}</td>
              <td>
                <button class="link-button" @click="startEdit(item)">编辑</button>
                <button class="link-button danger" @click="deleteHouseItem(item)">
                  删除
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <div class="pagination" v-if="total > 0">
          <span>共 {{ total }} 条</span>
          <button :disabled="pageNo <= 1" @click="changePage(pageNo - 1)">
            上一页
          </button>
          <span>第 {{ pageNo }} / {{ totalPages }} 页</span>
          <button :disabled="pageNo >= totalPages" @click="changePage(pageNo + 1)">
            下一页
          </button>
        </div>
      </div>

      <div class="house-form-wrapper">
        <h3 class="form-title">{{ editingId ? '编辑房屋' : '新增房屋' }}</h3>
        <form class="house-form" @submit.prevent="submit">
          <div class="form-item">
            <span>房屋编号</span>
            <input
              v-model="form.houseCode"
              type="text"
              maxlength="64"
              required
              :readonly="!!editingId"
            />
          </div>

          <div class="form-item">
            <span>名称</span>
            <input v-model="form.houseName" type="text" maxlength="128" />
          </div>

          <div class="form-item">
            <span>地址</span>
            <input v-model="form.locationText" type="text" maxlength="255" />
          </div>

          <div class="form-item-row">
            <div class="form-item">
              <span>月租金</span>
              <input
                v-model.number="form.rentAmount"
                type="number"
                step="0.01"
                min="0"
                required
              />
            </div>
            <div class="form-item">
              <span>押金</span>
              <input
                v-model.number="form.depositAmount"
                type="number"
                step="0.01"
                min="0"
                required
              />
            </div>
          </div>

          <div class="form-item">
            <span>入住日期</span>
            <input v-model="form.checkInDate" type="date" />
          </div>

          <div class="form-item">
            <span>当前租客</span>
            <select v-model.number="form.currentTenantId">
              <option :value="undefined">未选择</option>
              <option v-for="t in tenants" :key="t.id" :value="t.id">
                {{ t.fullName }}（{{ t.mobileNumber }}）
              </option>
            </select>
          </div>

          <div class="form-item">
            <span>电表（多选）</span>
            <div class="picker-row">
              <div class="picker-summary">
                <span class="picker-count">已选 {{ form.electricMeterIds.length }} 个</span>
                <div v-if="form.electricMeterIds.length" class="tag-list">
                  <span
                    v-for="id in form.electricMeterIds.slice(0, 3)"
                    :key="id"
                    class="tag"
                  >
                    {{ renderMeterSimpleLabel(id, 'ELECTRIC') }}
                  </span>
                  <span
                    v-if="form.electricMeterIds.length > 3"
                    class="tag more-tag"
                  >
                    +{{ form.electricMeterIds.length - 3 }}
                  </span>
                </div>
              </div>
              <button type="button" class="secondary-button" @click="openElectricDialog">
                选择电表
              </button>
            </div>
          </div>

          <div class="form-item">
            <span>水表（多选）</span>
            <div class="picker-row">
              <div class="picker-summary">
                <span class="picker-count">已选 {{ form.waterMeterIds.length }} 个</span>
                <div v-if="form.waterMeterIds.length" class="tag-list">
                  <span
                    v-for="id in form.waterMeterIds.slice(0, 3)"
                    :key="id"
                    class="tag"
                  >
                    {{ renderMeterSimpleLabel(id, 'WATER') }}
                  </span>
                  <span
                    v-if="form.waterMeterIds.length > 3"
                    class="tag more-tag"
                  >
                    +{{ form.waterMeterIds.length - 3 }}
                  </span>
                </div>
              </div>
              <button type="button" class="secondary-button" @click="openWaterDialog">
                选择水表
              </button>
            </div>
          </div>

          <div class="form-actions">
            <button type="submit" class="primary-button">保存</button>
            <button type="button" class="secondary-button" @click="resetForm">
              重置
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 选择电表弹窗 -->
    <div v-if="showElectricDialog" class="modal-mask">
      <div class="modal-container">
        <h3 class="modal-title">选择电表</h3>
        <input
          v-model="electricSearch"
          type="text"
          class="modal-search"
          placeholder="按编号或位置搜索"
        />
        <div class="modal-list">
          <label
            v-for="m in filteredElectricMeters"
            :key="m.id"
            class="modal-item"
          >
            <input
              v-model="tempElectricIds"
              type="checkbox"
              :value="m.id"
            />
            <span>
              {{ m.meterCode }}（{{ m.installLocation || '-' }}）
            </span>
          </label>
          <div v-if="!filteredElectricMeters.length" class="modal-empty">
            无匹配电表
          </div>
        </div>
        <div class="modal-footer">
          <span>已选 {{ tempElectricIds.length }} 个</span>
          <div class="modal-actions">
            <button type="button" class="secondary-button" @click="clearElectric">
              清空
            </button>
            <button type="button" class="secondary-button" @click="closeElectricDialog">
              取消
            </button>
            <button type="button" class="primary-button" @click="confirmElectric">
              确定
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 选择水表弹窗 -->
    <div v-if="showWaterDialog" class="modal-mask">
      <div class="modal-container">
        <h3 class="modal-title">选择水表</h3>
        <input
          v-model="waterSearch"
          type="text"
          class="modal-search"
          placeholder="按编号或位置搜索"
        />
        <div class="modal-list">
          <label
            v-for="m in filteredWaterMeters"
            :key="m.id"
            class="modal-item"
          >
            <input
              v-model="tempWaterIds"
              type="checkbox"
              :value="m.id"
            />
            <span>
              {{ m.meterCode }}（{{ m.installLocation || '-' }}）
            </span>
          </label>
          <div v-if="!filteredWaterMeters.length" class="modal-empty">
            无匹配水表
          </div>
        </div>
        <div class="modal-footer">
          <span>已选 {{ tempWaterIds.length }} 个</span>
          <div class="modal-actions">
            <button type="button" class="secondary-button" @click="clearWater">
              清空
            </button>
            <button type="button" class="secondary-button" @click="closeWaterDialog">
              取消
            </button>
            <button type="button" class="primary-button" @click="confirmWater">
              确定
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import type {
  HouseDetail,
  HousePayload,
  HousePageResult,
  MeterDetail,
  TenantDetail,
} from '@/api';
import {
  pageHouses,
  createHouse,
  updateHouse,
  deleteHouse,
  pageMeters,
  pageTenants,
} from '@/api';

const houses = ref<HouseDetail[]>([]);
const loading = ref(false);
const keyword = ref('');
const pageNo = ref(1);
const pageSize = ref(10);
const total = ref(0);

const editingId = ref<number | null>(null);

// 弹窗相关状态（多选电表/水表）
const showElectricDialog = ref(false);
const showWaterDialog = ref(false);
const electricSearch = ref('');
const waterSearch = ref('');
const tempElectricIds = ref<number[]>([]);
const tempWaterIds = ref<number[]>([]);

const form = reactive<{
  houseCode: string;
  houseName?: string;
  locationText?: string;
  rentAmount: number | null;
  depositAmount: number | null;
  checkInDate: string | null;
  currentTenantId?: number;
  electricMeterIds: number[];
  waterMeterIds: number[];
}>({
  houseCode: '',
  houseName: '',
  locationText: '',
  rentAmount: null,
  depositAmount: null,
  checkInDate: null,
  currentTenantId: undefined,
  electricMeterIds: [],
  waterMeterIds: [],
});

const tenants = ref<TenantDetail[]>([]);
const electricMeters = ref<MeterDetail[]>([]);
const waterMeters = ref<MeterDetail[]>([]);

const totalPages = computed(() => {
  return total.value === 0 ? 1 : Math.ceil(total.value / pageSize.value);
});

const filteredElectricMeters = computed(() => {
  const kw = electricSearch.value.trim().toLowerCase();
  if (!kw) return electricMeters.value;
  return electricMeters.value.filter((m) => {
    const text = `${m.meterCode || ''} ${m.installLocation || ''}`.toLowerCase();
    return text.includes(kw);
  });
});

const filteredWaterMeters = computed(() => {
  const kw = waterSearch.value.trim().toLowerCase();
  if (!kw) return waterMeters.value;
  return waterMeters.value.filter((m) => {
    const text = `${m.meterCode || ''} ${m.installLocation || ''}`.toLowerCase();
    return text.includes(kw);
  });
});

async function reload() {
  loading.value = true;
  try {
    const res: HousePageResult = await pageHouses({
      keyword: keyword.value || undefined,
      pageNo: pageNo.value,
      pageSize: pageSize.value,
    });
    houses.value = res.records || [];
    total.value = res.total || 0;
  } finally {
    loading.value = false;
  }
}

function changePage(next: number) {
  if (next < 1) return;
  pageNo.value = next;
  reload();
}

function startCreate() {
  editingId.value = null;
  resetForm();
}

function startEdit(item: HouseDetail) {
  editingId.value = item.id;
  form.houseCode = item.houseCode;
  form.houseName = item.houseName || '';
  form.locationText = item.locationText || '';
  form.rentAmount = item.rentAmount ?? null;
  form.depositAmount = item.depositAmount ?? null;
  form.checkInDate = item.checkInDate || null;
  form.currentTenantId = item.currentTenantId;
  const ids = item.meterIds || [];
  form.electricMeterIds = ids.filter((id) =>
    electricMeters.value.some((m) => m.id === id),
  );
  form.waterMeterIds = ids.filter((id) =>
    waterMeters.value.some((m) => m.id === id),
  );
}

function resetForm() {
  form.houseCode = '';
  form.houseName = '';
  form.locationText = '';
  form.rentAmount = null;
  form.depositAmount = null;
  form.checkInDate = null;
  form.currentTenantId = undefined;
  form.electricMeterIds = [];
  form.waterMeterIds = [];
}

async function submit() {
  if (!form.houseCode.trim()) {
    alert('房屋编号不能为空');
    return;
  }
  if (form.rentAmount == null || Number.isNaN(form.rentAmount)) {
    alert('月租金不能为空');
    return;
  }
  if (form.depositAmount == null || Number.isNaN(form.depositAmount)) {
    alert('押金不能为空');
    return;
  }

  const meterIds = [...form.electricMeterIds, ...form.waterMeterIds];
  const payload: HousePayload = {
    houseCode: form.houseCode.trim(),
    houseName: form.houseName?.trim() || undefined,
    locationText: form.locationText?.trim() || undefined,
    rentAmount: Number(form.rentAmount),
    depositAmount: Number(form.depositAmount),
    checkInDate: form.checkInDate || null,
    currentTenantId: form.currentTenantId ?? null,
    meterIds,
  };

  if (editingId.value) {
    await updateHouse(editingId.value, payload);
  } else {
    await createHouse(payload);
  }
  await reload();
}

async function deleteHouseItem(item: HouseDetail) {
  if (!confirm(`确认删除房屋【${item.houseCode}】？`)) {
    return;
  }
  await deleteHouse(item.id);
  await reload();
}

function renderTenant(tenantId: number | undefined) {
  if (!tenantId) return '-';
  const found = tenants.value.find((t) => t.id === tenantId);
  if (!found) return tenantId;
  return `${found.fullName}（${found.mobileNumber}）`;
}

function renderMeter(meterId: number | undefined, kind: 'ELECTRIC' | 'WATER') {
  if (!meterId) return '-';
  const list = kind === 'ELECTRIC' ? electricMeters.value : waterMeters.value;
  const found = list.find((m) => m.id === meterId);
  if (!found) return meterId;
  return `${found.meterCode}${found.installLocation ? '（' + found.installLocation + '）' : ''}`;
}

function renderMeterSimpleLabel(id: number, kind: 'ELECTRIC' | 'WATER') {
  const list = kind === 'ELECTRIC' ? electricMeters.value : waterMeters.value;
  const found = list.find((m) => m.id === id);
  if (!found) return id;
  return found.meterCode;
}

function electricCount(item: HouseDetail): number {
  const ids = item.meterIds || [];
  return ids.filter((id) =>
    electricMeters.value.some((m) => m.id === id),
  ).length;
}

function waterCount(item: HouseDetail): number {
  const ids = item.meterIds || [];
  return ids.filter((id) =>
    waterMeters.value.some((m) => m.id === id),
  ).length;
}

function openElectricDialog() {
  tempElectricIds.value = [...form.electricMeterIds];
  electricSearch.value = '';
  showElectricDialog.value = true;
}

function closeElectricDialog() {
  showElectricDialog.value = false;
}

function clearElectric() {
  tempElectricIds.value = [];
}

function confirmElectric() {
  form.electricMeterIds = [...tempElectricIds.value];
  showElectricDialog.value = false;
}

function openWaterDialog() {
  tempWaterIds.value = [...form.waterMeterIds];
  waterSearch.value = '';
  showWaterDialog.value = true;
}

function closeWaterDialog() {
  showWaterDialog.value = false;
}

function clearWater() {
  tempWaterIds.value = [];
}

function confirmWater() {
  form.waterMeterIds = [...tempWaterIds.value];
  showWaterDialog.value = false;
}

function formatAmount(value: number | undefined) {
  if (value == null) return '-';
  return Number(value).toFixed(2);
}

function renderUsage(value: number | undefined): string {
  if (value === null || value === undefined) {
    return '-';
  }
  return Number(value).toFixed(2);
}

async function loadOptions() {
  const [tenantRes, meterRes] = await Promise.all([
    pageTenants({ pageNo: 1, pageSize: 500 }),
    pageMeters({ pageNo: 1, pageSize: 500 }),
  ]);
  tenants.value = tenantRes.records || [];
  const allMeters = meterRes.records || [];
  electricMeters.value = allMeters.filter((m) => m.meterKind === 'ELECTRIC');
  waterMeters.value = allMeters.filter((m) => m.meterKind === 'WATER');
}

onMounted(() => {
  loadOptions();
  reload();
});
</script>

<style scoped>
.house-page {
  margin-top: 24px;
}

.house-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.search-input {
  flex: 1;
  max-width: 320px;
  padding: 8px 10px;
  border-radius: 6px;
  border: 1px solid var(--color-border);
}

.house-content {
  display: grid;
  grid-template-columns: 2fr 1.1fr;
  gap: 20px;
}

.house-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.house-table th,
.house-table td {
  padding: 8px 10px;
  border-bottom: 1px solid var(--color-border);
  text-align: left;
}

.house-table th {
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

.house-form-wrapper {
  border-left: 1px solid var(--color-border);
  padding-left: 20px;
}

.form-title {
  margin-top: 0;
  margin-bottom: 12px;
}

.house-form {
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

.form-item-row {
  display: flex;
  gap: 12px;
}

.form-item input,
.form-item select {
  padding: 8px 10px;
  border-radius: 6px;
  border: 1px solid var(--color-border);
}

.form-actions {
  display: flex;
  gap: 8px;
  margin-top: 4px;
}

.picker-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.picker-summary {
  flex: 1;
  min-width: 0;
}

.picker-count {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-top: 4px;
}

.tag {
  padding: 2px 6px;
  border-radius: 10px;
  background-color: #f3f3f3;
  font-size: 12px;
}

.more-tag {
  background-color: #e5f3ff;
  color: #1677ff;
}

.modal-mask {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-container {
  width: 480px;
  max-width: 90vw;
  max-height: 80vh;
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.18);
  display: flex;
  flex-direction: column;
}

.modal-title {
  margin: 0 0 8px;
  font-size: 16px;
}

.modal-search {
  padding: 6px 8px;
  border-radius: 6px;
  border: 1px solid var(--color-border);
  font-size: 13px;
  margin-bottom: 8px;
}

.modal-list {
  flex: 1;
  overflow: auto;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  padding: 8px;
  margin-bottom: 8px;
}

.modal-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  padding: 4px 2px;
}

.modal-empty {
  text-align: center;
  font-size: 12px;
  color: var(--color-text-secondary);
  padding: 8px 0;
}

.modal-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  font-size: 12px;
}

.modal-actions {
  display: flex;
  gap: 8px;
}

@media (max-width: 960px) {
  .house-content {
    grid-template-columns: 1fr;
  }

  .house-form-wrapper {
    border-left: none;
    border-top: 1px solid var(--color-border);
    padding-left: 0;
    padding-top: 16px;
    margin-top: 16px;
  }
}
</style>

