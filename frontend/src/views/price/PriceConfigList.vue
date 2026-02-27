<template>
  <div class="price-page card">
    <div class="section-title">水电单价配置</div>

    <div class="price-toolbar">
      <div class="kind-switch">
        <button
          class="tab-button"
          :class="{ active: currentKind === 'ELECTRIC' }"
          @click="switchKind('ELECTRIC')"
        >
          电费单价
        </button>
        <button
          class="tab-button"
          :class="{ active: currentKind === 'WATER' }"
          @click="switchKind('WATER')"
        >
          水费单价
        </button>
      </div>

      <div class="current-info" v-if="current">
        <span>当前生效：</span>
        <strong>{{ current.unitPrice }} 元/{{ currentLabel }}</strong>
        <span class="date-range">
          （{{ current.effectiveDate }} 起
          <span v-if="current.expiredDate"> 至 {{ current.expiredDate }}</span>
          <span v-else> 至今</span>）
        </span>
      </div>
      <div class="current-info empty" v-else>
        当前没有生效的{{ currentLabel }}单价
      </div>
    </div>

    <div class="price-content">
      <div class="price-table-wrapper">
        <table class="price-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>费用类型</th>
              <th>单价</th>
              <th>生效日期</th>
              <th>失效日期</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="7" class="table-empty">加载中...</td>
            </tr>
            <tr v-else-if="list.length === 0">
              <td colspan="7" class="table-empty">暂无配置记录</td>
            </tr>
            <tr v-else v-for="item in list" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.chargeKind === 'ELECTRIC' ? '电费' : '水费' }}</td>
              <td>{{ item.unitPrice }}</td>
              <td>{{ item.effectiveDate }}</td>
              <td>{{ item.expiredDate || '-' }}</td>
              <td>{{ item.createdAt || '-' }}</td>
              <td>
                <button class="link-button" @click="startEdit(item)">编辑</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="price-form-wrapper">
        <h3 class="form-title">
          {{ editingId ? '编辑单价配置' : '新增单价配置' }}（{{ currentKindLabel }}）
        </h3>
        <form class="price-form" @submit.prevent="submit">
          <div class="form-item">
            <span>单价（元）</span>
            <input
              v-model.number="form.unitPrice"
              type="number"
              min="0"
              step="0.0001"
              required
            />
          </div>
          <div class="form-item">
            <span>生效日期</span>
            <input v-model="form.effectiveDate" type="date" required />
          </div>
          <div class="form-item">
            <span>失效日期（可选）</span>
            <input v-model="form.expiredDate" type="date" />
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
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import type { ChargeKind, PriceConfigDTO, PriceConfigPayload } from '@/api';
import {
  createPriceConfig,
  getCurrentPriceConfig,
  listPriceConfigs,
  updatePriceConfig,
} from '@/api';

const currentKind = ref<ChargeKind>('ELECTRIC');
const list = ref<PriceConfigDTO[]>([]);
const current = ref<PriceConfigDTO | null>(null);
const loading = ref(false);
const editingId = ref<number | null>(null);

const form = reactive({
  unitPrice: 0,
  effectiveDate: '',
  expiredDate: '',
});

const currentLabel = computed(() =>
  currentKind.value === 'ELECTRIC' ? '度' : '吨',
);

const currentKindLabel = computed(() =>
  currentKind.value === 'ELECTRIC' ? '电费' : '水费',
);

async function reload() {
  loading.value = true;
  try {
    const kind = currentKind.value;
    const [listRes, currentRes] = await Promise.allSettled([
      listPriceConfigs(kind),
      getCurrentPriceConfig(kind),
    ]);

    if (listRes.status === 'fulfilled') {
      list.value = listRes.value || [];
    } else {
      list.value = [];
    }

    if (currentRes.status === 'fulfilled') {
      current.value = currentRes.value;
    } else {
      current.value = null;
    }
  } finally {
    loading.value = false;
  }
}

function switchKind(kind: ChargeKind) {
  if (currentKind.value === kind) return;
  currentKind.value = kind;
  resetForm();
  reload();
}

function startEdit(item: PriceConfigDTO) {
  editingId.value = item.id;
  form.unitPrice = item.unitPrice;
  form.effectiveDate = item.effectiveDate;
  form.expiredDate = item.expiredDate || '';
}

function resetForm() {
  editingId.value = null;
  form.unitPrice = 0;
  form.effectiveDate = '';
  form.expiredDate = '';
}

async function submit() {
  if (!form.effectiveDate) {
    alert('生效日期不能为空');
    return;
  }
  if (!form.unitPrice || form.unitPrice <= 0) {
    alert('单价必须大于0');
    return;
  }

  const payload: PriceConfigPayload = {
    chargeKind: currentKind.value,
    unitPrice: form.unitPrice,
    effectiveDate: form.effectiveDate,
    expiredDate: form.expiredDate || undefined,
  };

  if (editingId.value) {
    await updatePriceConfig(editingId.value, payload);
  } else {
    await createPriceConfig(payload);
  }

  await reload();
}

onMounted(() => {
  reload();
});
</script>

<style scoped>
.price-page {
  margin-top: 24px;
}

.price-toolbar {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
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

.current-info {
  font-size: 13px;
}

.current-info.empty {
  color: var(--color-text-secondary);
}

.date-range {
  margin-left: 4px;
}

.price-content {
  display: grid;
  grid-template-columns: 2fr 1.1fr;
  gap: 20px;
}

.price-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.price-table th,
.price-table td {
  padding: 8px 10px;
  border-bottom: 1px solid var(--color-border);
  text-align: left;
}

.price-table th {
  background-color: #fafafa;
}

.table-empty {
  text-align: center;
  color: var(--color-text-secondary);
}

.price-form-wrapper {
  border-left: 1px solid var(--color-border);
  padding-left: 20px;
}

.form-title {
  margin-top: 0;
  margin-bottom: 12px;
}

.price-form {
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

@media (max-width: 960px) {
  .price-content {
    grid-template-columns: 1fr;
  }

  .price-form-wrapper {
    border-left: none;
    border-top: 1px solid var(--color-border);
    padding-left: 0;
    padding-top: 16px;
    margin-top: 16px;
  }
}
</style>

