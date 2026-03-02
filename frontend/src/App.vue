<template>
  <div class="app-root">
    <header class="app-header">
      <h1>房屋租赁管理后台</h1>
      <p class="app-subtitle">水电表 · 房屋 · 租客 · 账单一体化管理</p>
    </header>
    <main class="app-main">
      <section class="app-card">
        <h2>快速入口</h2>
        <div class="quick-links">
          <button
            class="primary-button"
            :class="{ active: currentView === 'tenant' }"
            @click="currentView = 'tenant'"
          >
            租客管理
          </button>
          <button
            class="primary-button"
            :class="{ active: currentView === 'shared' }"
            @click="currentView = 'shared'"
          >
            公共场所管理
          </button>
          <button
            class="primary-button"
            :class="{ active: currentView === 'price' }"
            @click="currentView = 'price'"
          >
            水电单价配置
          </button>
          <button
            class="primary-button"
            :class="{ active: currentView === 'meter' }"
            @click="currentView = 'meter'"
          >
            水电表管理
          </button>
          <button
            class="primary-button"
            :class="{ active: currentView === 'house' }"
            @click="currentView = 'house'"
          >
            房屋管理
          </button>
          <button
            class="primary-button"
            :class="{ active: currentView === 'bill' }"
            @click="currentView = 'bill'"
          >
            账单中心
          </button>
        </div>
        <p class="tip-text">
          请选择上方入口，单独操作对应功能模块。当前将逐步接入
          <strong>art-design-pro</strong> 组件库，构建统一的表格、表单与分析视图。
        </p>
      </section>

      <section class="app-content card">
        <TenantList v-if="currentView === 'tenant'" />
        <SharedPlaceList v-else-if="currentView === 'shared'" />
        <PriceConfigList v-else-if="currentView === 'price'" />
        <MeterList v-else-if="currentView === 'meter'" />
        <HouseList v-else-if="currentView === 'house'" />
        <BillCenter v-else-if="currentView === 'bill'" />
      </section>
    </main>
    <AgentationWrapper />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import TenantList from './views/tenant/TenantList.vue';
import AgentationWrapper from './components/AgentationWrapper.vue';
import SharedPlaceList from './views/sharedplace/SharedPlaceList.vue';
import PriceConfigList from './views/price/PriceConfigList.vue';
import MeterList from './views/meter/MeterList.vue';
import HouseList from './views/house/HouseList.vue';
import BillCenter from './views/bill/BillCenter.vue';

type ViewKey = 'tenant' | 'shared' | 'price' | 'meter' | 'house' | 'bill';

const currentView = ref<ViewKey>('tenant');
</script>

<style scoped>
.app-root {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  padding: 24px 32px;
  background: linear-gradient(135deg, #1677ff, #52c41a);
  color: #fff;
}

.app-header h1 {
  margin: 0;
  font-size: 24px;
}

.app-subtitle {
  margin-top: 8px;
  opacity: 0.9;
}

.app-main {
  flex: 1;
  padding: 24px 32px;
  background-color: #f5f5f5;
}

.app-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.app-card h2 {
  margin-top: 0;
}

.quick-links {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 12px;
  margin: 16px 0 8px;
}

.primary-button.active {
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.7),
    0 2px 6px rgba(0, 0, 0, 0.18);
}

.tip-text {
  margin-top: 16px;
  font-size: 13px;
  color: #666;
}

.app-content {
  margin-top: 24px;
}
</style>

