<template>
  <div class="tenant-page card">
    <div class="section-title">租客管理</div>

    <div class="tenant-toolbar">
      <input
        v-model="keyword"
        type="text"
        class="search-input"
        placeholder="按姓名或手机号搜索"
        @keyup.enter="reload"
      />
      <button class="primary-button" @click="startCreate">新增租客</button>
    </div>

    <div class="tenant-content">
      <div class="tenant-table-wrapper">
        <table class="tenant-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>姓名</th>
              <th>手机号</th>
              <th>微信号</th>
              <th>备注</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="7" class="table-empty">加载中...</td>
            </tr>
            <tr v-else-if="tenants.length === 0">
              <td colspan="7" class="table-empty">暂无数据</td>
            </tr>
            <tr v-else v-for="item in tenants" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.fullName }}</td>
              <td>{{ item.mobileNumber }}</td>
              <td>{{ item.wechatId || '-' }}</td>
              <td>{{ item.remarkText || '-' }}</td>
              <td>{{ item.createdAt }}</td>
              <td>
                <button class="link-button" @click="startEdit(item)">编辑</button>
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

      <div class="tenant-form-wrapper">
        <h3 class="form-title">{{ editingId ? '编辑租客' : '新增租客' }}</h3>
        <form class="tenant-form" @submit.prevent="submit">
          <label class="form-item">
            <span>姓名</span>
            <input v-model="form.fullName" type="text" required maxlength="64" />
          </label>
          <label class="form-item">
            <span>手机号</span>
            <input v-model="form.mobileNumber" type="text" required maxlength="32" />
          </label>
          <label class="form-item">
            <span>微信号</span>
            <input v-model="form.wechatId" type="text" maxlength="64" />
          </label>
          <label class="form-item">
            <span>备注</span>
            <textarea v-model="form.remarkText" maxlength="255" rows="3" />
          </label>
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
import {
  createTenant,
  updateTenant,
  pageTenants,
  type TenantDetail,
} from '@/api';

const tenants = ref<TenantDetail[]>([]);
const loading = ref(false);
const keyword = ref('');
const pageNo = ref(1);
const pageSize = ref(10);
const total = ref(0);

const editingId = ref<number | null>(null);

const form = reactive({
  fullName: '',
  mobileNumber: '',
  wechatId: '',
  remarkText: '',
});

const totalPages = computed(() => {
  return total.value === 0 ? 1 : Math.ceil(total.value / pageSize.value);
});

async function reload() {
  loading.value = true;
  try {
    const res = await pageTenants({
      keyword: keyword.value || undefined,
      pageNo: pageNo.value,
      pageSize: pageSize.value,
    });
    tenants.value = res.records || [];
    total.value = res.total || 0;
  } finally {
    loading.value = false;
  }
}

function changePage(nextPage: number) {
  if (nextPage < 1) return;
  pageNo.value = nextPage;
  reload();
}

function startCreate() {
  editingId.value = null;
  resetForm();
}

function startEdit(item: TenantDetail) {
  editingId.value = item.id;
  form.fullName = item.fullName;
  form.mobileNumber = item.mobileNumber;
  form.wechatId = item.wechatId || '';
  form.remarkText = item.remarkText || '';
}

async function submit() {
  const payload = {
    fullName: form.fullName.trim(),
    mobileNumber: form.mobileNumber.trim(),
    wechatId: form.wechatId.trim() || undefined,
    remarkText: form.remarkText.trim() || undefined,
  };

  if (!payload.fullName || !payload.mobileNumber) {
    alert('姓名和手机号不能为空');
    return;
  }

  if (editingId.value) {
    await updateTenant(editingId.value, payload);
  } else {
    await createTenant(payload);
  }

  await reload();
}

function resetForm() {
  form.fullName = '';
  form.mobileNumber = '';
  form.wechatId = '';
  form.remarkText = '';
}

onMounted(() => {
  reload();
});
</script>

<style scoped>
.tenant-page {
  margin-top: 24px;
}

.tenant-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  gap: 12px;
}

.search-input {
  flex: 1;
  max-width: 320px;
  padding: 8px 10px;
  border-radius: 6px;
  border: 1px solid var(--color-border);
}

.tenant-content {
  display: grid;
  grid-template-columns: 2fr 1.1fr;
  gap: 20px;
}

.tenant-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.tenant-table th,
.tenant-table td {
  padding: 8px 10px;
  border-bottom: 1px solid var(--color-border);
  text-align: left;
}

.tenant-table th {
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

.link-button {
  border: none;
  background: none;
  color: var(--color-primary);
  cursor: pointer;
  padding: 0;
}

.tenant-form-wrapper {
  border-left: 1px solid var(--color-border);
  padding-left: 20px;
}

.form-title {
  margin-top: 0;
  margin-bottom: 12px;
}

.tenant-form {
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
.form-item textarea {
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
  .tenant-content {
    grid-template-columns: 1fr;
  }

  .tenant-form-wrapper {
    border-left: none;
    border-top: 1px solid var(--color-border);
    padding-left: 0;
    padding-top: 16px;
    margin-top: 16px;
  }
}
</style>

