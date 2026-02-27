<template>
  <div class="shared-page card">
    <div class="section-title">公共场所管理</div>

    <div class="shared-toolbar">
      <input
        v-model="keyword"
        type="text"
        class="search-input"
        placeholder="按名称搜索公共场所"
        @keyup.enter="reload"
      />
      <button class="primary-button" @click="startCreate">新增公共场所</button>
    </div>

    <div class="shared-content">
      <div class="shared-table-wrapper">
        <table class="shared-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>名称</th>
              <th>电表数</th>
              <th>水表数</th>
              <th>租客数</th>
              <th>本月电量</th>
              <th>本月水量</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="7" class="table-empty">加载中...</td>
            </tr>
            <tr v-else-if="places.length === 0">
              <td colspan="7" class="table-empty">暂无公共场所</td>
            </tr>
            <tr v-else v-for="item in places" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.placeName }}</td>
              <td>{{ electricCount(item) }}</td>
              <td>{{ waterCount(item) }}</td>
              <td>{{ item.tenantIds?.length ?? 0 }}</td>
              <td>{{ renderUsage(item.electricUsageThisMonth) }}</td>
              <td>{{ renderUsage(item.waterUsageThisMonth) }}</td>
              <td>{{ item.createdAt || '-' }}</td>
              <td>
                <button class="link-button" @click="startEdit(item)">编辑</button>
                <button class="link-button" @click="openUsageDialog(item)">用量</button>
                <button class="link-button" @click="openShareDialog(item)">分摊</button>
                <button class="link-button danger" @click="deletePlace(item)">
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

      <div class="shared-form-wrapper">
        <h3 class="form-title">{{ editingId ? '编辑公共场所' : '新增公共场所' }}</h3>
        <form class="shared-form" @submit.prevent="submit">
          <div class="form-item">
            <span>名称</span>
            <input v-model="form.placeName" type="text" maxlength="128" required />
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

          <div class="form-item">
            <span>关联租客（多选）</span>
            <div class="picker-row">
              <div class="picker-summary">
                <span class="picker-count">已选 {{ form.tenantIds.length }} 个</span>
                <div v-if="form.tenantIds.length" class="tag-list">
                  <span
                    v-for="id in form.tenantIds.slice(0, 3)"
                    :key="id"
                    class="tag"
                  >
                    {{ renderTenantSimpleLabel(id) }}
                  </span>
                  <span
                    v-if="form.tenantIds.length > 3"
                    class="tag more-tag"
                  >
                    +{{ form.tenantIds.length - 3 }}
                  </span>
                </div>
              </div>
              <button type="button" class="secondary-button" @click="openTenantDialog">
                选择租客
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

    <!-- 选择租客弹窗 -->
    <div v-if="showTenantDialog" class="modal-mask">
      <div class="modal-container">
        <h3 class="modal-title">选择租客</h3>
        <input
          v-model="tenantSearch"
          type="text"
          class="modal-search"
          placeholder="按姓名或手机号搜索"
        />
        <div class="modal-list">
          <label
            v-for="t in filteredTenants"
            :key="t.id"
            class="modal-item"
          >
            <input
              v-model="tempTenantIds"
              type="checkbox"
              :value="t.id"
            />
            <span>
              {{ t.fullName }}（{{ t.mobileNumber }}）
            </span>
          </label>
          <div v-if="!filteredTenants.length" class="modal-empty">
            无匹配租客
          </div>
        </div>
        <div class="modal-footer">
          <span>已选 {{ tempTenantIds.length }} 个</span>
          <div class="modal-actions">
            <button type="button" class="secondary-button" @click="clearTenant">
              清空
            </button>
            <button type="button" class="secondary-button" @click="closeTenantDialog">
              取消
            </button>
            <button type="button" class="primary-button" @click="confirmTenant">
              确定
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 公共场所-水电用量历史 -->
    <div v-if="showUsageDialog" class="modal-mask">
      <div class="modal-container large">
        <h3 class="modal-title">
          公共场所水电用量 - {{ usagePlace?.placeName || '' }}
        </h3>
        <div class="usage-dialog">
          <div class="usage-left">
            <div class="modal-subtitle">关联计量表</div>
            <ul class="meter-list">
              <li
                v-for="m in usageMeters"
                :key="m.id"
                :class="['meter-item', { active: selectedUsageMeter && selectedUsageMeter.id === m.id }]"
                @click="selectUsageMeter(m)"
              >
                <div class="meter-line">
                  <span class="badge" :class="m.meterKind === 'ELECTRIC' ? 'badge-electric' : 'badge-water'">
                    {{ m.meterKind === 'ELECTRIC' ? '电' : '水' }}
                  </span>
                  <span class="meter-code">{{ m.meterCode }}</span>
                </div>
                <div class="meter-desc">
                  {{ m.installLocation || '-' }}
                </div>
              </li>
              <li v-if="!usageMeters.length" class="meter-item empty">
                暂无已关联的水电表
              </li>
            </ul>
          </div>
          <div class="usage-right">
            <div class="usage-header">
              <div>
                <div class="modal-subtitle">
                  {{ selectedUsageMeter ? (selectedUsageMeter.meterKind === 'ELECTRIC' ? '电表' : '水表') + '月度用量' : '请选择左侧计量表' }}
                </div>
                <div v-if="selectedUsageMeter" class="usage-meter-info">
                  编号：{{ selectedUsageMeter.meterCode }}，
                  位置：{{ selectedUsageMeter.installLocation || '-' }}
                </div>
              </div>
              <div class="usage-filter" v-if="selectedUsageMeter">
                <span>年份：</span>
                <input v-model.number="usageYear" type="number" min="2000" max="2100" />
                <button class="secondary-button" @click="reloadUsages">查询</button>
              </div>
            </div>

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
                  <tr v-else-if="!selectedUsageMeter">
                    <td colspan="8" class="table-empty">请先在左侧选择一个计量表</td>
                  </tr>
                  <tr v-else-if="usages.length === 0">
                    <td colspan="8" class="table-empty">暂无用量记录</td>
                  </tr>
                  <tr v-else v-for="u in usages" :key="u.id">
                    <td>{{ u.usageYear }}</td>
                    <td>{{ u.usageMonth }}</td>
                    <td>{{ u.previousReading }}</td>
                    <td>{{ u.currentReading }}</td>
                    <td>{{ u.usageValue }}</td>
                    <td>{{ u.unitPrice ?? '-' }}</td>
                    <td>{{ u.totalAmount ?? '-' }}</td>
                    <td>{{ u.updatedAt || u.createdAt || '-' }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <div />
          <div class="modal-actions">
            <button type="button" class="secondary-button" @click="closeUsageDialog">
              关闭
            </button>
          </div>
        </div>
      </div>
    </div>
    <!-- 公共场所-水电分摊 -->
    <div v-if="showShareDialog" class="modal-mask">
      <div class="modal-container large">
        <h3 class="modal-title">
          公共场所水电分摊 - {{ sharePlace?.placeName || '' }}
        </h3>
        <div class="usage-header">
          <div>
            <div class="modal-subtitle">选择账期</div>
            <div>
              年份：
              <input v-model.number="shareYear" type="number" min="2000" max="2100" style="width: 90px" />
              月份：
              <input v-model.number="shareMonth" type="number" min="1" max="12" style="width: 60px" />
              <button class="secondary-button" @click="reloadShare">加载分摊</button>
            </div>
          </div>
          <div v-if="shareLoaded" class="usage-meter-info">
            公共场所当月总电量：{{ renderUsage(shareTotalElectric) }}，
            公共场所当月总水量：{{ renderUsage(shareTotalWater) }}
          </div>
        </div>
        <div class="usage-table-wrapper" v-if="shareLoaded">
          <table class="usage-table">
            <thead>
              <tr>
                <th>租客</th>
                <th>电量分摊</th>
                <th>水量分摊</th>
                <th>电费估算</th>
                <th>水费估算</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="!shareItems.length">
                <td colspan="5" class="table-empty">请先在“关联租客”中配置租客</td>
              </tr>
              <tr v-else v-for="item in shareItems" :key="item.tenantId">
                <td>{{ renderTenantSimpleLabel(item.tenantId) }}</td>
                <td>
                  <input
                    v-model.number="item.electricUsage"
                    type="number"
                    min="0"
                    step="0.01"
                    class="inline-input"
                  />
                </td>
                <td>
                  <input
                    v-model.number="item.waterUsage"
                    type="number"
                    min="0"
                    step="0.01"
                    class="inline-input"
                  />
                </td>
                <td>{{ item.electricAmount ?? '-' }}</td>
                <td>{{ item.waterAmount ?? '-' }}</td>
              </tr>
              <tr v-if="shareItems.length">
                <td>合计</td>
                <td :class="{ 'text-error': shareSumElectric > (shareTotalElectric ?? 0) }">
                  {{ shareSumElectric }}
                </td>
                <td :class="{ 'text-error': shareSumWater > (shareTotalWater ?? 0) }">
                  {{ shareSumWater }}
                </td>
                <td colspan="2">
                  <span v-if="shareTotalElectric != null || shareTotalWater != null">
                    （总电量 {{ renderUsage(shareTotalElectric) }}，总水量 {{ renderUsage(shareTotalWater) }}）
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="modal-footer">
          <div class="modal-actions">
            <button type="button" class="secondary-button" @click="closeShareDialog">
              取消
            </button>
            <button type="button" class="primary-button" @click="saveShare" :disabled="!shareLoaded">
              保存分摊
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
  SharedPlaceDetail,
  SharedPlacePayload,
  SharedPlacePageResult,
  MeterDetail,
  TenantDetail,
  MeterUsageDTO,
  SharedPlaceShareDetail,
} from '@/api';
import {
  pageSharedPlaces,
  createSharedPlace,
  updateSharedPlace,
  deleteSharedPlace,
  pageMeters,
  pageTenants,
  pageMeterUsages,
  getSharedPlaceShare,
  saveSharedPlaceShare,
} from '@/api';

const places = ref<SharedPlaceDetail[]>([]);
const loading = ref(false);
const keyword = ref('');
const pageNo = ref(1);
const pageSize = ref(10);
const total = ref(0);

const editingId = ref<number | null>(null);

// 弹窗相关状态
const showElectricDialog = ref(false);
const showWaterDialog = ref(false);
const showTenantDialog = ref(false);

const electricSearch = ref('');
const waterSearch = ref('');
const tenantSearch = ref('');

const tempElectricIds = ref<number[]>([]);
const tempWaterIds = ref<number[]>([]);
const tempTenantIds = ref<number[]>([]);

// 用量历史弹窗相关
const showUsageDialog = ref(false);
const usagePlace = ref<SharedPlaceDetail | null>(null);
const usageMeters = ref<MeterDetail[]>([]);
const selectedUsageMeter = ref<MeterDetail | null>(null);
const usages = ref<MeterUsageDTO[]>([]);
const usageLoading = ref(false);
const usageYear = ref<number>(new Date().getFullYear());

// 分摊弹窗相关
const showShareDialog = ref(false);
const sharePlace = ref<SharedPlaceDetail | null>(null);
const shareYear = ref<number>(new Date().getFullYear());
const shareMonth = ref<number>(new Date().getMonth() + 1);
const shareLoaded = ref(false);
const shareItems = ref<
  {
    tenantId: number;
    electricUsage: number | null;
    waterUsage: number | null;
    electricAmount?: number | null;
    waterAmount?: number | null;
  }[]
>([]);
const shareTotalElectric = ref<number | null>(null);
const shareTotalWater = ref<number | null>(null);

const form = reactive<{
  placeName: string;
  electricMeterIds: number[];
  waterMeterIds: number[];
  tenantIds: number[];
}>({
  placeName: '',
  electricMeterIds: [],
  waterMeterIds: [],
  tenantIds: [],
});

const electricMeters = ref<MeterDetail[]>([]);
const waterMeters = ref<MeterDetail[]>([]);
const tenants = ref<TenantDetail[]>([]);

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

const filteredTenants = computed(() => {
  const kw = tenantSearch.value.trim().toLowerCase();
  if (!kw) return tenants.value;
  return tenants.value.filter((t) => {
    const text = `${t.fullName || ''} ${t.mobileNumber || ''}`.toLowerCase();
    return text.includes(kw);
  });
});

const shareSumElectric = computed(() =>
  shareItems.value.reduce((sum, item) => sum + (item.electricUsage || 0), 0),
);
const shareSumWater = computed(() =>
  shareItems.value.reduce((sum, item) => sum + (item.waterUsage || 0), 0),
);

async function reload() {
  loading.value = true;
  try {
    const res: SharedPlacePageResult = await pageSharedPlaces({
      keyword: keyword.value || undefined,
      pageNo: pageNo.value,
      pageSize: pageSize.value,
    });
    places.value = res.records || [];
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

function startEdit(item: SharedPlaceDetail) {
  editingId.value = item.id;
  form.placeName = item.placeName;
  form.electricMeterIds = (item.meterIds || []).filter((id) =>
    electricMeters.value.some((m) => m.id === id)
  );
  form.waterMeterIds = (item.meterIds || []).filter((id) =>
    waterMeters.value.some((m) => m.id === id)
  );
  form.tenantIds = [...(item.tenantIds || [])];
}

function resetForm() {
  form.placeName = '';
  form.electricMeterIds = [];
  form.waterMeterIds = [];
  form.tenantIds = [];
}

function openShareDialog(item: SharedPlaceDetail) {
  sharePlace.value = item;
  shareYear.value = new Date().getFullYear();
  shareMonth.value = new Date().getMonth() + 1;
  shareLoaded.value = false;
  shareItems.value = [];
  shareTotalElectric.value = null;
  shareTotalWater.value = null;
  showShareDialog.value = true;
  reloadShare();
}

async function reloadShare() {
  if (!sharePlace.value) return;
  if (!shareYear.value || !shareMonth.value) return;
  const detail: SharedPlaceShareDetail = await getSharedPlaceShare(
    sharePlace.value.id,
    shareYear.value,
    shareMonth.value,
  );
  shareTotalElectric.value = detail.totalElectricUsage ?? null;
  shareTotalWater.value = detail.totalWaterUsage ?? null;
  shareItems.value =
    detail.items?.map((it) => ({
      tenantId: it.tenantId,
      electricUsage: it.electricUsage ?? null,
      waterUsage: it.waterUsage ?? null,
      electricAmount: it.electricAmount ?? null,
      waterAmount: it.waterAmount ?? null,
    })) ?? [];
  shareLoaded.value = true;
}

function closeShareDialog() {
  showShareDialog.value = false;
}

async function saveShare() {
  if (!sharePlace.value) return;
  if (!shareYear.value || !shareMonth.value) {
    alert('请先填写年份和月份');
    return;
  }
  if (shareTotalElectric.value != null && shareSumElectric.value > shareTotalElectric.value + 1e-6) {
    alert('电量分摊合计不能超过公共场所总电量');
    return;
  }
  if (shareTotalWater.value != null && shareSumWater.value > shareTotalWater.value + 1e-6) {
    alert('水量分摊合计不能超过公共场所总水量');
    return;
  }
  await saveSharedPlaceShare(sharePlace.value.id, {
    placeId: sharePlace.value.id,
    usageYear: shareYear.value,
    usageMonth: shareMonth.value,
    items: shareItems.value.map((it) => ({
      tenantId: it.tenantId,
      electricUsage: it.electricUsage ?? 0,
      waterUsage: it.waterUsage ?? 0,
    })),
  });
  alert('保存成功');
  showShareDialog.value = false;
}

async function submit() {
  if (!form.placeName.trim()) {
    alert('名称不能为空');
    return;
  }
  const meterIds = [...form.electricMeterIds, ...form.waterMeterIds];
  const payload: SharedPlacePayload = {
    placeName: form.placeName.trim(),
    meterIds,
    tenantIds: form.tenantIds,
  };
  if (editingId.value) {
    await updateSharedPlace(editingId.value, payload);
  } else {
    await createSharedPlace(payload);
  }
  await reload();
}

async function deletePlace(item: SharedPlaceDetail) {
  if (!confirm(`确认删除公共场所【${item.placeName}】？`)) {
    return;
  }
  await deleteSharedPlace(item.id);
  await reload();
}

function electricCount(item: SharedPlaceDetail): number {
  const ids = item.meterIds || [];
  return ids.filter((id) => electricMeters.value.some((m) => m.id === id)).length;
}

function waterCount(item: SharedPlaceDetail): number {
  const ids = item.meterIds || [];
  return ids.filter((id) => waterMeters.value.some((m) => m.id === id)).length;
}

function renderUsage(value: number | undefined): string {
  if (value === null || value === undefined) {
    return '-';
  }
  // 保留两位小数，避免显示过长
  return Number(value).toFixed(2);
}

function openUsageDialog(place: SharedPlaceDetail) {
  usagePlace.value = place;
  // 根据 place.meterIds 从当前缓存的电表/水表中找出详情
  const ids = place.meterIds || [];
  const allMeters = [...electricMeters.value, ...waterMeters.value];
  const related = allMeters.filter((m) => ids.includes(m.id));
  usageMeters.value = related;
  if (related.length > 0) {
    selectedUsageMeter.value = related[0];
    usageYear.value = new Date().getFullYear();
    reloadUsages();
  } else {
    selectedUsageMeter.value = null;
    usages.value = [];
  }
  showUsageDialog.value = true;
}

function closeUsageDialog() {
  showUsageDialog.value = false;
  usagePlace.value = null;
  usageMeters.value = [];
  selectedUsageMeter.value = null;
  usages.value = [];
}

function selectUsageMeter(m: MeterDetail) {
  if (!m) return;
  selectedUsageMeter.value = m;
  reloadUsages();
}

async function reloadUsages() {
  if (!selectedUsageMeter.value) return;
  usageLoading.value = true;
  try {
    const res = await pageMeterUsages({
      meterId: selectedUsageMeter.value.id,
      usageYear: usageYear.value || undefined,
      pageNo: 1,
      pageSize: 12,
    });
    usages.value = res.records || [];
  } finally {
    usageLoading.value = false;
  }
}

function renderMeterSimpleLabel(id: number, kind: 'ELECTRIC' | 'WATER') {
  const list = kind === 'ELECTRIC' ? electricMeters.value : waterMeters.value;
  const found = list.find((m) => m.id === id);
  if (!found) return id;
  return found.meterCode;
}

function renderTenantSimpleLabel(id: number) {
  const found = tenants.value.find((t) => t.id === id);
  if (!found) return id;
  return found.fullName;
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

function openTenantDialog() {
  tempTenantIds.value = [...form.tenantIds];
  tenantSearch.value = '';
  showTenantDialog.value = true;
}

function closeTenantDialog() {
  showTenantDialog.value = false;
}

function clearTenant() {
  tempTenantIds.value = [];
}

function confirmTenant() {
  form.tenantIds = [...tempTenantIds.value];
  showTenantDialog.value = false;
}

async function loadMetersAndTenants() {
  // 简单拉一页足够多的数据作为选择项
  const [meterRes, tenantRes] = await Promise.all([
    pageMeters({ pageNo: 1, pageSize: 500 }),
    pageTenants({ pageNo: 1, pageSize: 500 }),
  ]);
  const allMeters = meterRes.records || [];
  electricMeters.value = allMeters.filter((m) => m.meterKind === 'ELECTRIC');
  waterMeters.value = allMeters.filter((m) => m.meterKind === 'WATER');
  tenants.value = tenantRes.records || [];
}

onMounted(() => {
  loadMetersAndTenants();
  reload();
});
</script>

<style scoped>
.shared-page {
  margin-top: 24px;
}

.shared-toolbar {
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

.shared-content {
  display: grid;
  grid-template-columns: 2fr 1.1fr;
  gap: 20px;
}

.shared-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.shared-table th,
.shared-table td {
  padding: 8px 10px;
  border-bottom: 1px solid var(--color-border);
  text-align: left;
}

.shared-table th {
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

.shared-form-wrapper {
  border-left: 1px solid var(--color-border);
  padding-left: 20px;
}

.form-title {
  margin-top: 0;
  margin-bottom: 12px;
}

.shared-form {
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

.form-item input,
.form-item select {
  padding: 8px 10px;
  border-radius: 6px;
  border: 1px solid var(--color-border);
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

.modal-container.large {
  width: 900px;
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

.usage-dialog {
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 16px;
  margin-top: 8px;
}

.usage-left {
  border-right: 1px solid var(--color-border);
  padding-right: 12px;
}

.usage-right {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.modal-subtitle {
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 4px;
}

.meter-list {
  list-style: none;
  padding: 0;
  margin: 0;
  max-height: 360px;
  overflow: auto;
}

.meter-item {
  padding: 6px 8px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
}

.meter-item + .meter-item {
  margin-top: 4px;
}

.meter-item:hover {
  background-color: #f5f5f5;
}

.meter-item.active {
  background-color: #e6f4ff;
}

.meter-item.empty {
  cursor: default;
  color: var(--color-text-secondary);
}

.meter-line {
  display: flex;
  align-items: center;
  gap: 6px;
}

.meter-desc {
  font-size: 12px;
  color: var(--color-text-secondary);
  margin-top: 2px;
}

.badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  border-radius: 999px;
  font-size: 11px;
  color: #fff;
}

.badge-electric {
  background-color: #1677ff;
}

.badge-water {
  background-color: #13c2c2;
}

.meter-code {
  font-weight: 500;
}

.usage-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.usage-meter-info {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.usage-filter input {
  width: 100px;
  padding: 6px 8px;
  border-radius: 6px;
  border: 1px solid var(--color-border);
}

.usage-table-wrapper {
  margin-top: 8px;
  max-height: 360px;
  overflow: auto;
}

.usage-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.usage-table th,
.usage-table td {
  padding: 6px 8px;
  border-bottom: 1px solid var(--color-border);
  text-align: left;
}

.usage-table th {
  background-color: #fafafa;
}

.form-actions {
  display: flex;
  gap: 8px;
  margin-top: 4px;
}

@media (max-width: 960px) {
  .shared-content {
    grid-template-columns: 1fr;
  }

  .shared-form-wrapper {
    border-left: none;
    border-top: 1px solid var(--color-border);
    padding-left: 0;
    padding-top: 16px;
    margin-top: 16px;
  }
}
</style>

