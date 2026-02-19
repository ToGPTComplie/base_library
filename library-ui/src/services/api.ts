import axios from 'axios';
import { v4 as uuidv4 } from 'uuid';

// 获取或生成设备ID
const getDeviceId = () => {
    let deviceId = localStorage.getItem('device_id');
    if (!deviceId) {
        deviceId = uuidv4();
        localStorage.setItem('device_id', deviceId);
    }
    return deviceId;
};

const api = axios.create({
    baseURL: '/api',
    headers: {
        'Content-Type': 'application/json',
        'X-Device-Id': getDeviceId(),
    },
});

// 添加请求拦截器，自动携带 Token
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('access_token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 添加响应拦截器，处理 Token 刷新
api.interceptors.response.use(
    (response) => {
        return response;
    },
    async (error) => {
        const originalRequest = error.config;

        // 如果是 401 错误，且不是刷新 Token 本身的请求，则尝试刷新
        if (error.response?.status === 401 && !originalRequest._retry && !originalRequest.url.includes('/auth/refresh-token')) {
            originalRequest._retry = true;

            try {
                const refreshToken = localStorage.getItem('refresh_token');
                if (!refreshToken) {
                    throw new Error('No refresh token available');
                }

                // 调用刷新 Token 接口
                const response = await api.post('/auth/refresh-token', {
                    refreshToken: refreshToken,
                });

                const { accessToken, refreshToken: newRefreshToken } = response.data.data;

                // 更新本地存储
                localStorage.setItem('access_token', accessToken);
                localStorage.setItem('refresh_token', newRefreshToken);

                // 更新请求头并重试原请求
                api.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
                originalRequest.headers['Authorization'] = `Bearer ${accessToken}`;
                
                return api(originalRequest);
            } catch (refreshError) {
                // 刷新失败，清除 Token 并跳转登录
                localStorage.removeItem('access_token');
                localStorage.removeItem('refresh_token');
                window.location.href = '/login';
                return Promise.reject(refreshError);
            }
        }

        return Promise.reject(error);
    }
);

export default api;
