import axios from 'axios';

// Electron 打包后无代理，需直连后端；浏览器开发时用 /api 走 Vite 代理
const baseURL =
  import.meta.env.VITE_API_ORIGIN != null && import.meta.env.VITE_API_ORIGIN !== ''
    ? `${import.meta.env.VITE_API_ORIGIN}/api`
    : '/api';

const http = axios.create({
  baseURL,
  timeout: 15000
});

http.interceptors.response.use(
  (response) => response.data,
  (error) => {
    // 这里后续可以统一处理错误提示
    return Promise.reject(error);
  }
);

export default http;

