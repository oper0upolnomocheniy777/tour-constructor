import axios from 'axios';
import { AuthResponse, LoginCredentials, RegisterData, User } from '../types';
import { mockAuthApi } from './mockApi';

// Переключатель: true = использовать мок, false = реальный API
const USE_MOCK_API = true; // Временно включаем мок для тестирования

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error);
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Используем мок или реальный API
export const authApi = {
  login: async (credentials: LoginCredentials) => {
    if (USE_MOCK_API) {
      return mockAuthApi.login(credentials);
    }
    return api.post<AuthResponse>('/auth/login', credentials);
  },
  
  register: async (data: RegisterData) => {
    if (USE_MOCK_API) {
      return mockAuthApi.register(data);
    }
    return api.post<AuthResponse>('/auth/register', data);
  },
  
  logout: async () => {
    if (USE_MOCK_API) {
      return mockAuthApi.logout();
    }
    return api.post('/auth/logout');
  },
  
  getCurrentUser: async () => {
    if (USE_MOCK_API) {
      return mockAuthApi.getCurrentUser();
    }
    return api.get<User>('/auth/me');
  }
};

export const toursApi = {
  getAll: () => api.get('/tours'),
  getById: (id: number) => api.get(`/tours/${id}`),
  create: (data: any) => api.post('/tours', data),
  update: (id: number, data: any) => api.put(`/tours/${id}`, data),
  delete: (id: number) => api.delete(`/tours/${id}`)
};

export default api;