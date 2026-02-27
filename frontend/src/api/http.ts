import axios from 'axios';

const http = axios.create({
  baseURL: '/api',
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

