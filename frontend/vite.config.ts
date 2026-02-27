import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig(({ mode }) => ({
  plugins: [vue()],
  // Electron 用 file 协议加载，资源需相对路径；浏览器部署可用 /
  base: mode === 'electron' ? './' : '/',
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  resolve: {
    alias: {
      '@': '/src'
    }
  }
}));

