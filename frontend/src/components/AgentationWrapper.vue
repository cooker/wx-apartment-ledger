<template>
  <div v-if="isDev" ref="containerRef" class="agentation-wrapper" aria-hidden="true" />
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';

const isDev = import.meta.env.DEV;
const containerRef = ref<HTMLElement | null>(null);
let unmount: (() => void) | null = null;

onMounted(async () => {
  if (!isDev || !containerRef.value) return;
  const { mountAgentation } = await import('../agentation-root');
  unmount = mountAgentation(containerRef.value);
});

onUnmounted(() => {
  unmount?.();
  unmount = null;
});
</script>

<style scoped>
.agentation-wrapper {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 9999;
}
.agentation-wrapper > * {
  pointer-events: auto;
}
</style>
